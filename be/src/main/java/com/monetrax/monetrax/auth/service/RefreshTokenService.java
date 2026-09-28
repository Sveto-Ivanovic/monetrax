package com.monetrax.monetrax.auth.service;

import com.monetrax.monetrax.auth.entity.RefreshTokenEntity;
import com.monetrax.monetrax.auth.exceptions.RefreshTokenAuthenticationException;
import com.monetrax.monetrax.auth.repository.RefreshTokenRepository;
import com.monetrax.monetrax.user.entity.UserEntity;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HexFormat;
import java.util.Optional;

@Slf4j
@Service
public class RefreshTokenService {

    @Value("${jwt.refreshExpirationMs}")
    private Long refreshTokenDurationMs;

    @Value("${jwt.refreshTokenLength}")
    private Long refreshTokenLength;

    private final RefreshTokenRepository refreshTokenRepository;

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    static public class RefreshResponse {
        UserEntity user;
        String refreshToken;
    }

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public String hashRefreshToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            log.error("SHA-256 algorithm not available for refresh token hashing", e);
            throw new RuntimeException(e);
        }
    }

    public String generateRandomAlphaNumericalString(long lengthOfTxt) {
        String alphabet = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        StringBuilder reqString = new StringBuilder();
        SecureRandom random = new SecureRandom();

        for (int i = 0; i < lengthOfTxt; i++) {
            int index = random.nextInt(alphabet.length());
            reqString.append(alphabet.charAt(index));
        }
        return reqString.toString();
    }

    public String createRefreshToken(UserEntity user, String ipAddress, String userAgent, RefreshTokenEntity pastToken) {
        log.debug("Creating refresh token for userId={}, ip={}, rotation={}", user.getUserId(), ipAddress, pastToken != null);

        String token = generateRandomAlphaNumericalString(refreshTokenLength);
        String tokenHash = hashRefreshToken(token);

        var tokenEntity = RefreshTokenEntity.builder()
                .user(user)
                .expiresAt(OffsetDateTime.now(ZoneOffset.UTC).plusSeconds(refreshTokenDurationMs / 1000))
                .issuedAt(OffsetDateTime.now(ZoneOffset.UTC))
                .replacedByToken(null)
                .ipAddress(ipAddress)
                .revokedAt(null)
                .tokenHash(tokenHash)
                .userAgent(userAgent)
                .build();
        var savedNewTokenEntity = refreshTokenRepository.save(tokenEntity);
        log.info("Refresh token issued for userId={}, tokenId={}, ip={}", user.getUserId(), savedNewTokenEntity.getRefreshTokenId(), ipAddress);

        if (pastToken != null) {
            pastToken.setReplacedByToken(savedNewTokenEntity);
            pastToken.setRevokedAt(OffsetDateTime.now(ZoneOffset.UTC));
            refreshTokenRepository.save(pastToken);
            log.info("Refresh token rotated: oldTokenId={} replaced by newTokenId={} for userId={}",
                    pastToken.getRefreshTokenId(), savedNewTokenEntity.getRefreshTokenId(), user.getUserId());
        }

        return token;
    }

    public RefreshTokenEntity isTokenValid(String token) {
        String tokenHash = hashRefreshToken(token);

        Optional<RefreshTokenEntity> tokenEntity = refreshTokenRepository.getTokenEntityBasedOnTokenHash(tokenHash, OffsetDateTime.now(ZoneOffset.UTC));
        if (tokenEntity.isEmpty()) {
            log.warn("Refresh token validation failed: token not found, expired or revoked");
            throw new RefreshTokenAuthenticationException("Refresh token is invalid");
        }

        log.debug("Refresh token valid for tokenId={}", tokenEntity.get().getRefreshTokenId());
        return tokenEntity.get();
    }

    public void negateRefreshToken(String token) {
        String tokenHash = hashRefreshToken(token);

        RefreshTokenEntity tokenEntity = refreshTokenRepository.getTokenEntityBasedOnTokenHash(tokenHash, OffsetDateTime.now(ZoneOffset.UTC))
                .orElseThrow(() -> {
                    log.warn("Refresh token revocation failed: token missing, expired or revoked");
                    return new RefreshTokenAuthenticationException("Refresh token is missing, expired or revoked.");
                });

        if (tokenEntity.getRevokedAt() == null) {
            tokenEntity.setRevokedAt(OffsetDateTime.now(ZoneOffset.UTC));
            log.info("Refresh token revoked: tokenId={}", tokenEntity.getRefreshTokenId());
        } else {
            log.debug("Refresh token already revoked: tokenId={}", tokenEntity.getRefreshTokenId());
        }

        refreshTokenRepository.save(tokenEntity);
    }

    @Transactional
    public RefreshResponse refreshToken(String token, String ipAddress, String userAgent) {
        log.debug("Refreshing token from ip={}", ipAddress);
        RefreshTokenEntity refreshTokenEntity = isTokenValid(token);

        return new RefreshResponse(refreshTokenEntity.getUser(), createRefreshToken(refreshTokenEntity.getUser(), ipAddress, userAgent, refreshTokenEntity));
    }
}
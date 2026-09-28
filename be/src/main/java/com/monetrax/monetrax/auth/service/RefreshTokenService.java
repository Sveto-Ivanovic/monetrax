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
    static public class RefreshResponse{
        UserEntity user;
        String refreshToken;
    }

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;

    }

    public String hashRefreshToken(String token){
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public String generateRandomAlphaNumericalString(long lengthOfTxt){
        String alphabet = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        StringBuilder reqString = new StringBuilder();
        SecureRandom random = new SecureRandom();

        for (int i = 0; i < lengthOfTxt; i++) {
            int index = random.nextInt(alphabet.length());
            reqString.append(alphabet.charAt(index));
        }
      return  reqString.toString();
    }


    public String createRefreshToken(UserEntity user, String ipAddress, String userAgent, RefreshTokenEntity pastToken) {

        String token = generateRandomAlphaNumericalString(refreshTokenLength);
        String tokenHash = hashRefreshToken(token);

        var tokenEntity = RefreshTokenEntity.builder()
                .user(user)
                .expiresAt(OffsetDateTime.now(ZoneOffset.UTC).plusSeconds(refreshTokenDurationMs/1000))
                .issuedAt(OffsetDateTime.now(ZoneOffset.UTC))
                .replacedByToken(null)
                .ipAddress(ipAddress)
                .revokedAt(null)
                .tokenHash(tokenHash)
                .userAgent(userAgent)
                .build();
        var savedNewTokenEntity = refreshTokenRepository.save(tokenEntity);

        if(pastToken != null){
            pastToken.setReplacedByToken(savedNewTokenEntity);
            pastToken.setRevokedAt(OffsetDateTime.now(ZoneOffset.UTC));
            refreshTokenRepository.save(pastToken);
        }

        return token;
    }

    public RefreshTokenEntity isTokenValid(String token) {

        String tokenHash = hashRefreshToken(token);

        Optional<RefreshTokenEntity> tokenEntity = refreshTokenRepository.getTokenEntityBasedOnTokenHash(tokenHash, OffsetDateTime.now(ZoneOffset.UTC));
        if(tokenEntity.isEmpty())
            throw new RefreshTokenAuthenticationException("Refresh token is invalid");

        return tokenEntity.get();
    }

    public void negateRefreshToken(String token) {

        String tokenHash = hashRefreshToken(token);

        RefreshTokenEntity tokenEntity = refreshTokenRepository.getTokenEntityBasedOnTokenHash(tokenHash, OffsetDateTime.now(ZoneOffset.UTC)).orElseThrow(()->new RefreshTokenAuthenticationException("Refresh token is missing, expired or revoked."));

        if(tokenEntity.getRevokedAt()==null)
            tokenEntity.setRevokedAt(OffsetDateTime.now(ZoneOffset.UTC));

        refreshTokenRepository.save(tokenEntity);
    }

    @Transactional
    public RefreshResponse refreshToken(String token, String ipAddress, String userAgent){
        RefreshTokenEntity refreshTokenEntity = isTokenValid(token);

        return new RefreshResponse(refreshTokenEntity.getUser(), createRefreshToken(refreshTokenEntity.getUser(), ipAddress, userAgent, refreshTokenEntity));
    }
}

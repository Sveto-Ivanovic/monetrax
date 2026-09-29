package com.monetrax.monetrax.auth.controller;

import com.monetrax.monetrax.auth.dto.AuthRequest;
import com.monetrax.monetrax.auth.dto.AuthResponse;
import com.monetrax.monetrax.auth.service.AuthService;
import com.monetrax.monetrax.auth.service.JwtService;
import com.monetrax.monetrax.auth.service.RefreshTokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.SameSiteCookies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private JwtService jwtService;

    @Value("${jwt.refreshExpirationMs}")
    private int refreshTokenDurationMs;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticateAndGetToken(@Valid @RequestBody AuthRequest authInfo, HttpServletRequest httpRequest, HttpServletResponse response) {
        String ipAddress = httpRequest.getRemoteAddr();
        String userAgent = httpRequest.getHeader("User-Agent");
        log.info("Endpoint called: POST /auth/login [ip={}]", ipAddress);
        log.debug("POST /auth/login [ip={}, userAgent={}]", ipAddress, userAgent);

        try {
            AuthService.LoginResponse loginResponse = authService.login(authInfo);
            String refreshToken = refreshTokenService.createRefreshToken(loginResponse.getUser(), ipAddress, userAgent, null);
            log.debug("POST /auth/login refresh token created [userId={}]", loginResponse.getUser().getUserId());

            ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                    .httpOnly(true)
                    .maxAge(refreshTokenDurationMs / 1000)
                    .sameSite(SameSiteCookies.STRICT.toString())
                    .secure(true)
                    .path("/auth/token")
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

            log.info("POST /auth/login succeeded [userId={}, ip={}]", loginResponse.getUser().getUserId(), ipAddress);
            return ResponseEntity.ok(loginResponse.getAuthResponse());
        } catch (Exception e) {
            log.warn("POST /auth/login failed [ip={}]: {}", ipAddress, e.getMessage());
            throw e;
        }
    }

    @PostMapping("/token/refresh")
    public ResponseEntity<AuthResponse> refreshTokens(@CookieValue(value = "refreshToken", required = true) String refreshToken, HttpServletRequest httpRequest, HttpServletResponse response) {
        String ipAddress = httpRequest.getRemoteAddr();
        String userAgent = httpRequest.getHeader("User-Agent");
        log.info("Endpoint called: POST /auth/token/refresh [ip={}]", ipAddress);
        log.debug("POST /auth/token/refresh [ip={}, userAgent={}]", ipAddress, userAgent);

        try {
            var res = refreshTokenService.refreshToken(refreshToken, ipAddress, userAgent);
            String jwtToken = jwtService.generateToken(res.getUser().getUserEmail(), res.getUser().getUserId());

            ResponseCookie cookie = ResponseCookie.from("refreshToken", res.getRefreshToken())
                    .httpOnly(true)
                    .maxAge(refreshTokenDurationMs / 1000)
                    .sameSite(SameSiteCookies.STRICT.toString())
                    .secure(true)
                    .path("/auth/token")
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

            log.info("POST /auth/token/refresh succeeded [userId={}, ip={}]", res.getUser().getUserId(), ipAddress);
            return ResponseEntity.ok(AuthResponse.builder()
                    .authToken(jwtToken)
                    .userId(res.getUser().getUserId().toString())
                    .build());
        } catch (Exception e) {
            log.warn("POST /auth/token/refresh failed [ip={}]: {}", ipAddress, e.getMessage());
            throw e;
        }
    }

    @PostMapping("/token/logout")
    public ResponseEntity<String> logOut(@CookieValue(value = "refreshToken", required = true) String refreshToken, HttpServletRequest httpRequest, HttpServletResponse response) {
        String ipAddress = httpRequest.getRemoteAddr();
        log.info("Endpoint called: POST /auth/token/logout [ip={}]", ipAddress);

        try {
            refreshTokenService.negateRefreshToken(refreshToken);

            Cookie cookie = new Cookie("refreshToken", "");
            cookie.setMaxAge(0);
            cookie.setPath("/auth/token");
            response.addCookie(cookie);

            log.info("POST /auth/token/logout succeeded [ip={}]", ipAddress);
            return ResponseEntity.ok("Successfully logged out.");
        } catch (Exception e) {
            log.warn("POST /auth/token/logout failed [ip={}]: {}", ipAddress, e.getMessage());
            throw e;
        }
    }

    @GetMapping("/health-check")
    public ResponseEntity<String> healthCheck() {
        log.debug("Endpoint called: GET /auth/health-check");
        return ResponseEntity.ok("Health test: ok.");
    }
}
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
        log.info("Login attempt from ip={}", ipAddress);

        try {
            AuthService.LoginResponse loginResponse = authService.login(authInfo);
            String refreshToken = refreshTokenService.createRefreshToken(loginResponse.getUser(), ipAddress, userAgent, null);
            log.debug("Refresh token created for userId={}", loginResponse.getUser().getUserId());

            ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                    .httpOnly(true)
                    .maxAge(refreshTokenDurationMs / 1000)
                    .sameSite(SameSiteCookies.STRICT.toString())
                    .secure(true)
                    .path("/auth/token")
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

            log.info("Login successful for userId={}, ip={}", loginResponse.getUser().getUserId(), ipAddress);
            return ResponseEntity.ok(loginResponse.getAuthResponse());
        } catch (Exception e) {
            log.warn("Login failed from ip={}: {}", ipAddress, e.getMessage());
            throw e;
        }
    }

    @PostMapping("/token/refresh")
    public ResponseEntity<AuthResponse> refreshTokens(@CookieValue(value = "refreshToken", required = true) String refreshToken, HttpServletRequest httpRequest, HttpServletResponse response) {
        String ipAddress = httpRequest.getRemoteAddr();
        String userAgent = httpRequest.getHeader("User-Agent");
        log.info("Token refresh requested from ip={}", ipAddress);

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

            log.info("Token refresh successful for userId={}, ip={}", res.getUser().getUserId(), ipAddress);
            return ResponseEntity.ok(AuthResponse.builder()
                    .authToken(jwtToken)
                    .userId(res.getUser().getUserId().toString())
                    .build());
        } catch (Exception e) {
            log.warn("Token refresh failed from ip={}: {}", ipAddress, e.getMessage());
            throw e;
        }
    }

    @PostMapping("/token/logout")
    public ResponseEntity<String> logOut(@CookieValue(value = "refreshToken", required = true) String refreshToken, HttpServletRequest httpRequest, HttpServletResponse response) {
        log.info("Logout requested from ip={}", httpRequest.getRemoteAddr());

        refreshTokenService.negateRefreshToken(refreshToken);

        Cookie cookie = new Cookie("refreshToken", "");
        cookie.setMaxAge(0);
        cookie.setPath("/auth/token");
        response.addCookie(cookie);

        log.info("Logout successful, refresh token invalidated");
        return ResponseEntity.ok("Successfully logged out.");
    }

    @GetMapping("/health-check")
    public ResponseEntity<String> authenticateAndGetToken() {
        log.debug("Health check called");
        return ResponseEntity.ok("Health test: ok.");
    }
}
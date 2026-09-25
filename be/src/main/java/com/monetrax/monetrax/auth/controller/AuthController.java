package com.monetrax.monetrax.auth.controller;


import com.monetrax.monetrax.auth.dto.AuthRequest;
import com.monetrax.monetrax.auth.dto.AuthResponse;
import com.monetrax.monetrax.auth.dto.LogoutRequest;
import com.monetrax.monetrax.auth.service.AuthService;
import com.monetrax.monetrax.auth.service.JwtService;
import com.monetrax.monetrax.auth.service.RefreshTokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.apache.tomcat.util.http.SameSiteCookies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        public ResponseEntity<AuthResponse> authenticateAndGetToken(@Valid @RequestBody AuthRequest authInfo, HttpServletRequest httpRequest, HttpServletResponse response){
            AuthService.LoginResponse loginResponse = authService.login(authInfo);
            String userAgent = httpRequest.getHeader("User-Agent");
            String ipAddress = httpRequest.getRemoteAddr();
            String refreshToken = refreshTokenService.createRefreshToken(loginResponse.getUser(), ipAddress, userAgent, null);

            ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                    .httpOnly(true)
                    .maxAge(refreshTokenDurationMs/1000)
                    .sameSite(SameSiteCookies.STRICT.toString())
                    .secure(true)
                    .path("/auth")
                    .build();


            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

            return ResponseEntity.ok(loginResponse.getAuthResponse());
        }

        @PostMapping("/refresh")
        public ResponseEntity<AuthResponse> refreshTokens(@CookieValue(value = "refreshToken", required = true) String refreshToken, HttpServletRequest httpRequest, HttpServletResponse response){

            String userAgent = httpRequest.getHeader("User-Agent");
            String ipAddress = httpRequest.getRemoteAddr();
            var res = refreshTokenService.refreshToken(refreshToken, ipAddress, userAgent);
            String jwtToken = jwtService.generateToken(res.getUser().getUserEmail(), res.getUser().getUserId());

            ResponseCookie cookie = ResponseCookie.from("refreshToken", res.getRefreshToken())
                    .httpOnly(true)
                    .maxAge(refreshTokenDurationMs/1000)
                    .sameSite(SameSiteCookies.STRICT.toString())
                    .secure(true)
                    .path("/auth")
                    .build();


            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

            return ResponseEntity.ok(AuthResponse.builder()
                    .authToken(jwtToken)
                    .userId(res.getUser().getUserId().toString())
                    .build());
        }

    @PostMapping("/logout")
    public ResponseEntity<String> logOut(@CookieValue(value = "refreshToken", required = true) String refreshToken, HttpServletRequest httpRequest, HttpServletResponse response){

        refreshTokenService.negateRefreshToken(refreshToken);

        Cookie cookie = new Cookie("refreshToken", "");
        cookie.setMaxAge(0);
        cookie.setPath("/auth");
        response.addCookie(cookie);

        return ResponseEntity.ok("Successfully logged out.");
    }



        @GetMapping("/health-check")
        public ResponseEntity<String> authenticateAndGetToken() {
            return ResponseEntity.ok("Health test: ok.");
        }
    }

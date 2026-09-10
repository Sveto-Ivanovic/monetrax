package com.monetrax.monetrax.auth.service;

import com.monetrax.monetrax.auth.dto.AuthRequest;
import com.monetrax.monetrax.auth.dto.AuthResponse;
import com.monetrax.monetrax.auth.security.CustomUserDetails;
import com.monetrax.monetrax.user.mapper.UserMapper;
import com.monetrax.monetrax.user.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService( AuthenticationManager authenticationManager, JwtService jwtService){
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;

    }

    public AuthResponse login(AuthRequest authRequest){
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword());
        Authentication authentication = authenticationManager.authenticate(token);
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        assert customUserDetails != null;
        String authToken = jwtService.generateToken(customUserDetails.getUsername(), UUID.fromString(customUserDetails.getUserId()));
        return AuthResponse.builder()
                .authToken(authToken)
                .userId(customUserDetails.getUserId())
                .build();
    }

}

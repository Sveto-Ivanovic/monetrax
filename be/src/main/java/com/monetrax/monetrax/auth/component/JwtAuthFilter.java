package com.monetrax.monetrax.auth.component;

import com.monetrax.monetrax.auth.dto.AuthInfo;
import com.monetrax.monetrax.auth.security.CustomUserDetails;
import com.monetrax.monetrax.auth.service.AuthService;
import com.monetrax.monetrax.auth.service.CustomUserDetailsService;
import com.monetrax.monetrax.auth.service.JwtService;
import com.monetrax.monetrax.common.exception.shared.UnauthorizedAccess;
import com.monetrax.monetrax.user.entity.UserEntity;
import com.monetrax.monetrax.user.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;


@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver handlerExceptionResolver;

    public JwtAuthFilter(JwtService jwtService, CustomUserDetailsService userDetailsService){
        this.jwtService=jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) {
        try {
            String authorizationToken = request.getHeader("Authorization");
            if (authorizationToken == null || !authorizationToken.startsWith("Bearer")) {
                filterChain.doFilter(request, response);
                return;
            }

            String token = authorizationToken.substring(7);

            AuthInfo authInfo = jwtService.getAuthInfoFromToken(token);

            if (authInfo.getUserId() != null &&
                    authInfo.getEmail() != null &&
                    authInfo.getType().equals("access") &&
                    SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails userDetails = userDetailsService.loadUserByUsername(authInfo.getEmail());
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }

            filterChain.doFilter(request, response);
        }
        catch(Exception e){
            handlerExceptionResolver.resolveException(request, response, null, e);
        }
    }
}

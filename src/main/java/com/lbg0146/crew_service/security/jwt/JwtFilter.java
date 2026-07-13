package com.lbg0146.crew_service.security.jwt;

import com.lbg0146.crew_service.exception.InvalidTokenException;
import com.lbg0146.crew_service.security.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;
    private final AuthenticationEntryPoint authenticationEntryPoint;

    public JwtFilter(JwtTokenProvider jwtTokenProvider, CustomUserDetailsService customUserDetailsService, AuthenticationEntryPoint authenticationEntryPoint) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.customUserDetailsService = customUserDetailsService;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);

            return;
        }

        String token = authorization.substring(7);

        try {
            if (jwtTokenProvider.isExpired(token)) {
                throw new InvalidTokenException("토큰이 만료되었습니다.");
            }

            String category = jwtTokenProvider.getCategory(token);

            if (!"access".equals(category)) {
                throw new InvalidTokenException("Access Token이 아닙니다.");
            }

            String loginId = jwtTokenProvider.getLoginId(token);

            UserDetails userDetails = customUserDetailsService.loadUserByUsername(loginId);

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authToken);

        } catch (InvalidTokenException e) {
            authenticationEntryPoint.commence(request, response, e);
            return;
        }

        filterChain.doFilter(request, response);
    }
}

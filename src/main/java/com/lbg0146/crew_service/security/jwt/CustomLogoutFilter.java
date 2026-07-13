package com.lbg0146.crew_service.security.jwt;

import com.lbg0146.crew_service.security.CookieUtil;
import com.lbg0146.crew_service.security.RefreshTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@RequiredArgsConstructor
public class CustomLogoutFilter extends GenericFilterBean {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        if (!request.getRequestURI().equals("/logout")) {
            filterChain.doFilter(request, response);
            return;
        }

        if (!request.getMethod().equals("POST")) {
            filterChain.doFilter(request, response);
            return;
        }

        String refreshToken = CookieUtil.getRefreshToken(request);

        if (refreshToken == null) {
            //response status code
            throw new IllegalArgumentException("Refresh Token이 없습니다.");
        }

        if (jwtTokenProvider.isExpired(refreshToken)) {
            throw new IllegalArgumentException("Refresh Token이 만료되었습니다.");
        }

        if (!jwtTokenProvider.getCategory(refreshToken).equals("refresh")) {
            throw new IllegalArgumentException("Refresh Token이 아닙니다.");
        }

        String loginId = jwtTokenProvider.getLoginId(refreshToken);

        if (!refreshTokenService.isValid(loginId, refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 Refresh Token입니다.");
        }

        refreshTokenService.delete(loginId);

        Cookie cookie = CookieUtil.createLogoutCookie();


        response.addCookie(cookie);
        response.setStatus(HttpServletResponse.SC_OK);
    }
}

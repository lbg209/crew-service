package com.lbg0146.crew_service.service;

import com.lbg0146.crew_service.dto.TokenResponse;
import com.lbg0146.crew_service.security.RefreshTokenService;
import com.lbg0146.crew_service.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;

    public TokenResponse reissue(String refreshToken) {

        if (refreshToken == null) {
            throw new IllegalArgumentException("Refresh Token이 없습니다.");
        }

        if (jwtTokenProvider.isExpired(refreshToken)) {
            throw new IllegalArgumentException("Refresh Token이 만료되었습니다.");
        }

        if (!jwtTokenProvider.getCategory(refreshToken).equals("refresh")) {
            throw new IllegalArgumentException("Refresh Token이 아닙니다.");
        }

        String loginId = jwtTokenProvider.getLoginId(refreshToken);
        String role = jwtTokenProvider.getRole(refreshToken);

        if (!refreshTokenService.isValid(loginId, refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 Refresh Token입니다.");
        }

        String newAccess = jwtTokenProvider.createJwt("access", loginId, role, 30 * 60 * 1000L);
        String newRefresh = jwtTokenProvider.createJwt("refresh", loginId, role, 24 * 60 * 60 * 1000L);

        refreshTokenService.save(loginId, newRefresh, 24 * 60 * 60 * 1000L);

        return new TokenResponse(newAccess, newRefresh);
    }
}

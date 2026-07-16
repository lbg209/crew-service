package com.lbg0146.crew_service.controller;

import com.lbg0146.crew_service.dto.TokenResponse;
import com.lbg0146.crew_service.service.AuthService;
import com.lbg0146.crew_service.security.CookieUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth API", description = "인증 관련 API")
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/reissue")
    @Operation(
            summary = "Access Token 재발급",
            description = "Refresh Token을 이용하여 Access Token과 Refresh Token을 재발급합니다."
    )
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {

        // 쿠키 검증
        String refreshToken = CookieUtil.getRefreshToken(request);

        //
        TokenResponse tokenResponse = authService.reissue(refreshToken);

        response.addHeader("Authorization", "Bearer " + tokenResponse.getAccessToken());

        response.addCookie(CookieUtil.createCookie("refresh", tokenResponse.getRefreshToken()));

        return  ResponseEntity.ok().build();
    }
}

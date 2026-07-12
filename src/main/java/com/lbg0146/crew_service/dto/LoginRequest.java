package com.lbg0146.crew_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "로그인 요청 DTO")
public class LoginRequest {

    @Schema(description = "로그인 ID", example = "test123")
    @NotBlank
    private String loginId;
    @Schema(description = "비밀번호", example = "1234")
    @NotBlank
    private String password;
}

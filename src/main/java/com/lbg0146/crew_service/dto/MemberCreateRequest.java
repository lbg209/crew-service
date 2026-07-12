package com.lbg0146.crew_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "멤버 생성 요청 DTO")
public class MemberCreateRequest {

    @Schema(description = "멤버 로그인 ID", example = "kim1234")
    @NotNull
    private String loginId;

    @Schema(description = "멤버 패스워드", example = "1234")
    @NotBlank
    private String password;

    @Schema(description = "멤버 닉네임", example = "RONALDO")
    @NotBlank
    private String nickname;

    public MemberCreateRequest(String loginId, String password, String nickname) {
        this.loginId = loginId;
        this.password = password;
        this.nickname = nickname;
    }
}

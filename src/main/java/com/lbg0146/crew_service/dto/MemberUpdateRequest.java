package com.lbg0146.crew_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "멤버 닉네임 변경 요청 DTO")
public class MemberUpdateRequest {
    @Schema(description = "멤버 변경 닉네임", example = "MESSI")
    private String nickname;

    public MemberUpdateRequest(String nickname) {
        this.nickname = nickname;
    }
}

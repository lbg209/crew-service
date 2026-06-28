package com.lbg0146.crew_service.dto;

import com.lbg0146.crew_service.domain.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "멤버 조회 응답 DTO")
public class MemberResponse {
    @Schema(description = "멤버 ID", example = "1")
    private Long id;
    @Schema(description = "멤버 로그인 ID", example = "kim1234")
    private String loginId;
    @Schema(description = "멤버 닉네임", example = "RONALDO")
    private String nickname;

    public MemberResponse(Member member) {
        this.id = member.getId();
        this.loginId = member.getLoginId();
        this.nickname = member.getNickname();
    }
}

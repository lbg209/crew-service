package com.lbg0146.crew_service.dto;

import com.lbg0146.crew_service.domain.Member;
import lombok.Getter;

@Getter
public class MemberResponse {
    private Long id;
    private String loginId;
    private String nickname;

    public MemberResponse(Member member) {
        this.id = member.getId();
        this.loginId = member.getLoginId();
        this.nickname = member.getNickname();
    }
}

package com.lbg0146.crew_service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MemberUpdateRequest {
    private String nickname;

    public MemberUpdateRequest(String nickname) {
        this.nickname = nickname;
    }
}

package com.lbg0146.crew_service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MemberCrewApplicationRequest {

    private Long memberId;
    private Long crewId;

    public MemberCrewApplicationRequest(Long memberId, Long crewId) {
        this.memberId = memberId;
        this.crewId = crewId;
    }
}

package com.lbg0146.crew_service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MemberCrewApplicationUpdateRequest {

    private Long leaderId;
    private Long memberId;
    private Long crewId;

    public MemberCrewApplicationUpdateRequest(Long leaderId, Long memberId, Long crewId) {
        this.leaderId = leaderId;
        this.memberId = memberId;
        this.crewId = crewId;
    }
}

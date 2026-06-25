package com.lbg0146.crew_service.dto;

import com.lbg0146.crew_service.domain.enums.RecruitmentStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecruitmentStatusRequest {

    private Long memberId;
    private RecruitmentStatus status;



}

package com.lbg0146.crew_service.dto;

import com.lbg0146.crew_service.domain.MemberCrewApplication;
import com.lbg0146.crew_service.domain.enums.ApplicationStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
public class ApplicationResponse {

    private Long applicationId;

    private Long memberId;
    private String memberNickname;

    private Long crewId;
    private String crewTitle;

    private ApplicationStatus status;

    public ApplicationResponse(MemberCrewApplication application) {
        this.applicationId = application.getId();
        this.memberId = application.getMember().getId();
        this.memberNickname = application.getMember().getNickname();
        this.crewId = application.getCrew().getId();
        this.crewTitle = application.getCrew().getTitle();
        this.status = application.getStatus();
    }
}

package com.lbg0146.crew_service.dto;

import com.lbg0146.crew_service.domain.MemberCrewApplication;
import com.lbg0146.crew_service.domain.enums.ApplicationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Schema(description = "크루 신청 조회의 응답 DTO")
public class ApplicationResponse {

    @Schema(description = "크루 신청 ID", example = "4")
    private Long applicationId;

    @Schema(description = "크루 신청자 ID", example = "7")
    private Long memberId;
    @Schema(description = "크루 신청자 닉네임", example = "RONALDO")
    private String memberNickname;

    @Schema(description = "신청 크루 ID", example = "2")
    private Long crewId;
    @Schema(description = "신청 크루 이름", example = "Real Madrid")
    private String crewTitle;

    @Schema(description = "신청서 상태", example = "PENDING")
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

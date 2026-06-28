package com.lbg0146.crew_service.dto;

import com.lbg0146.crew_service.domain.enums.RecruitmentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "크루 모집 상태 변경 요청 DTO")
public class RecruitmentStatusRequest {

    @Schema(description = "크루장 검사용 ID", example = "4")
    private Long memberId;
    @Schema(description = "크루 상태 변경 값", example = "RECRUITING", allowableValues = {"RECRUITING", "CLOSED"})
    private RecruitmentStatus status;
}

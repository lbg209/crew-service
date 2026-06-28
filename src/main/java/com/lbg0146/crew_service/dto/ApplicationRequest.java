package com.lbg0146.crew_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "크루 신청 요청 DTO")
public class ApplicationRequest {

    @Schema(description = "신청하는 회원 ID", example = "10")
    private Long memberId;
    @Schema(description = "신청할 크루 ID", example = "3")
    private Long crewId;

    public ApplicationRequest(Long memberId, Long crewId) {
        this.memberId = memberId;
        this.crewId = crewId;
    }
}

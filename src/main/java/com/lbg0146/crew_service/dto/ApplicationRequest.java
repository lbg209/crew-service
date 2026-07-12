package com.lbg0146.crew_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "크루 신청 요청 DTO")
public class ApplicationRequest {

    @Schema(description = "신청할 크루 ID", example = "3")
    @NotNull
    private Long crewId;

    public ApplicationRequest(Long crewId) {
        this.crewId = crewId;
    }
}

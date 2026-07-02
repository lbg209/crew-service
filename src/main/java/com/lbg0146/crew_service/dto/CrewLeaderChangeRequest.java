package com.lbg0146.crew_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "리더 변경 요청 DTO")
public class CrewLeaderChangeRequest {

    @Schema(description = "신규 리더 ID", example = "7")
    @NotNull
    private Long newLeaderId;
}

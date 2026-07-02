package com.lbg0146.crew_service.dto;

import com.lbg0146.crew_service.domain.enums.Region;
import com.lbg0146.crew_service.domain.enums.SubCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "크루 정보 변경 요청 DTO")
public class CrewUpdateRequest {

    @Schema(description = "크루 카테고리", example = "BASKETBALL", allowableValues = {"FOOTBALL", "BASKETBALL", "RUNNING"})
    @NotNull
    private SubCategory subCategory;

    @Schema(description = "크루 지역", example = "BUSAN", allowableValues = {"SEOUL", "BUSAN", "DAEJEON"})
    @NotNull
    private Region region;

    @Schema(description = "크루 이름", example = "동네 농구왕")
    @NotBlank
    private String title;

    @Schema(description = "크루 설명", example = "동네에서 농구 하실 분 구합니다.")
    @NotBlank
    private String description;

    @Min(1)
    @Schema(description = "크루 최대 인원", example = "10")
    private int maxMemberCount;

    public CrewUpdateRequest(SubCategory subCategory, Region region, String title, String description, int maxMemberCount) {
        this.subCategory = subCategory;
        this.region = region;
        this.title = title;
        this.description = description;
        this.maxMemberCount = maxMemberCount;
    }
}

package com.lbg0146.crew_service.dto;

import com.lbg0146.crew_service.domain.Crew;
import com.lbg0146.crew_service.domain.enums.Region;
import com.lbg0146.crew_service.domain.enums.SubCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Schema(description = "크루 조회 응답 DTO")
public class CrewResponse {

    @Schema(description = "크루 ID", example = "1")
    private Long id;
    @Schema(description = "크루 리더 ID", example = "7")
    private Long leaderId;
    @Schema(description = "크루 카테고리", example = "FOOTBALL", allowableValues = {"FOOTBALL", "BASKETBALL", "RUNNING"})
    private SubCategory subCategory;
    @Schema(description = "크루 지역", example = "SEOUL", allowableValues = {"SEOUL", "BUSAN", "DAEJEON"})
    private Region region;
    @Schema(description = "크루 이름", example = "동네 축구왕")
    private String title;
    @Schema(description = "크루 설명", example = "동네에서 축구 하실 분 구합니다.")
    private String description;
    @Schema(description = "크루 최대 인원", example = "10")
    private int maxMemberCount;
    @Schema(description = "크루 현재 인원", example = "1")
    private int currentMemberCount;

    public CrewResponse(Crew crew) {
        this.id = crew.getId();
        this.leaderId = crew.getLeader().getId();
        this.subCategory = crew.getSubCategory();
        this.region = crew.getRegion();
        this.title = crew.getTitle();
        this.description = crew.getDescription();
        this.maxMemberCount = crew.getMaxMemberCount();
        this.currentMemberCount = crew.getCurrentMemberCount();
    }
}

package com.lbg0146.crew_service.dto;

import com.lbg0146.crew_service.domain.enums.RecruitmentStatus;
import com.lbg0146.crew_service.domain.enums.Region;
import com.lbg0146.crew_service.domain.enums.SubCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "크루 조건 검색 조회 요청 DTO")
public class CrewSearchCondition {

    @Schema(description = "크루 지역 검색 조건", example = "null or Seoul", allowableValues = {"SEOUL", "BUSAN", "DAEJEON"})
    private Region region;
    @Schema(description = "크루 카테고리 검색 조건", example = "null or FOOTBALL", allowableValues = {"FOOTBALL", "BASKETBALL", "RUNNING"})
    private SubCategory subCategory;
    @Schema(description = "크루 이름 검색 조건", example = "축구")
    private String title;
    @Schema(description = "크루 상태 검색 조건", example = "RECRUITING", allowableValues = {"RECRUITING", "CLOSED"})
    private RecruitmentStatus recruitmentStatus;
}

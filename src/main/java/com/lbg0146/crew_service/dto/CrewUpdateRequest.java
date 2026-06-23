package com.lbg0146.crew_service.dto;

import com.lbg0146.crew_service.domain.enums.Region;
import com.lbg0146.crew_service.domain.enums.SubCategory;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CrewUpdateRequest {

    private Long memberId;

    private SubCategory subCategory;
    private Region region;
    private String title;
    private String description;
    private int maxMemberCount;

    public CrewUpdateRequest(Long memberId, SubCategory subCategory, Region region, String title, String description, int maxMemberCount) {
        this.memberId = memberId;
        this.subCategory = subCategory;
        this.region = region;
        this.title = title;
        this.description = description;
        this.maxMemberCount = maxMemberCount;
    }
}

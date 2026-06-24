package com.lbg0146.crew_service.dto;

import com.lbg0146.crew_service.domain.Crew;
import com.lbg0146.crew_service.domain.enums.Region;
import com.lbg0146.crew_service.domain.enums.SubCategory;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
public class CrewResponse {

    private Long id;
    private Long leaderId;
    private SubCategory subCategory;
    private Region region;
    private String title;
    private String description;
    private int maxMemberCount;
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

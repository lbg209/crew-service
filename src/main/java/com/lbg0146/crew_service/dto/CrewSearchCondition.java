package com.lbg0146.crew_service.dto;

import com.lbg0146.crew_service.domain.enums.Region;
import com.lbg0146.crew_service.domain.enums.SubCategory;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CrewSearchCondition {

    private Region region;
    private SubCategory subCategory;
    private String title;
}

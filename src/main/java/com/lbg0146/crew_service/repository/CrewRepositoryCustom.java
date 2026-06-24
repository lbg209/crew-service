package com.lbg0146.crew_service.repository;

import com.lbg0146.crew_service.domain.Crew;
import com.lbg0146.crew_service.dto.CrewSearchCondition;

import java.util.List;

public interface CrewRepositoryCustom {

    List<Crew> search(CrewSearchCondition condition);
}

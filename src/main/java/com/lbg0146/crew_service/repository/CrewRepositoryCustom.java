package com.lbg0146.crew_service.repository;

import com.lbg0146.crew_service.domain.Crew;
import com.lbg0146.crew_service.dto.CrewSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CrewRepositoryCustom {

    Page<Crew> search(CrewSearchCondition condition, Pageable pageable);
}

package com.lbg0146.crew_service.repository;

import com.lbg0146.crew_service.domain.Crew;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CrewRepository extends JpaRepository<Crew, Long>, CrewRepositoryCustom {
}

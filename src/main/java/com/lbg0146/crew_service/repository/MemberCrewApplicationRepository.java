package com.lbg0146.crew_service.repository;

import com.lbg0146.crew_service.domain.MemberCrewApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberCrewApplicationRepository extends JpaRepository<MemberCrewApplication, Long> {
    Optional<MemberCrewApplication> findByMemberIdAndCrewId(Long memberId, Long crewId);
}

package com.lbg0146.crew_service.repository;

import com.lbg0146.crew_service.domain.MemberCrewApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberCrewApplicationRepository extends JpaRepository<MemberCrewApplication, Long> {
    Optional<MemberCrewApplication> findByMemberIdAndCrewId(Long memberId, Long crewId);

    // N+1 발생
    @EntityGraph(attributePaths = {"member", "crew"}) // 중복 join 발생
    Page<MemberCrewApplication> findByCrewId(Long crewId, Pageable pageable);

    // N+1 발생
    @EntityGraph(attributePaths = {"member", "crew"}) // 중복 join 발생
    Page<MemberCrewApplication> findByMemberId(Long memberId, Pageable pageable);
}

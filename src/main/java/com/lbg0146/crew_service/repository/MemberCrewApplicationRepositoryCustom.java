package com.lbg0146.crew_service.repository;

import com.lbg0146.crew_service.domain.MemberCrewApplication;
import com.lbg0146.crew_service.domain.enums.ApplicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberCrewApplicationRepositoryCustom {

    Page<MemberCrewApplication> searchByCrew(Long crewId, ApplicationStatus status, Pageable pageable);

    Page<MemberCrewApplication> searchByMember(Long memberId, ApplicationStatus status, Pageable pageable);
}

package com.lbg0146.crew_service.service;

import com.lbg0146.crew_service.domain.Crew;
import com.lbg0146.crew_service.domain.Member;
import com.lbg0146.crew_service.domain.MemberCrewApplication;
import com.lbg0146.crew_service.domain.enums.ApplicationStatus;
import com.lbg0146.crew_service.domain.enums.RecruitmentStatus;
import com.lbg0146.crew_service.exception.*;
import com.lbg0146.crew_service.repository.CrewRepository;
import com.lbg0146.crew_service.repository.MemberCrewApplicationRepository;
import com.lbg0146.crew_service.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberCrewApplicationService {

    private final MemberRepository memberRepository;
    private final CrewRepository crewRepository;
    private final MemberCrewApplicationRepository applicationRepository;

    public Long apply(Long memberId, Long crewId) {
        if (applicationRepository.findByMemberIdAndCrewId(memberId, crewId).isPresent()){
            throw new DuplicateApplicationException();
        }

        Member member = memberRepository.findById(memberId).orElseThrow(()-> new MemberNotFoundException());
        Crew crew = crewRepository.findById(crewId).orElseThrow(()-> new CrewNotFoundException());

        if (crew.getRecruitmentStatus() == RecruitmentStatus.CLOSED) {
            throw new IllegalStateException("모집이 마감된 크루입니다.");
        }

        if (crew.getLeader().getId().equals(memberId)) {
            throw new UnauthorizedException("크루장은 신청할 수 없습니다.");
        }

        MemberCrewApplication application = MemberCrewApplication.create(member, crew);

        applicationRepository.save(application);

        return application.getId();
    }

    public void approve(Long applicationId, Long leaderId) {
        MemberCrewApplication application = applicationRepository.findById(applicationId).orElseThrow(() -> new ApplicationNotFoundException());

        Crew crew = application.getCrew();

        if (!crew.getLeader().getId().equals(leaderId)) {
            throw new UnauthorizedException("크루 리더만 승인 가능합니다.");
        }

        application.approve();
        crew.increaseMemberCount();
    }

    public void reject(Long applicationId, Long leaderId) {
        MemberCrewApplication application = applicationRepository.findById(applicationId).orElseThrow(() -> new ApplicationNotFoundException());

        Crew crew = application.getCrew();

        if (!crew.getLeader().getId().equals(leaderId)) {
            throw new UnauthorizedException("크루 리더만 거절 가능합니다.");
        }

        application.reject();
    }

    public Page<MemberCrewApplication> findApplicationsByCrew(Long crewId,Long memberId , ApplicationStatus status, Pageable pageable) {
        Crew crew = crewRepository.findById(crewId).orElseThrow(()-> new CrewNotFoundException());

        if (!crew.getLeader().getId().equals(memberId)) {
            throw new UnauthorizedException("크루장만 조회 가능합니다.");
        }

        return applicationRepository.searchByCrew(crewId, status, pageable);
    }

    public Page<MemberCrewApplication> findApplicationsByMember(Long memberId, ApplicationStatus status , Pageable pageable) {
        return applicationRepository.searchByMember(memberId, status, pageable);
    }

    public void cancel(Long applicationId, Long memberId) {
        MemberCrewApplication application = applicationRepository.findById(applicationId).orElseThrow(() -> new ApplicationNotFoundException());

        if (!application.getMember().getId().equals(memberId)){
            throw new UnauthorizedException("본인의 신청만 취소할 수 있습니다");
        }

        if (application.getStatus() != ApplicationStatus.PENDING) {
            throw new IllegalStateException("대기 중인 신청만 취소할 수 있습니다.");
        }

        applicationRepository.delete(application);
    }
}

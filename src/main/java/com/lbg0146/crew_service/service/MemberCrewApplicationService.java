package com.lbg0146.crew_service.service;

import com.lbg0146.crew_service.domain.Crew;
import com.lbg0146.crew_service.domain.Member;
import com.lbg0146.crew_service.domain.MemberCrewApplication;
import com.lbg0146.crew_service.domain.enums.ApplicationStatus;
import com.lbg0146.crew_service.repository.CrewRepository;
import com.lbg0146.crew_service.repository.MemberCrewApplicationRepository;
import com.lbg0146.crew_service.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberCrewApplicationService {

    private final MemberRepository memberRepository;
    private final CrewRepository crewRepository;
    private final MemberCrewApplicationRepository applicationRepository;

    public Long apply(Long memberId, Long crewId) {
        if (applicationRepository.findByMemberIdAndCrewId(memberId, crewId).isPresent()){
            throw new IllegalStateException("이미 신청한 크루입니다.");
        }

        Member member = memberRepository.findById(memberId).orElseThrow(()-> new IllegalArgumentException("존재하지 않는 회원입니다."));
        Crew crew = crewRepository.findById(crewId).orElseThrow(()-> new IllegalArgumentException("존재하지 않는 크루입니다."));
        if (crew.getLeader().getId().equals(memberId)) {
            throw new IllegalStateException("크루장은 신청할 수 없습니다.");
        }

        MemberCrewApplication application = MemberCrewApplication.create(member, crew);

        applicationRepository.save(application);

        return application.getId();
    }

    public void approve(Long applicationId, Long leaderId) {
        MemberCrewApplication application = applicationRepository.findById(applicationId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 신청입니다"));

        Crew crew = application.getCrew();

        if (!crew.getLeader().getId().equals(leaderId)) {
            throw new IllegalStateException("크루 리더만 승인 가능합니다.");
        }

        application.approve();
        crew.increaseMemberCount();
    }

    public void reject(Long applicationId, Long leaderId) {
        MemberCrewApplication application = applicationRepository.findById(applicationId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 신청입니다"));

        Crew crew = application.getCrew();

        if (!crew.getLeader().getId().equals(leaderId)) {
            throw new IllegalStateException("크루 리더만 거절 가능합니다.");
        }

        application.reject();
    }

    public List<MemberCrewApplication> findApplicationsByCrew(Long crewId) {
        return applicationRepository.findByCrewId(crewId);
    }

    public List<MemberCrewApplication> findApplicationsByMember(Long memberId) {
        return applicationRepository.findByMemberId(memberId);
    }
}

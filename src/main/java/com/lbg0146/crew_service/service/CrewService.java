package com.lbg0146.crew_service.service;

import com.lbg0146.crew_service.domain.MemberCrewApplication;
import com.lbg0146.crew_service.domain.enums.ApplicationStatus;
import com.lbg0146.crew_service.domain.enums.RecruitmentStatus;
import com.lbg0146.crew_service.dto.CrewCreateRequest;
import com.lbg0146.crew_service.domain.Crew;
import com.lbg0146.crew_service.domain.Member;
import com.lbg0146.crew_service.domain.enums.Region;
import com.lbg0146.crew_service.domain.enums.SubCategory;
import com.lbg0146.crew_service.dto.CrewLeaderChangeRequest;
import com.lbg0146.crew_service.dto.CrewSearchCondition;
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
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CrewService {

    private final CrewRepository crewRepository;
    private final MemberRepository memberRepository;
    private final MemberCrewApplicationRepository applicationRepository;

    @Transactional
    public Long createCrew(CrewCreateRequest request) {
        if (request.getMaxMemberCount() <= 0) {
            throw new IllegalArgumentException("최대 인원은 1이어야합니다.");
        }

        Member member = memberRepository.findById(request.getMemberId()).orElseThrow(()-> new MemberNotFoundException());

        Crew crew = new Crew(member, request.getSubCategory(), request.getRegion(), request.getTitle(), request.getDescription(), request.getMaxMemberCount());

        crewRepository.save(crew);
        return crew.getId();
    }

    public Page<Crew> findCrews(Pageable pageable) {
        return crewRepository.findAll(pageable);
    }

    public Crew findOne(Long crewId) {
        return crewRepository.findById(crewId).orElseThrow(() -> new CrewNotFoundException());
    }

    @Transactional
    public void update(Long crewId, Long memberId, SubCategory subCategory, Region region, String title, String description, int maxMemberCount) {
        Crew crew = crewRepository.findById(crewId).orElseThrow(() -> new CrewNotFoundException());

        if (!crew.getLeader().getId().equals(memberId)){
            throw new UnauthorizedException("크루장만 수정 가능합니다.");
        }

        if (maxMemberCount < crew.getCurrentMemberCount()) {
            throw new InvalidMemberCountException("현재 인원보다 적게 설정할 수 없습니다.");
        }

        if (maxMemberCount <= 0) {
            throw new InvalidMemberCountException("최대 인원은 1명 이상이어야 합니다.");
        }

        crew.update(subCategory, region, title, description, maxMemberCount);
    }

    @Transactional
    public void delete(Long crewId, Long memberId) {
        Crew crew = crewRepository.findById(crewId).orElseThrow(() -> new CrewNotFoundException());

        if (!crew.getLeader().getId().equals(memberId)) {
            throw new UnauthorizedException("크루장만 삭제 가능합니다.");
        }

        applicationRepository.deleteByCrewId(crewId);
        crewRepository.delete(crew);
    }

    @Transactional
    public void changeRecruitmentStatus(Long crewId, Long memberId, RecruitmentStatus status) {
        Crew crew = crewRepository.findById(crewId).orElseThrow(() -> new CrewNotFoundException());

        if (!crew.getLeader().getId().equals(memberId)) {
            throw new UnauthorizedException("크루장만 변경 가능합니다.");
        }

        crew.changeRecruitmentStatus(status);
    }

    public Page<Crew> search(CrewSearchCondition condition, Pageable pageable) {
        return crewRepository.search(condition, pageable);
    }

    @Transactional
    public void leave(Long crewId, Long memberId) {
        Crew crew = crewRepository.findById(crewId).orElseThrow(() -> new CrewNotFoundException());
        MemberCrewApplication application = applicationRepository.findByMemberIdAndCrewId(memberId, crewId).orElseThrow(() -> new ApplicationNotFoundException());

        if (crew.getLeader().getId().equals(memberId)){
            throw new UnauthorizedException("크루장은 탈퇴 불가능합니다 크루장을 변경해주세요.");
        }

        if (application.getStatus() != ApplicationStatus.APPROVED) {
            throw new IllegalStateException("승인된 회원만 탈퇴할 수 있습니다.");
        }

        crew.decreaseMemberCount();
        applicationRepository.delete(application);
    }

    @Transactional
    public void changeLeader(Long crewId, CrewLeaderChangeRequest request) {
        Crew crew = crewRepository.findById(crewId).orElseThrow(() -> new CrewNotFoundException());

        if (!crew.getLeader().getId().equals(request.getLeaderId())) {
            throw new UnauthorizedException("크루장만 변경할 수 있습니다.");
        }

        if (request.getLeaderId().equals(request.getNewLeaderId())) {
            throw new IllegalArgumentException("이미 현재 크루장입니다.");
        }

        MemberCrewApplication application = applicationRepository.findByMemberIdAndCrewId(request.getNewLeaderId(), crewId).orElseThrow(() -> new ApplicationNotFoundException());

        if (application.getStatus() != ApplicationStatus.APPROVED) {
            throw new IllegalStateException("승인된 회원만 크루장이 될 수 있습니다.");
        }

        createLeaderApplication(crew, request.getLeaderId()); // 기존 리더가 신청이 없다면 새로 생성

        Member member = memberRepository.findById(request.getNewLeaderId()).orElseThrow(() -> new MemberNotFoundException());
        crew.changeLeader(member);
    }

    private void createLeaderApplication(Crew crew, Long oldLeaderId) {
        MemberCrewApplication oldLeaderApplication =
                applicationRepository.findByMemberIdAndCrewId(oldLeaderId, crew.getId())
                        .orElse(null);

        if (oldLeaderApplication == null) {
            MemberCrewApplication newApplication =
                    MemberCrewApplication.create(crew.getLeader(), crew);

            newApplication.approve();

            applicationRepository.save(newApplication);
        }
    }
}
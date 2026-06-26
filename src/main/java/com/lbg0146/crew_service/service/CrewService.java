package com.lbg0146.crew_service.service;

import com.lbg0146.crew_service.domain.enums.RecruitmentStatus;
import com.lbg0146.crew_service.dto.CrewCreateRequest;
import com.lbg0146.crew_service.domain.Crew;
import com.lbg0146.crew_service.domain.Member;
import com.lbg0146.crew_service.domain.enums.Region;
import com.lbg0146.crew_service.domain.enums.SubCategory;
import com.lbg0146.crew_service.dto.CrewSearchCondition;
import com.lbg0146.crew_service.exception.CrewNotFoundException;
import com.lbg0146.crew_service.exception.InvalidMemberCountException;
import com.lbg0146.crew_service.exception.MemberNotFoundException;
import com.lbg0146.crew_service.exception.UnauthorizedException;
import com.lbg0146.crew_service.repository.CrewRepository;
import com.lbg0146.crew_service.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CrewService {

    private final CrewRepository crewRepository;
    private final MemberRepository memberRepository;

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
    public void update(Long crewId, Long memberId,SubCategory subCategory, Region region, String title, String description, int maxMemberCount) {
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

        crew.setSubCategory(subCategory);
        crew.setRegion(region);
        crew.setTitle(title);
        crew.setDescription(description);
        crew.setMaxMemberCount(maxMemberCount);
    }

    @Transactional
    public void delete(Long crewId, Long memberId) {
        Crew crew = crewRepository.findById(crewId).orElseThrow(() -> new CrewNotFoundException());

        if (!crew.getLeader().getId().equals(memberId)) {
            throw new UnauthorizedException("크루장만 삭제 가능합니다.");
        }

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
}

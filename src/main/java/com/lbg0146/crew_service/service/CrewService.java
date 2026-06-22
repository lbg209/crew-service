package com.lbg0146.crew_service.service;

import com.lbg0146.crew_service.domain.Crew;
import com.lbg0146.crew_service.domain.Member;
import com.lbg0146.crew_service.domain.enums.Region;
import com.lbg0146.crew_service.domain.enums.SubCategory;
import com.lbg0146.crew_service.repository.CrewRepository;
import com.lbg0146.crew_service.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CrewService {

    private final CrewRepository crewRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public Long createCrew(Long memberId, SubCategory subCategory, Region region, String title, String description, int maxMemberCount ) {
        Crew crew = new Crew();

        Member member = memberRepository.findById(memberId).orElseThrow(()-> new IllegalArgumentException("존재하지 않는 회원입니다."));

        crew.setLeader(member);
        crew.setSubCategory(subCategory);
        crew.setRegion(region);
        crew.setTitle(title);
        crew.setDescription(description);
        crew.setMaxMemberCount(maxMemberCount);
        crew.setCurrentMemberCount(1);
        crewRepository.save(crew);

        return crew.getId();
    }

    public List<Crew> findCrews() {
        return crewRepository.findAll();
    }

    public Crew findOne(Long crewId) {
        return crewRepository.findById(crewId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 크루입니다"));
    }

    public void update(Long id, SubCategory subCategory, Region region, String title, String description, int maxMemberCount) {
        // 구현 예정
    }

}

package com.lbg0146.crew_service.service;

import com.lbg0146.crew_service.dto.CrewCreateRequest;
import com.lbg0146.crew_service.dto.MemberCreateRequest;
import com.lbg0146.crew_service.domain.Crew;
import com.lbg0146.crew_service.domain.enums.Region;
import com.lbg0146.crew_service.domain.enums.SubCategory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class CrewServiceTest {

    @Autowired
    CrewService crewService;
    @Autowired
    MemberService memberService;

    @Test
    public void 크루생성() {
        MemberCreateRequest member = new MemberCreateRequest("lbg", "1234", "LEE");
        Long saveId = memberService.join(member);

        CrewCreateRequest crewCreateRequest = new CrewCreateRequest(saveId, SubCategory.FOOTBALL, Region.SEOUL, "축구크루", "footballmans", 10);
        Long createId = crewService.createCrew(crewCreateRequest);
        Crew findCrew = crewService.findOne(createId);

        assertThat(findCrew.getTitle()).isEqualTo("축구크루");
        assertThat(findCrew.getLeader().getId()).isEqualTo(saveId);
        assertThat(findCrew.getCurrentMemberCount()).isEqualTo(1);
        assertThat(findCrew.getSubCategory()).isEqualTo(SubCategory.FOOTBALL);
        assertThat(findCrew.getRegion()).isEqualTo(Region.SEOUL);
    }

    @Test
    public void 존재하지_않는_회원으로_크루생성() {
        CrewCreateRequest crewCreateRequest = new CrewCreateRequest(1L, SubCategory.FOOTBALL, Region.SEOUL, "축구크루", "footballmans", 10);

        assertThatThrownBy(() -> crewService.createCrew(crewCreateRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
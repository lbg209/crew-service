package com.lbg0146.crew_service.service;

import com.lbg0146.crew_service.domain.Crew;
import com.lbg0146.crew_service.domain.Member;
import com.lbg0146.crew_service.domain.enums.Region;
import com.lbg0146.crew_service.domain.enums.SubCategory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class CrewServiceTest {

    @Autowired
    CrewService crewService;
    @Autowired
    MemberService memberService;

    @Test
    public void 크루생성() {
        Member member = new Member();
        member.setNickname("LEE");
        member.setLoginId("lbg");
        member.setPassword("1234");

        Long saveId = memberService.join(member);
        Long createId = crewService.createCrew(saveId, SubCategory.FOOTBALL, Region.SEOUL, "축구크루", "footballmans", 10);
        Crew findCrew = crewService.findOne(createId);

        assertThat(findCrew.getTitle()).isEqualTo("축구크루");
        assertThat(findCrew.getLeader().getId()).isEqualTo(member.getId());
        assertThat(findCrew.getCurrentMemberCount()).isEqualTo(1);
        assertThat(findCrew.getSubCategory()).isEqualTo(SubCategory.FOOTBALL);
        assertThat(findCrew.getRegion()).isEqualTo(Region.SEOUL);
    }

    @Test
    public void 존재하지_않는_회원으로_크루생성() {
        assertThatThrownBy(() -> crewService.createCrew(
                999L, SubCategory.FOOTBALL, Region.SEOUL, "축구크루", "footballmans", 10))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
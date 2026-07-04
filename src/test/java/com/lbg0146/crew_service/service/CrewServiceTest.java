package com.lbg0146.crew_service.service;

import com.lbg0146.crew_service.domain.enums.RecruitmentStatus;
import com.lbg0146.crew_service.dto.CrewCreateRequest;
import com.lbg0146.crew_service.dto.MemberCreateRequest;
import com.lbg0146.crew_service.domain.Crew;
import com.lbg0146.crew_service.domain.enums.Region;
import com.lbg0146.crew_service.domain.enums.SubCategory;
import com.lbg0146.crew_service.exception.MemberNotFoundException;
import com.lbg0146.crew_service.exception.UnauthorizedException;
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
    @Autowired
    MemberCrewApplicationService applicationService;

    @Test
    public void 크루생성() {
        MemberCreateRequest member = new MemberCreateRequest("lbg", "1234", "LEE");
        Long saveId = memberService.join(member);

        CrewCreateRequest crewCreateRequest = new CrewCreateRequest(SubCategory.FOOTBALL, Region.SEOUL, "축구크루", "footballmans", 10);
        Long createId = crewService.createCrew(saveId ,crewCreateRequest);
        Crew findCrew = crewService.findOne(createId);

        assertThat(findCrew.getTitle()).isEqualTo("축구크루");
        assertThat(findCrew.getLeader().getId()).isEqualTo(saveId);
        assertThat(findCrew.getCurrentMemberCount()).isEqualTo(1);
        assertThat(findCrew.getSubCategory()).isEqualTo(SubCategory.FOOTBALL);
        assertThat(findCrew.getRegion()).isEqualTo(Region.SEOUL);
    }

    @Test
    public void 존재하지_않는_회원으로_크루생성() {
        CrewCreateRequest crewCreateRequest = new CrewCreateRequest(SubCategory.FOOTBALL, Region.SEOUL, "축구크루", "footballmans", 10);

        assertThatThrownBy(() -> crewService.createCrew(999L, crewCreateRequest))
                .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    public void 크루_수정_성공() {
        Long memberId = memberService.join(new MemberCreateRequest("lbg", "1234", "LEE"));
        Long crewId = crewService.createCrew(memberId, new CrewCreateRequest(SubCategory.FOOTBALL, Region.SEOUL, "soccer", "footballclub", 10));


        crewService.update(crewId, memberId, SubCategory.BASKETBALL, Region.BUSAN, "BASKETBALL", "basketballclub", 15);

        Crew crew = crewService.findOne(crewId);

        assertThat(crew.getTitle()).isEqualTo("BASKETBALL");
    }

    @Test
    public void 크루_수정_리더아니면_실패() {
        Long leaderId = memberService.join(new MemberCreateRequest("leader", "1234", "leader"));
        Long memberId = memberService.join(new MemberCreateRequest("member", "1234", "member"));

        Long crewId = crewService.createCrew(leaderId, new CrewCreateRequest(SubCategory.FOOTBALL, Region.SEOUL, "soccer", "footballclub", 10));

        assertThatThrownBy(() -> crewService.update(crewId, memberId, SubCategory.BASKETBALL, Region.BUSAN, "BASKETBALL", "basketballclub", 15))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void 모집상태_변경() {
        Long leaderId = memberService.join(new MemberCreateRequest("leader", "1234", "leader"));

        Long crewId = crewService.createCrew(leaderId, new CrewCreateRequest(SubCategory.FOOTBALL, Region.SEOUL, "soccer", "footballclub", 10));

        crewService.changeRecruitmentStatus(crewId, leaderId, RecruitmentStatus.CLOSED);

        Crew crew = crewService.findOne(crewId);

        assertThat(crew.getRecruitmentStatus()).isEqualTo(RecruitmentStatus.CLOSED);
    }

    @Test
    void 크루리더_변경() {
        Long oldLeaderId = memberService.join(new MemberCreateRequest("leader", "1234", "leader"));
        Long newLeaderId = memberService.join(new MemberCreateRequest("member", "1234", "member"));
        Long crewId = crewService.createCrew(oldLeaderId, new CrewCreateRequest(SubCategory.FOOTBALL, Region.SEOUL, "soccer", "footballclub", 10));

        Long applyId = applicationService.apply(newLeaderId, crewId);

        applicationService.approve(applyId, oldLeaderId);

        crewService.changeLeader(crewId, oldLeaderId, newLeaderId);

        Crew crew = crewService.findOne(crewId);

        assertThat(crew.getLeader().getId()).isEqualTo(newLeaderId);

    }

    @Test
    void 크루_탈퇴_성공() {
        Long leaderId = memberService.join(new MemberCreateRequest("leader", "1234", "leader"));
        Long memberId = memberService.join(new MemberCreateRequest("member", "1234", "member"));

        Long crewId = crewService.createCrew(leaderId, new CrewCreateRequest(SubCategory.FOOTBALL, Region.SEOUL, "soccer", "footballclub", 10));

        Long apply = applicationService.apply(memberId, crewId);

        applicationService.approve(apply, leaderId);

        crewService.leave(crewId, memberId);

        Crew crew = crewService.findOne(crewId);
        assertThat(crew.getCurrentMemberCount()).isEqualTo(1);

    }

}
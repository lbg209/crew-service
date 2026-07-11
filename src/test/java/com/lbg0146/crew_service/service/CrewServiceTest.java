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

    private Long createLeader() {
        return memberService.join(new MemberCreateRequest("leader", "1234", "leader"));
    }

    private Long createMember() {
        return memberService.join(new MemberCreateRequest("member", "1234", "member"));
    }

    private Long createCrew(Long leaderId) {
        return crewService.createCrew(leaderId, new CrewCreateRequest(SubCategory.FOOTBALL, Region.SEOUL, "soccer", "footballclub", 10));
    }

    @Test
    public void 크루생성() {
        Long leaderId = createLeader();

        CrewCreateRequest crewCreateRequest = new CrewCreateRequest(SubCategory.FOOTBALL, Region.SEOUL, "축구크루", "footballmans", 10);
        Long createId = crewService.createCrew(leaderId ,crewCreateRequest);
        Crew findCrew = crewService.findOne(createId);

        assertThat(findCrew.getTitle()).isEqualTo("축구크루");
        assertThat(findCrew.getLeader().getId()).isEqualTo(leaderId);
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
        Long leaderId = createLeader();
        Long crewId = createCrew(leaderId);


        crewService.update(crewId, leaderId, SubCategory.BASKETBALL, Region.BUSAN, "BASKETBALL", "basketballclub", 15);

        Crew crew = crewService.findOne(crewId);

        assertThat(crew.getTitle()).isEqualTo("BASKETBALL");
    }


    @Test
    public void 크루_수정_리더아니면_실패() {
        Long leaderId = createLeader();
        Long memberId = createMember();

        Long crewId = createCrew(leaderId);

        assertThatThrownBy(() -> crewService.update(crewId, memberId, SubCategory.BASKETBALL, Region.BUSAN, "BASKETBALL", "basketballclub", 15))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void 모집상태_변경() {
        Long leaderId = createLeader();

        Long crewId = createCrew(leaderId);

        crewService.changeRecruitmentStatus(crewId, leaderId, RecruitmentStatus.CLOSED);

        Crew crew = crewService.findOne(crewId);

        assertThat(crew.getRecruitmentStatus()).isEqualTo(RecruitmentStatus.CLOSED);
    }

    @Test
    void 크루리더_변경() {
        Long oldLeaderId = createLeader();
        Long newLeaderId = createMember();
        Long crewId = createCrew(oldLeaderId);

        Long applyId = applicationService.apply(newLeaderId, crewId);

        applicationService.approve(applyId, oldLeaderId);

        crewService.changeLeader(crewId, oldLeaderId, newLeaderId);

        Crew crew = crewService.findOne(crewId);

        assertThat(crew.getLeader().getId()).isEqualTo(newLeaderId);

    }

    @Test
    void 크루_탈퇴_성공() {
        Long leaderId = createLeader();
        Long memberId = createMember();

        Long crewId = createCrew(leaderId);

        Long apply = applicationService.apply(memberId, crewId);

        applicationService.approve(apply, leaderId);

        crewService.leave(crewId, memberId);

        Crew crew = crewService.findOne(crewId);
        assertThat(crew.getCurrentMemberCount()).isEqualTo(1);

    }

}
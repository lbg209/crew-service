package com.lbg0146.crew_service.service;

import com.lbg0146.crew_service.domain.Member;
import com.lbg0146.crew_service.domain.MemberCrewApplication;
import com.lbg0146.crew_service.domain.enums.ApplicationStatus;
import com.lbg0146.crew_service.domain.enums.Region;
import com.lbg0146.crew_service.domain.enums.SubCategory;
import com.lbg0146.crew_service.repository.MemberCrewApplicationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberCrewApplicationServiceTest {

    @Autowired
    MemberCrewApplicationService applicationService;
    @Autowired
    MemberCrewApplicationRepository applicationRepository;
    @Autowired
    MemberService memberService;
    @Autowired
    CrewService crewService;

    private Long createAndSaveMember(String nickname, String loginId) {
        Member member = new Member();
        member.setNickname(nickname);
        member.setLoginId(loginId);
        member.setPassword("1234");

        return memberService.join(member);
    }

    @Test
    public void 크루신청() {
        Long leaderId = createAndSaveMember("LEE", "lbg");
        Long applicantId = createAndSaveMember("KIM", "abc");
        Long crewId = crewService.createCrew(leaderId, SubCategory.FOOTBALL, Region.SEOUL, "축구크루", "footballmans", 10);

        applicationService.apply(applicantId, crewId);

        MemberCrewApplication application = applicationRepository.findByMemberIdAndCrewId(applicantId, crewId).orElseThrow();

        assertThat(application.getStatus()).isEqualTo(ApplicationStatus.PENDING);
        assertThat(application.getMember().getId()).isEqualTo(applicantId);
        assertThat(application.getCrew().getId()).isEqualTo(crewId);
    }

    @Test
    public void 승인() {
        Long leaderId = createAndSaveMember("LEE", "lbg");
        Long applicantId = createAndSaveMember("KIM", "abc");
        Long crewId = crewService.createCrew(leaderId, SubCategory.FOOTBALL, Region.SEOUL, "축구크루", "footballmans", 10);

        applicationService.apply(applicantId, crewId);
        MemberCrewApplication application = applicationRepository.findByMemberIdAndCrewId(applicantId, crewId).orElseThrow();
        applicationService.approve(application.getId());

        assertThat(application.getStatus()).isEqualTo(ApplicationStatus.APPROVED);
        assertThat(application.getCrew().getCurrentMemberCount()).isEqualTo(2);
    }

    @Test
    public void 거절() {
        Long leaderId = createAndSaveMember("LEE", "lbg");
        Long applicantId = createAndSaveMember("KIM", "abc");
        Long crewId = crewService.createCrew(leaderId, SubCategory.FOOTBALL, Region.SEOUL, "축구크루", "footballmans", 10);

        applicationService.apply(applicantId, crewId);
        MemberCrewApplication application = applicationRepository.findByMemberIdAndCrewId(applicantId, crewId).orElseThrow();
        applicationService.reject(application.getId());

        assertThat(application.getStatus()).isEqualTo(ApplicationStatus.REJECTED);
    }

    @Test
    public void 중복신청() {
        Long leaderId = createAndSaveMember("LEE", "lbg");
        Long applicantId = createAndSaveMember("KIM", "abc");
        Long crewId = crewService.createCrew(leaderId, SubCategory.FOOTBALL, Region.SEOUL, "축구크루", "footballmans", 10);

        applicationService.apply(applicantId, crewId);

        assertThatThrownBy(() -> applicationService.apply(applicantId, crewId)).isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void 중복승인() {
        Long leaderId = createAndSaveMember("LEE", "lbg");
        Long applicantId = createAndSaveMember("KIM", "abc");
        Long crewId = crewService.createCrew(leaderId, SubCategory.FOOTBALL, Region.SEOUL, "축구크루", "footballmans", 10);

        applicationService.apply(applicantId, crewId);
        MemberCrewApplication application = applicationRepository.findByMemberIdAndCrewId(applicantId, crewId).orElseThrow();
        applicationService.approve(application.getId());

        assertThatThrownBy(() -> applicationService.approve(application.getId())).isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void 정원초과() {
        Long leaderId = createAndSaveMember("LEE", "lbg");
        Long applicantId = createAndSaveMember("KIM", "abc");
        Long crewId = crewService.createCrew(leaderId, SubCategory.FOOTBALL, Region.SEOUL, "축구크루", "footballmans", 1);

        applicationService.apply(applicantId, crewId);
        MemberCrewApplication application = applicationRepository.findByMemberIdAndCrewId(applicantId, crewId).orElseThrow();

        assertThatThrownBy(() -> applicationService.approve(application.getId())).isInstanceOf(IllegalStateException.class);
    }

}
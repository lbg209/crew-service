package com.lbg0146.crew_service.service;

import com.lbg0146.crew_service.dto.CrewCreateRequest;
import com.lbg0146.crew_service.dto.MemberCreateRequest;
import com.lbg0146.crew_service.domain.MemberCrewApplication;
import com.lbg0146.crew_service.domain.enums.ApplicationStatus;
import com.lbg0146.crew_service.domain.enums.Region;
import com.lbg0146.crew_service.domain.enums.SubCategory;
import com.lbg0146.crew_service.exception.DuplicateApplicationException;
import com.lbg0146.crew_service.exception.InvalidMemberCountException;
import com.lbg0146.crew_service.exception.UnauthorizedException;
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

    private Long createMember(String loginId, String nickname) {
        MemberCreateRequest member = new MemberCreateRequest(loginId, "1234", nickname);

        return memberService.join(member);
    }

    private Long createCrew(Long leaderId, int maxMembers) {
        CrewCreateRequest request = new CrewCreateRequest(SubCategory.FOOTBALL, Region.SEOUL, "축구크루", "footballmans", maxMembers);

        return crewService.createCrew(leaderId, request);
    }

    @Test
    public void 크루신청() {
        Long leaderId = createMember("lbg", "LEE");
        Long applicantId = createMember("abc", "KIM");
        Long crewId = createCrew(leaderId, 10);

        applicationService.apply(applicantId, crewId);

        MemberCrewApplication application = applicationRepository.findByMemberIdAndCrewId(applicantId, crewId).orElseThrow();

        assertThat(application.getStatus()).isEqualTo(ApplicationStatus.PENDING);
        assertThat(application.getMember().getId()).isEqualTo(applicantId);
        assertThat(application.getCrew().getId()).isEqualTo(crewId);
    }

    @Test
    public void 승인() {
        Long leaderId = createMember("lbg", "LEE");
        Long applicantId = createMember("abc", "KIM");
        Long crewId = createCrew(leaderId, 10);

        applicationService.apply(applicantId, crewId);
        MemberCrewApplication application = applicationRepository.findByMemberIdAndCrewId(applicantId, crewId).orElseThrow();
        applicationService.approve(application.getId(), leaderId);

        assertThat(application.getStatus()).isEqualTo(ApplicationStatus.APPROVED);
        assertThat(application.getCrew().getCurrentMemberCount()).isEqualTo(2);
    }

    @Test
    public void 거절() {
        Long leaderId = createMember("lbg", "LEE");
        Long applicantId = createMember("abc", "KIM");
        Long crewId = createCrew(leaderId, 10);

        applicationService.apply(applicantId, crewId);
        MemberCrewApplication application = applicationRepository.findByMemberIdAndCrewId(applicantId, crewId).orElseThrow();
        applicationService.reject(application.getId(), leaderId);

        assertThat(application.getStatus()).isEqualTo(ApplicationStatus.REJECTED);
    }

    @Test
    public void 중복신청_예외() {
        Long leaderId = createMember("lbg", "LEE");
        Long applicantId = createMember("abc", "KIM");
        Long crewId = createCrew(leaderId, 10);

        applicationService.apply(applicantId, crewId);

        assertThatThrownBy(() -> applicationService.apply(applicantId, crewId)).isInstanceOf(DuplicateApplicationException.class);
    }

    @Test
    public void 중복승인_예외() {
        Long leaderId = createMember("lbg", "LEE");
        Long applicantId = createMember("abc", "KIM");
        Long crewId = createCrew(leaderId, 10);

        Long applyId = applicationService.apply(applicantId, crewId);
        applicationService.approve(applyId, leaderId);

        assertThatThrownBy(() -> applicationService.approve(applyId, leaderId)).isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void 정원초과_예외() {
        Long leaderId = createMember("lbg", "LEE");
        Long applicantId = createMember("abc", "KIM");
        Long crewId = createCrew(leaderId, 1);

        Long applyId = applicationService.apply(applicantId, crewId);

        assertThatThrownBy(() -> applicationService.approve(applyId, leaderId)).isInstanceOf(InvalidMemberCountException.class);
    }

    @Test
    public void 크루장_신청_예외() {
        Long leaderId = createMember("lbg", "LEE");
        Long crewId = createCrew(leaderId, 1);

        assertThatThrownBy(() -> applicationService.apply(leaderId, crewId)).isInstanceOf(UnauthorizedException.class);
    }

    @Test
    public void 크루장이_아닌_멤버의_승인또는거절_예외() {
        Long leaderId = createMember("lbg", "LEE");
        Long applicantId = createMember("abc", "KIM");
        Long memberId = createMember("member1", "member");
        Long crewId = createCrew(leaderId, 1);

        Long applyId = applicationService.apply(applicantId, crewId);

        assertThatThrownBy(() -> applicationService.approve(applyId, memberId)).isInstanceOf(UnauthorizedException.class);
        assertThatThrownBy(() -> applicationService.reject(applyId, memberId)).isInstanceOf(UnauthorizedException.class);
    }

    @Test
    public void 중복거절_예외() {
        Long leaderId = createMember("lbg", "LEE");
        Long applicantId = createMember("abc", "KIM");
        Long crewId = createCrew(leaderId, 1);

        Long applyId = applicationService.apply(applicantId, crewId);
        applicationService.reject(applyId, leaderId);

        assertThatThrownBy(() -> applicationService.reject(applyId, leaderId)).isInstanceOf(IllegalStateException.class);
    }

}
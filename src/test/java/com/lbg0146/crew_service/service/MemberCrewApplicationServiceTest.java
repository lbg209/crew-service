package com.lbg0146.crew_service.service;

import com.lbg0146.crew_service.domain.Crew;
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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.*;

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

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void 동시에_승인요청시_정원을_초과하지_않는다() throws Exception {
        Long leaderId = createMember("leader101", "leader101");
        Long member1 = createMember("member500", "member500");
        Long member2 = createMember("member501", "member501");

        Long crewId = createCrew(leaderId, 2);

        Long apply1 = applicationService.apply(member1, crewId);
        Long apply2 = applicationService.apply(member2, crewId);

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(2);
        List<Exception> exceptions = new CopyOnWriteArrayList<>();

        executorService.submit(() -> {
            try {
                applicationService.approve(apply1, leaderId);
            } catch (Exception e) {
                exceptions.add(e);
            } finally {
                latch.countDown();
            }
        });

        executorService.submit(() -> {
            try {
                applicationService.approve(apply2, leaderId);
            } catch (Exception e) {
                exceptions.add(e);
            } finally {
                latch.countDown();
            }
        });

        latch.await(5, TimeUnit.SECONDS);

        Crew crew = crewService.findOne(crewId);

        assertThat(crew.getCurrentMemberCount()).isEqualTo(2);

        MemberCrewApplication application1 = applicationRepository.findById(apply1).orElseThrow();
        MemberCrewApplication application2 = applicationRepository.findById(apply2).orElseThrow();

        long approvedCount = 0;

        if (application1.getStatus() == ApplicationStatus.APPROVED)
            approvedCount++;
        if (application2.getStatus() == ApplicationStatus.APPROVED)
            approvedCount++;

        assertThat(approvedCount).isEqualTo(1);

        assertThat(exceptions)
                .hasSize(1)
                .first()
                .isInstanceOf(InvalidMemberCountException.class);
    }

}
package com.lbg0146.crew_service.service;

import com.lbg0146.crew_service.domain.MemberCrewApplication;
import com.lbg0146.crew_service.domain.enums.ApplicationStatus;
import com.lbg0146.crew_service.domain.enums.Region;
import com.lbg0146.crew_service.domain.enums.SubCategory;
import com.lbg0146.crew_service.dto.CrewCreateRequest;
import com.lbg0146.crew_service.dto.MemberCreateRequest;
import com.lbg0146.crew_service.jwt.JwtTokenProvider;
import com.lbg0146.crew_service.repository.MemberCrewApplicationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class MemberCrewApplicationControllerTest {

    @Autowired
    MemberService memberService;
    @Autowired
    CrewService crewService;
    @Autowired
    MemberCrewApplicationService applicationService;
    @Autowired
    MemberCrewApplicationRepository applicationRepository;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void 크루_신청_테스트() throws Exception {

        Long leaderId = memberService.join(new MemberCreateRequest("test999", "1234", "nickname999"));
        Long memberId = memberService.join(new MemberCreateRequest("test444", "1234", "nickname444"));
        Long crewId = crewService.createCrew(leaderId, new CrewCreateRequest(SubCategory.FOOTBALL, Region.SEOUL, "soccer", "footballclub", 10));

        String token = jwtTokenProvider.createJwt("test444", "ROLE_USER", 1000L * 60 * 10);

        mockMvc.perform(post("/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content("""
                        {
                          "crewId" : %d
                        }
                        """.formatted(crewId)))
                .andExpect(status().isOk());

        MemberCrewApplication application = applicationRepository.findByMemberIdAndCrewId(memberId, crewId).orElseThrow();

        assertThat(application.getStatus()).isEqualTo(ApplicationStatus.PENDING);
    }

    @Test
    void 크루_신청_JWT없이_테스트() throws Exception {

        Long leaderId = memberService.join(new MemberCreateRequest("test999", "1234", "nickname999"));
        Long memberId = memberService.join(new MemberCreateRequest("test444", "1234", "nickname444"));
        Long crewId = crewService.createCrew(leaderId, new CrewCreateRequest(SubCategory.FOOTBALL, Region.SEOUL, "soccer", "footballclub", 10));

        mockMvc.perform(post("/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "crewId" : %d
                        }
                        """.formatted(crewId)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void 크루_승인_테스트() throws Exception {

        Long leaderId = memberService.join(new MemberCreateRequest("test999", "1234", "nickname999"));
        Long memberId = memberService.join(new MemberCreateRequest("test444", "1234", "nickname444"));
        Long crewId = crewService.createCrew(leaderId, new CrewCreateRequest(SubCategory.FOOTBALL, Region.SEOUL, "soccer", "footballclub", 10));
        Long applyId = applicationService.apply(memberId, crewId);

        String token = jwtTokenProvider.createJwt("test999", "ROLE_USER", 1000L * 60 * 10);

        mockMvc.perform(patch("/applications/{applicationId}/approve", applyId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        MemberCrewApplication application = applicationRepository.findByMemberIdAndCrewId(memberId, crewId).orElseThrow();

        assertThat(application.getStatus()).isEqualTo(ApplicationStatus.APPROVED);
    }

    @Test
    void 크루_승인_JWT없이_테스트() throws Exception {

        Long leaderId = memberService.join(new MemberCreateRequest("test999", "1234", "nickname999"));
        Long memberId = memberService.join(new MemberCreateRequest("test444", "1234", "nickname444"));
        Long crewId = crewService.createCrew(leaderId, new CrewCreateRequest(SubCategory.FOOTBALL, Region.SEOUL, "soccer", "footballclub", 10));
        Long applyId = applicationService.apply(memberId, crewId);

        mockMvc.perform(patch("/applications/{applicationId}/approve", applyId))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void 크루장이_아닌_사용자가_승인_요청() throws Exception {
        Long leaderId = memberService.join(new MemberCreateRequest("test999", "1234", "nickname999"));
        Long memberId = memberService.join(new MemberCreateRequest("test444", "1234", "nickname444"));
        Long crewId = crewService.createCrew(leaderId, new CrewCreateRequest(SubCategory.FOOTBALL, Region.SEOUL, "soccer", "footballclub", 10));
        Long applyId = applicationService.apply(memberId, crewId);

        String token = jwtTokenProvider.createJwt("test444", "ROLE_USER", 1000L * 60 * 10);

        mockMvc.perform(patch("/applications/{applicationId}/approve", applyId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());

    }

    @Test
    void 크루_거절_테스트() throws Exception {

        Long leaderId = memberService.join(new MemberCreateRequest("test999", "1234", "nickname999"));
        Long memberId = memberService.join(new MemberCreateRequest("test444", "1234", "nickname444"));
        Long crewId = crewService.createCrew(leaderId, new CrewCreateRequest(SubCategory.FOOTBALL, Region.SEOUL, "soccer", "footballclub", 10));
        Long applyId = applicationService.apply(memberId, crewId);

        String token = jwtTokenProvider.createJwt("test999", "ROLE_USER", 1000L * 60 * 10);

        mockMvc.perform(patch("/applications/{applicationId}/reject", applyId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        MemberCrewApplication application = applicationRepository.findByMemberIdAndCrewId(memberId, crewId).orElseThrow();

        assertThat(application.getStatus()).isEqualTo(ApplicationStatus.REJECTED);
    }

    @Test
    void 크루_거절_JWT없이_테스트() throws Exception {

        Long leaderId = memberService.join(new MemberCreateRequest("test999", "1234", "nickname999"));
        Long memberId = memberService.join(new MemberCreateRequest("test444", "1234", "nickname444"));
        Long crewId = crewService.createCrew(leaderId, new CrewCreateRequest(SubCategory.FOOTBALL, Region.SEOUL, "soccer", "footballclub", 10));
        Long applyId = applicationService.apply(memberId, crewId);

        mockMvc.perform(patch("/applications/{applicationId}/reject", applyId))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void 크루장이_아닌_사용자가_거절_요청() throws Exception {
        Long leaderId = memberService.join(new MemberCreateRequest("test999", "1234", "nickname999"));
        Long memberId = memberService.join(new MemberCreateRequest("test444", "1234", "nickname444"));
        Long crewId = crewService.createCrew(leaderId, new CrewCreateRequest(SubCategory.FOOTBALL, Region.SEOUL, "soccer", "footballclub", 10));
        Long applyId = applicationService.apply(memberId, crewId);

        String token = jwtTokenProvider.createJwt("test444", "ROLE_USER", 1000L * 60 * 10);

        mockMvc.perform(patch("/applications/{applicationId}/reject", applyId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());

    }

    @Test
    void 이미_신청한_사용자가_다시_신청() throws Exception{
        Long leaderId = memberService.join(new MemberCreateRequest("test999", "1234", "nickname999"));
        Long memberId = memberService.join(new MemberCreateRequest("test444", "1234", "nickname444"));
        Long crewId = crewService.createCrew(leaderId, new CrewCreateRequest(SubCategory.FOOTBALL, Region.SEOUL, "soccer", "footballclub", 10));
        Long applyId = applicationService.apply(memberId, crewId);

        String token = jwtTokenProvider.createJwt("test444", "ROLE_USER", 1000L * 60 * 10);

        mockMvc.perform(post("/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content("""
                        {
                          "crewId" : %d
                        }
                        """.formatted(crewId)))
                .andExpect(status().isConflict());
    }

    @Test
    void 존재하지_않는_신청_승인() throws Exception {

        Long leaderId = memberService.join(new MemberCreateRequest("test999", "1234", "nickname999"));
        Long memberId = memberService.join(new MemberCreateRequest("test444", "1234", "nickname444"));
        Long crewId = crewService.createCrew(leaderId, new CrewCreateRequest(SubCategory.FOOTBALL, Region.SEOUL, "soccer", "footballclub", 10));

        String token = jwtTokenProvider.createJwt("test999", "ROLE_USER", 1000L * 60 * 10);

        mockMvc.perform(patch("/applications/{applicationId}/approve", 999L)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void 존재하지_않는_크루_신청() throws Exception {

        Long leaderId = memberService.join(new MemberCreateRequest("test999", "1234", "nickname999"));
        Long memberId = memberService.join(new MemberCreateRequest("test444", "1234", "nickname444"));
        Long crewId = crewService.createCrew(leaderId, new CrewCreateRequest(SubCategory.FOOTBALL, Region.SEOUL, "soccer", "footballclub", 10));

        String token = jwtTokenProvider.createJwt("test999", "ROLE_USER", 1000L * 60 * 10);

        mockMvc.perform(post("/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content("""
                        {
                          "crewId" : %d
                        }
                        """.formatted(999L)))
                .andExpect(status().isNotFound());

    }
}

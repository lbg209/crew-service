package com.lbg0146.crew_service.service;

import com.lbg0146.crew_service.domain.Crew;
import com.lbg0146.crew_service.domain.enums.RecruitmentStatus;
import com.lbg0146.crew_service.domain.enums.Region;
import com.lbg0146.crew_service.domain.enums.SubCategory;
import com.lbg0146.crew_service.dto.CrewCreateRequest;
import com.lbg0146.crew_service.dto.MemberCreateRequest;
import com.lbg0146.crew_service.exception.CrewNotFoundException;
import com.lbg0146.crew_service.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class CrewControllerTest {

    @Autowired
    MemberService memberService;
    @Autowired
    CrewService crewService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private String createToken(String loginId) {
        MemberCreateRequest request =
                new MemberCreateRequest(loginId, "1234", loginId + "nickname");

        memberService.join(request);

        return jwtTokenProvider.createJwt("access", loginId, "ROLE_USER", 1000L * 60 * 10);
    }

    private String getToken(String loginId) {
        return jwtTokenProvider.createJwt("access", loginId, "ROLE_USER", 1000L * 60 * 10);
    }

    private Long createCrew(Long memberId) {
        return crewService.createCrew(memberId, new CrewCreateRequest(SubCategory.FOOTBALL, Region.SEOUL, "soccer", "footballclub", 10));
    }

    private Long createMember() {
        return memberService.join(new MemberCreateRequest("test999", "1234", "nickname999"));
    }

    @Test
    void 크루_생성_테스트() throws Exception {

        String token = createToken("test999");

        mockMvc.perform(post("/crews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content("""
                        {
                          "subCategory": "FOOTBALL",
                          "region": "SEOUL",
                          "title": "test",
                          "description": "desc",
                          "maxMemberCount": 10
                        }
                        """))
                .andExpect(status().isOk());
    }

    @Test
    void 크루_생성_JWT없이_테스트() throws Exception {

        mockMvc.perform(post("/crews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "subCategory": "FOOTBALL",
                          "region": "SEOUL",
                          "title": "test",
                          "description": "desc",
                          "maxMemberCount": 10
                        }
                        """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void 크루_수정_성공_테스트() throws Exception {

        Long memberId = createMember();
        Long crewId = createCrew(memberId);

        String token = getToken("test999");

        mockMvc.perform(patch("/crews/"+crewId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content("""
                        {
                          "subCategory": "BASKETBALL",
                          "region": "BUSAN",
                          "title": "TEAM",
                          "description": "DESCRIPTION",
                          "maxMemberCount": 20
                        }
                        """))
                .andExpect(status().isOk());

        Crew crew = crewService.findOne(crewId);

        assertThat(crew.getTitle()).isEqualTo("TEAM");
        assertThat(crew.getRegion()).isEqualTo(Region.BUSAN);
        assertThat(crew.getSubCategory()).isEqualTo(SubCategory.BASKETBALL);
        assertThat(crew.getMaxMemberCount()).isEqualTo(20);
    }



    @Test
    void 크루_수정_실패_테스트() throws Exception {

        Long leaderId = createMember();
        memberService.join(new MemberCreateRequest("test444", "1234", "nickname444"));
        Long crewId = createCrew(leaderId);

        String token = getToken("test444");

        mockMvc.perform(patch("/crews/"+crewId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content("""
                        {
                          "subCategory": "BASKETBALL",
                          "region": "BUSAN",
                          "title": "TEAM",
                          "description": "DESCRIPTION",
                          "maxMemberCount": 20
                        }
                        """))
                .andExpect(status().isForbidden());

        Crew crew = crewService.findOne(crewId);

        assertThat(crew.getRegion()).isEqualTo(Region.SEOUL);
    }

    @Test
    void 크루_수정_JWT없이_테스트() throws Exception {

        Long leaderId = createMember();
        memberService.join(new MemberCreateRequest("test444", "1234", "nickname444"));
        Long crewId = createCrew(leaderId);

        mockMvc.perform(patch("/crews/"+crewId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "subCategory": "BASKETBALL",
                          "region": "BUSAN",
                          "title": "TEAM",
                          "description": "DESCRIPTION",
                          "maxMemberCount": 20
                        }
                        """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void 크루_삭제_성공_테스트() throws Exception {

        Long leaderId = createMember();
        Long crewId = createCrew(leaderId);

        String token = getToken("test999");

        mockMvc.perform(delete("/crews/" + crewId)
                        .header("Authorization", "Bearer " + token))
                        .andExpect(status().isOk());

        assertThatThrownBy(() -> crewService.findOne(crewId)).isInstanceOf(CrewNotFoundException.class);
    }

    @Test
    void 크루_삭제_실패_테스트() throws Exception {

        Long leaderId = createMember();
        memberService.join(new MemberCreateRequest("test444", "1234", "nickname444"));
        Long crewId = createCrew(leaderId);

        String token = getToken("test444");

        mockMvc.perform(delete("/crews/" + crewId)
                        .header("Authorization", "Bearer " + token))
                        .andExpect(status().isForbidden());

        Crew crew = crewService.findOne(crewId);
        assertThat(crew).isNotNull();
    }

    @Test
    void 크루_삭제_JWT없이_테스트() throws Exception {

        Long leaderId = createMember();
        memberService.join(new MemberCreateRequest("test444", "1234", "nickname444"));
        Long crewId = createCrew(leaderId);

        mockMvc.perform(delete("/crews/" + crewId))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void 크루_모집_상태_변경_성공_테스트() throws Exception {

        Long leaderId = createMember();
        Long crewId = createCrew(leaderId);

        String token = getToken("test999");

        mockMvc.perform(patch("/crews/" + crewId + "/recruitment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content("""
                        {
                          "status" : "CLOSED"
                        }
                        """))
                        .andExpect(status().isOk());

        Crew crew = crewService.findOne(crewId);

        assertThat(crew.getRecruitmentStatus()).isEqualTo(RecruitmentStatus.CLOSED);
    }

    @Test
    void 크루_모집_상태_변경_실패_테스트() throws Exception {

        Long leaderId = createMember();
        memberService.join(new MemberCreateRequest("test444", "1234", "nickname444"));
        Long crewId = createCrew(leaderId);

        String token = getToken("test444");

        mockMvc.perform(patch("/crews/" + crewId + "/recruitment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content("""
                        {
                          "status" : "CLOSED"
                        }
                        """))
                        .andExpect(status().isForbidden());

        Crew crew = crewService.findOne(crewId);

        assertThat(crew.getRecruitmentStatus()).isEqualTo(RecruitmentStatus.RECRUITING);
    }

    @Test
    void 크루_모집_상태_변경_JWT없이_테스트() throws Exception {
        Long leaderId = createMember();
        memberService.join(new MemberCreateRequest("test444", "1234", "nickname444"));
        Long crewId = createCrew(leaderId);

        getToken("test444");

        mockMvc.perform(patch("/crews/" + crewId + "/recruitment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "status" : "CLOSED"
                        }
                        """))
                .andExpect(status().isUnauthorized());
    }
}

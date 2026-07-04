package com.lbg0146.crew_service.service;

import com.lbg0146.crew_service.domain.Member;
import com.lbg0146.crew_service.dto.MemberCreateRequest;
import com.lbg0146.crew_service.exception.MemberNotFoundException;
import com.lbg0146.crew_service.jwt.JwtTokenProvider;
import com.lbg0146.crew_service.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class MemberControllerTest {

    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;



    @Test
    void 회원가입_성공() throws Exception {
        mockMvc.perform(post("/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "loginId":"testID",
                          "password":"1234",
                          "nickname":"testNickName"
                        }
                        """))
                .andExpect(status().isOk());

        Member member = memberRepository.findByLoginId("testID").orElseThrow();
        assertThat(member.getNickname()).isEqualTo("testNickName");
    }

    @Test
    void 로그인_성공() throws Exception {
        Long memberId = memberService.join(new MemberCreateRequest("test999", "1234", "nickname999"));

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "loginId":"test999",
                          "password":"1234"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(header().exists("Authorization"));
    }

    @Test
    void 단일회원_조회() throws Exception {

        Long memberId = memberService.join(new MemberCreateRequest("test999", "1234", "nickname999"));
        String token = jwtTokenProvider.createJwt("test999", "ROLE_USER", 1000L * 60 * 10);

        mockMvc.perform(get("/members/{memberId}", memberId)
                    .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk());
    }

    @Test
    void 존재하지_않는_회원_조회() throws Exception {

        Long memberId = memberService.join(new MemberCreateRequest("test999", "1234", "nickname999"));
        String token = jwtTokenProvider.createJwt("test999", "ROLE_USER", 1000L * 60 * 10);

        mockMvc.perform(get("/members/{memberId}", 9999L)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void 전체_회원_조회() throws Exception {

        Long memberId = memberService.join(new MemberCreateRequest("test999", "1234", "nickname999"));
        String token = jwtTokenProvider.createJwt("test999", "ROLE_USER", 1000L * 60 * 10);

        mockMvc.perform(get("/members")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void 회원_정보_수정() throws Exception {

        Long memberId = memberService.join(new MemberCreateRequest("test999", "1234", "nickname999"));
        String token = jwtTokenProvider.createJwt("test999", "ROLE_USER", 1000L * 60 * 10);

        mockMvc.perform(patch("/members/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content("""
                        {
                          "nickname" : "updateNickname"
                        }
                        """))
                .andExpect(status().isOk());

        Member member = memberService.findOne(memberId);

        assertThat(member.getNickname()).isEqualTo("updateNickname");
    }

    @Test
    void 회원_정보_JWT없이_수정() throws Exception {

        Long memberId = memberService.join(new MemberCreateRequest("test999", "1234", "nickname999"));

        mockMvc.perform(patch("/members/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "nickname" : "updateNickname"
                        }
                        """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void 회원_삭제() throws Exception {

        Long memberId = memberService.join(new MemberCreateRequest("test999", "1234", "nickname999"));
        String token = jwtTokenProvider.createJwt("test999", "ROLE_USER", 1000L * 60 * 10);

        mockMvc.perform(delete("/members/me")
                    .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        assertThatThrownBy(() -> memberService.findOne(memberId)).isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    void 회원_JWT없이_삭제() throws Exception {

        Long memberId = memberService.join(new MemberCreateRequest("test999", "1234", "nickname999"));

        mockMvc.perform(delete("/members/me"))
                .andExpect(status().isUnauthorized());

    }
}

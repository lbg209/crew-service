package com.lbg0146.crew_service.service;

import com.lbg0146.crew_service.dto.MemberCreateRequest;
import com.lbg0146.crew_service.domain.Member;
import com.lbg0146.crew_service.exception.DuplicateMemberException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class MemberServiceTest {

    @Autowired
    MemberService memberService;

    @Test
    public void 회원가입() {
        MemberCreateRequest member = new MemberCreateRequest("testID", "1234", "testNickname");

        Long saveId = memberService.join(member);
        Member findMember = memberService.findOne(saveId);
        assertThat(findMember.getNickname()).isEqualTo("testNickname");
    }

    @Test
    public void 중복_회원_예외() throws Exception {
        //given
        MemberCreateRequest member1 = new MemberCreateRequest("testID1", "1234", "testNickname1");
        MemberCreateRequest member2 = new MemberCreateRequest("testID1", "5678", "testNickname2");

        //when
        memberService.join(member1);

        //then
        assertThrows(DuplicateMemberException.class, () -> memberService.join(member2)); //예외 발생!!

    }

}
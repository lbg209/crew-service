package com.lbg0146.crew_service.service;

import com.lbg0146.crew_service.domain.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    MemberService memberService;

    @Test
    public void 회원가입() {
        Member member = new Member();
        member.setNickname("LEE");
        member.setLoginId("lbg");
        member.setPassword("1234");

        Long saveId = memberService.join(member);
        Member findMember = memberService.findOne(saveId);
        assertThat(findMember.getNickname()).isEqualTo("LEE");
    }

    @Test
    public void 중복_회원_예외() throws Exception {/*
        //given
        Member member1 = new Member();
        member1.setNickname("LEE");
        member1.setLoginId("lbg");
        member1.setPassword("1234");

        Member member2 = new Member();
        member2.setNickname("LEE");
        member2.setLoginId("kim");
        member2.setPassword("7894");

        //when
        memberService.join(member1);

        //then
        assertThrows(IllegalArgumentException.class, () -> memberService.join(member2)); //예외 발생!!
        */
    }

}
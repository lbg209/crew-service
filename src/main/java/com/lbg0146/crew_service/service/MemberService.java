package com.lbg0146.crew_service.service;

import com.lbg0146.crew_service.domain.Member;
import com.lbg0146.crew_service.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public Long join(Member member) {
        memberRepository.save(member);
        return member.getId();
    }

    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    public Member findOne(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(()-> new IllegalArgumentException("존재하지 않는 회원입니다."));
    }

    @Transactional
    public void update(Long id, String name) {
        Member member = memberRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        member.setNickname(name);
    }
}

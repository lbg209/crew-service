package com.lbg0146.crew_service.service;

import com.lbg0146.crew_service.aop.ExecutionTime;
import com.lbg0146.crew_service.dto.MemberCreateRequest;
import com.lbg0146.crew_service.domain.Member;
import com.lbg0146.crew_service.exception.DuplicateMemberException;
import com.lbg0146.crew_service.exception.MemberNotFoundException;
import com.lbg0146.crew_service.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @ExecutionTime
    @Transactional
    public Long join(MemberCreateRequest request) {
        Boolean isExist = memberRepository.existsByLoginId(request.getLoginId());

        if (isExist) {
            throw new DuplicateMemberException();
        }

        String encodedPassword = bCryptPasswordEncoder.encode(request.getPassword());
        Member member = new Member(request.getLoginId(), encodedPassword, request.getNickname());

        memberRepository.save(member);
        return member.getId();
    }

    public Page<Member> findMembers(Pageable pageable) {
        return memberRepository.findAll(pageable);
    }

    public Member findOne(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(()-> new MemberNotFoundException());
    }

    @Transactional
    public Member update(Long memberId, String nickname) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new MemberNotFoundException());

        // 더티 체킹 메서드가 끝나면 트랜잭션 커밋
        member.update(nickname);

        return member;
    }

    @Transactional
    public void delete(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new MemberNotFoundException());

        memberRepository.delete(member);
    }
}

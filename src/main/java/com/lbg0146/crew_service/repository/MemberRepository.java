package com.lbg0146.crew_service.repository;

import com.lbg0146.crew_service.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    /*
    memberRepository.save(member);
    memberRepository.findById(id);
    memberRepository.findAll();
    memberRepository.delete(member);
    memberRepository.existsById(id);
     */
}

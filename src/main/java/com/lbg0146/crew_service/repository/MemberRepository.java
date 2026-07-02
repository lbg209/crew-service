package com.lbg0146.crew_service.repository;

import com.lbg0146.crew_service.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Boolean existsByLoginId(String loginId);

    Optional<Member> findByLoginId(String loginId);

    /*
    memberRepository.save(member);
    memberRepository.findById(id);
    memberRepository.findAll();
    memberRepository.delete(member);
    memberRepository.existsById(id);
     */
}

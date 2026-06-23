package com.lbg0146.crew_service.controller;

import com.lbg0146.crew_service.dto.MemberCreateRequest;
import com.lbg0146.crew_service.dto.MemberResponse;
import com.lbg0146.crew_service.dto.MemberUpdateRequest;
import com.lbg0146.crew_service.domain.Member;
import com.lbg0146.crew_service.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    public Long join(@RequestBody MemberCreateRequest request) {

        return memberService.join(request);
    }

    @GetMapping("/{memberId}")
    public MemberResponse findOne(@PathVariable Long memberId) {
        Member member = memberService.findOne(memberId);

        return new MemberResponse(member);
    }

    @GetMapping
    public List<MemberResponse> findAll() {
        List<Member> members = memberService.findMembers();

        return members.stream()
                .map((member) -> new MemberResponse(member))
                .toList();
    }

    @PatchMapping("/{memberId}")
    public MemberResponse update(@PathVariable Long memberId, @RequestBody MemberUpdateRequest request) {
        memberService.update(memberId, request.getNickname());

        return new MemberResponse(memberService.findOne(memberId));
    }

    @DeleteMapping("/{memberId}")
    public void delete(@PathVariable Long memberId) {
        memberService.delete(memberId);
    }
}

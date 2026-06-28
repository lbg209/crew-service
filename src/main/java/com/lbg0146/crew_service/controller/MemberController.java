package com.lbg0146.crew_service.controller;

import com.lbg0146.crew_service.dto.MemberCreateRequest;
import com.lbg0146.crew_service.dto.MemberResponse;
import com.lbg0146.crew_service.dto.MemberUpdateRequest;
import com.lbg0146.crew_service.domain.Member;
import com.lbg0146.crew_service.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Member API", description = "회원 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "회원 가입", description = "아이디와 닉네임을 입력받아 새로운 회원을 생성합니다.")
    @PostMapping
    public Long join(@RequestBody MemberCreateRequest request) {

        return memberService.join(request);
    }

    @Operation(summary = "단일 회원 조회")
    @GetMapping("/{memberId}")
    public MemberResponse findOne(@PathVariable Long memberId) {
        Member member = memberService.findOne(memberId);

        return new MemberResponse(member);
    }

    @Operation(summary = "전체 회원 조회")
    @GetMapping
    public Page<MemberResponse> findAll(@ParameterObject Pageable pageable) {
        Page<Member> members = memberService.findMembers(pageable);

        return members.map((member) -> new MemberResponse(member));
    }

    @Operation(summary = "회원 정보 변경", description = "닉네임을 변경합니다.")
    @PatchMapping("/{memberId}")
    public MemberResponse update(@PathVariable Long memberId, @RequestBody MemberUpdateRequest request) {
        memberService.update(memberId, request.getNickname());

        return new MemberResponse(memberService.findOne(memberId));
    }

    @Operation(summary = "회원 삭제")
    @DeleteMapping("/{memberId}")
    public void delete(@PathVariable Long memberId) {
        memberService.delete(memberId);
    }
}

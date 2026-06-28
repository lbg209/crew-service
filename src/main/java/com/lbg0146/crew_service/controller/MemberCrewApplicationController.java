package com.lbg0146.crew_service.controller;

import com.lbg0146.crew_service.domain.MemberCrewApplication;
import com.lbg0146.crew_service.domain.enums.ApplicationStatus;
import com.lbg0146.crew_service.dto.ApplicationRequest;
import com.lbg0146.crew_service.dto.ApplicationResponse;
import com.lbg0146.crew_service.service.MemberCrewApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Application API", description = "크루 신청 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/applications")
public class MemberCrewApplicationController {

    private final MemberCrewApplicationService applicationService;

    @Operation(summary = "크루 신청", description = "멤버ID와 크루ID를 받아 PENDING 상태의 신청을 생성합니다.")
    @PostMapping()
    public Long apply(@RequestBody ApplicationRequest request) {
        return applicationService.apply(request.getMemberId(), request.getCrewId());
    }

    @Operation(summary = "크루 신청 승인", description = "크루장이 신청을 승인하며 신청 상태가 APPROVED로 변경됩니다.")
    @PatchMapping("/{applicationId}/approve")
    public void approve(@PathVariable Long applicationId, @RequestParam Long leaderId) {
        applicationService.approve(applicationId, leaderId);
    }

    @Operation(summary = "크루 신청 거절", description = "크루장이 신청을 거절하며 신청 상태가 REJECTED로 변경됩니다.")
    @PatchMapping("/{applicationId}/reject")
    public void reject(@PathVariable Long applicationId, @RequestParam Long leaderId) {
        applicationService.reject(applicationId, leaderId);
    }

    @Operation(summary = "크루 신청 취소", description = "신청한 멤버가 신청을 취소하며 PENDING 상태의 신청만 취소할 수 있습니다.")
    @DeleteMapping("/{applicationId}")
    public void cancel(@PathVariable Long applicationId, @RequestParam Long memberId) {
        applicationService.cancel(applicationId, memberId);
    }

    @Operation(summary = "크루의 모든 신청 조회", description = "신청 상태 조건으로 검색 가능하며 크루의 모든 신청을 조회합니다.")
    @GetMapping("/crew/{crewId}")
    public Page<ApplicationResponse> findApplicationsCrew(@PathVariable Long crewId, @RequestParam(required = false) ApplicationStatus status, Pageable pageable) {
        Page<MemberCrewApplication> applications = applicationService.findApplicationsByCrew(crewId, status, pageable);

        return applications.map((application) -> new ApplicationResponse(application));
    }

    @Operation(summary = "멤버의 모든 신청 조회", description = "신청 상태 조건으로 검색 가능하며 멤버의 모든 신청을 조회합니다.")
    @GetMapping("/member/{memberId}")
    public Page<ApplicationResponse> findMemberApplications(@PathVariable Long memberId, @RequestParam(required = false) ApplicationStatus status ,Pageable pageable) {
        Page<MemberCrewApplication> applications = applicationService.findApplicationsByMember(memberId, status, pageable);

        return applications.map((application) -> new ApplicationResponse(application));
    }
}

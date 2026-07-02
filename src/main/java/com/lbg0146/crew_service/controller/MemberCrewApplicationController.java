package com.lbg0146.crew_service.controller;

import com.lbg0146.crew_service.domain.MemberCrewApplication;
import com.lbg0146.crew_service.domain.enums.ApplicationStatus;
import com.lbg0146.crew_service.dto.ApplicationRequest;
import com.lbg0146.crew_service.dto.ApplicationResponse;
import com.lbg0146.crew_service.security.CustomUserDetails;
import com.lbg0146.crew_service.service.MemberCrewApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Application API", description = "크루 신청 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/applications")
public class MemberCrewApplicationController {

    private final MemberCrewApplicationService applicationService;

    @Operation(summary = "크루 신청", description = "크루ID에 PENDING 상태의 신청을 생성합니다.")
    @PostMapping()
    public Long apply(@RequestBody ApplicationRequest request, @AuthenticationPrincipal CustomUserDetails user) {
        return applicationService.apply(user.getId(), request.getCrewId());
    }

    @Operation(summary = "크루 신청 승인", description = "크루장이 신청을 승인하며 신청 상태가 APPROVED로 변경됩니다.")
    @PatchMapping("/{applicationId}/approve")
    public void approve(@PathVariable Long applicationId, @AuthenticationPrincipal CustomUserDetails user) {
        applicationService.approve(applicationId, user.getId());
    }

    @Operation(summary = "크루 신청 거절", description = "크루장이 신청을 거절하며 신청 상태가 REJECTED로 변경됩니다.")
    @PatchMapping("/{applicationId}/reject")
    public void reject(@PathVariable Long applicationId, @AuthenticationPrincipal CustomUserDetails user) {
        applicationService.reject(applicationId, user.getId());
    }

    @Operation(summary = "크루 신청 취소", description = "신청한 멤버가 신청을 취소하며 PENDING 상태의 신청만 취소할 수 있습니다.")
    @DeleteMapping("/{applicationId}")
    public void cancel(@PathVariable Long applicationId, @AuthenticationPrincipal CustomUserDetails user) {
        applicationService.cancel(applicationId, user.getId());
    }

    @Operation(summary = "크루의 모든 신청 조회", description = "신청 상태 조건으로 검색 가능하며 크루의 모든 신청을 조회합니다.")
    @GetMapping("/crew/{crewId}")
    public Page<ApplicationResponse> findApplicationsCrew(@PathVariable Long crewId,
                                                          @RequestParam(required = false) ApplicationStatus status,
                                                          @ParameterObject Pageable pageable,
                                                          @AuthenticationPrincipal CustomUserDetails user) {

        Page<MemberCrewApplication> applications = applicationService.findApplicationsByCrew(crewId, user.getId(), status, pageable);

        return applications.map((application) -> new ApplicationResponse(application));
    }

    @Operation(summary = "내 신청 조회", description = "로그인한 사용자의 신청 목록을 조회합니다.")
    @GetMapping("/me")
    public Page<ApplicationResponse> findMemberApplications(@RequestParam(required = false) ApplicationStatus status,
                                                            @ParameterObject Pageable pageable,
                                                            @AuthenticationPrincipal CustomUserDetails user) {

        Page<MemberCrewApplication> applications = applicationService.findApplicationsByMember(user.getId(), status, pageable);

        return applications.map((application) -> new ApplicationResponse(application));
    }
}

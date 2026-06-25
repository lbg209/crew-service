package com.lbg0146.crew_service.controller;

import com.lbg0146.crew_service.domain.MemberCrewApplication;
import com.lbg0146.crew_service.dto.ApplicationRequest;
import com.lbg0146.crew_service.dto.ApplicationResponse;
import com.lbg0146.crew_service.service.MemberCrewApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/applications")
public class MemberCrewApplicationController {

    private final MemberCrewApplicationService applicationService;

    @PostMapping()
    public Long apply(@RequestBody ApplicationRequest request) {
        return applicationService.apply(request.getMemberId(), request.getCrewId());
    }

    @PatchMapping("/{id}/approve")
    public void approve(@PathVariable Long id, @RequestParam Long leaderId) {
        applicationService.approve(id, leaderId);
    }

    @PatchMapping("/{id}/reject")
    public void reject(@PathVariable Long id, @RequestParam Long leaderId) {
        applicationService.reject(id, leaderId);
    }

    @GetMapping("/crew/{crewId}")
    public Page<ApplicationResponse> findApplicationsCrew(@PathVariable Long crewId, Pageable pageable) {
        Page<MemberCrewApplication> applications = applicationService.findApplicationsByCrew(crewId, pageable);

        return applications.map((application) -> new ApplicationResponse(application));
    }

    @GetMapping("/member/{memberId}")
    public Page<ApplicationResponse> findMemberApplications(@PathVariable Long memberId, Pageable pageable) {
        Page<MemberCrewApplication> applications = applicationService.findApplicationsByMember(memberId, pageable);

        return applications.map((application) -> new ApplicationResponse(application));
    }
}

package com.lbg0146.crew_service.controller;

import com.lbg0146.crew_service.dto.MemberCrewApplicationRequest;
import com.lbg0146.crew_service.service.MemberCrewApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/applications")
public class MemberCrewApplicationController {

    private final MemberCrewApplicationService applicationService;

    @PostMapping()
    public Long apply(@RequestBody MemberCrewApplicationRequest request) {
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
}

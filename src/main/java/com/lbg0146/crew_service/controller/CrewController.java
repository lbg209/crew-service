package com.lbg0146.crew_service.controller;

import com.lbg0146.crew_service.dto.*;
import com.lbg0146.crew_service.domain.Crew;
import com.lbg0146.crew_service.service.CrewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/crews")
public class CrewController {

    private final CrewService crewService;

    @PostMapping()
    public Long create(@RequestBody CrewCreateRequest request) {
        return crewService.createCrew(request);
    }

    @GetMapping("/{crewId}")
    public CrewResponse findOne(@PathVariable Long crewId) {
        Crew crew = crewService.findOne(crewId);

        return new CrewResponse(crew);
    }

    @GetMapping()
    public Page<CrewResponse> findAll(Pageable pageable) {
        Page<Crew> crews = crewService.findCrews(pageable);

        return crews.map((crew) -> new CrewResponse(crew));
    }

    @PatchMapping("/{crewId}")
    public CrewResponse update(@PathVariable Long crewId, @RequestBody CrewUpdateRequest request) {
        crewService.update(crewId, request.getMemberId(), request.getSubCategory(), request.getRegion(), request.getTitle(), request.getDescription(), request.getMaxMemberCount());

        return new CrewResponse(crewService.findOne(crewId));
    }

    @DeleteMapping("/{crewId}")
    public void delete(@PathVariable Long crewId, @RequestParam Long memberId) {
        crewService.delete(crewId, memberId);
    }

    @PatchMapping("/{crewId}/recruitment")
    public void changeRecruitment(@PathVariable Long crewId, @RequestBody RecruitmentStatusRequest request) {
        crewService.changeRecruitmentStatus(crewId, request.getMemberId(), request.getStatus());
    }

    @GetMapping("/search")
    public Page<CrewResponse> search(@ModelAttribute CrewSearchCondition condition, Pageable pageable) {
        return crewService.search(condition, pageable).map((crew)-> new CrewResponse(crew));
    }
}

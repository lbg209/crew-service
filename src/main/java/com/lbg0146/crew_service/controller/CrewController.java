package com.lbg0146.crew_service.controller;

import com.lbg0146.crew_service.dto.CrewCreateRequest;
import com.lbg0146.crew_service.dto.CrewResponse;
import com.lbg0146.crew_service.dto.CrewSearchCondition;
import com.lbg0146.crew_service.dto.CrewUpdateRequest;
import com.lbg0146.crew_service.domain.Crew;
import com.lbg0146.crew_service.service.CrewService;
import lombok.RequiredArgsConstructor;
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
    public List<CrewResponse> findAll() {
        List<Crew> crews = crewService.findCrews();

        return crews.stream()
                .map((crew) -> new CrewResponse(crew))
                .toList();
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

    @GetMapping("/search")
    public List<CrewResponse> search(@ModelAttribute CrewSearchCondition condition) {
        return crewService.search(condition)
                .stream()
                .map((crew)-> new CrewResponse(crew))
                .toList();
    }
}

package com.lbg0146.crew_service.controller;

import com.lbg0146.crew_service.dto.*;
import com.lbg0146.crew_service.domain.Crew;
import com.lbg0146.crew_service.security.CustomUserDetails;
import com.lbg0146.crew_service.service.CrewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Crew API", description = "크루 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/crews")
public class CrewController {

    private final CrewService crewService;

    @Operation(summary = "크루 생성", description = "로그인 후 입력된 데이터를 받아 새로운 크루가 생성되며 생성한 멤버가 리더로 설정됩니다.")
    @PostMapping()
    public Long create(@Valid @RequestBody CrewCreateRequest request, @AuthenticationPrincipal CustomUserDetails user) {
            return crewService.createCrew(user.getId(), request);
    }

    @Operation(summary = "단일 크루 조회")
    @GetMapping("/{crewId}")
    public CrewResponse findOne(@PathVariable Long crewId) {
        Crew crew = crewService.findOne(crewId);

        return new CrewResponse(crew);
    }

    @Operation(summary = "전체 크루 조회")
    @GetMapping()
    public Page<CrewResponse> findAll(@ParameterObject Pageable pageable) {
        Page<Crew> crews = crewService.findCrews(pageable);

        return crews.map((crew) -> new CrewResponse(crew));
    }

    @Operation(summary = "크루 정보 변경", description = "크루 정보를 수정합니다. 리더만 수정할 수 있으며 일부 필드는 변경 제한이 있습니다.")
    @PatchMapping("/{crewId}")
    public CrewResponse update(@PathVariable Long crewId, @Valid @RequestBody CrewUpdateRequest request, @AuthenticationPrincipal CustomUserDetails user) {
        Crew updateCrew = crewService.update(crewId, user.getId(), request.getSubCategory(), request.getRegion(), request.getTitle(), request.getDescription(), request.getMaxMemberCount());

        return new CrewResponse(updateCrew);
    }

    @Operation(summary = "크루 삭제")
    @DeleteMapping("/{crewId}")
    public void delete(@PathVariable Long crewId, @AuthenticationPrincipal CustomUserDetails user) {
        crewService.delete(crewId, user.getId());
    }

    @Operation(summary = "크루 모집 상태 변경", description = "모집, 마감 상태가 있으며 리더만 변경가능합니다.")
    @PatchMapping("/{crewId}/recruitment")
    public void changeRecruitment(@PathVariable Long crewId, @Valid @RequestBody RecruitmentStatusRequest request, @AuthenticationPrincipal CustomUserDetails user) {
        crewService.changeRecruitmentStatus(crewId, user.getId(), request.getStatus());
    }

    @Operation(summary = "크루 조건 검색", description = "조건 DTO를 통해 해당 조건의 크루 검색합니다.")
    @GetMapping("/search")
    public Page<CrewResponse> search(@ModelAttribute CrewSearchCondition condition,@ParameterObject Pageable pageable) {
        return crewService.search(condition, pageable).map((crew)-> new CrewResponse(crew));
    }

    @Operation(summary = "크루 탈퇴", description = "APPROVED 상태의 멤버만 크루 탈퇴 가능합니다.")
    @DeleteMapping("/{crewId}/leave")
    public void leave(@PathVariable Long crewId, @AuthenticationPrincipal CustomUserDetails user) {
        crewService.leave(crewId, user.getId());
    }

    @Operation(summary = "크루 리더 변경", description = "크루장을 통해 리더 변경합니다.")
    @PatchMapping("/{crewId}/leader")
    public void changeLeader(@PathVariable Long crewId, @Valid @RequestBody CrewLeaderChangeRequest request, @AuthenticationPrincipal CustomUserDetails user) {
        crewService.changeLeader(crewId, user.getId(), request.getNewLeaderId());
    }
}

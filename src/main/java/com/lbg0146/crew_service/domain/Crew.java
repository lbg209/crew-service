package com.lbg0146.crew_service.domain;

import com.lbg0146.crew_service.domain.enums.RecruitmentStatus;
import com.lbg0146.crew_service.domain.enums.Region;
import com.lbg0146.crew_service.domain.enums.SubCategory;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Crew extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "crew_id")
    private Long id;

    @OneToMany(mappedBy = "crew")
    private List<MemberCrewApplication> applications = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leader_id", nullable = false)
    private Member leader;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubCategory subCategory;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Region region;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RecruitmentStatus recruitmentStatus = RecruitmentStatus.RECRUITING;

    @Column(nullable = false, length = 100)
    private String title;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private int maxMemberCount;
    @Column(nullable = false)
    private int currentMemberCount = 0;

    protected Crew(){
    }

    public Crew(Member leader, SubCategory subCategory, Region region, String title, String description, int maxMemberCount) {
        this.leader = leader;
        this.subCategory = subCategory;
        this.region = region;
        this.title = title;
        this.description = description;
        this.maxMemberCount = maxMemberCount;
        this.currentMemberCount = 1;
    }

    public void increaseMemberCount() {
        if (currentMemberCount >= maxMemberCount) {
            throw new IllegalStateException("정원이 가득찼습니다.");
        }
        this.currentMemberCount++;

        if (currentMemberCount == maxMemberCount) {
            this.recruitmentStatus = RecruitmentStatus.CLOSED;
            // 정원이 가득 차면 자동으로 마감상태로 변경
        }
    }

    public void decreaseMemberCount() {
        this.currentMemberCount--;
    }

    public void changeRecruitmentStatus(RecruitmentStatus status) {
        this.recruitmentStatus = status;
    }
}

package com.lbg0146.crew_service.domain;

import com.lbg0146.crew_service.domain.enums.ApplicationStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"member_id", "crew_id"})})
public class MemberCrewApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crew_id")
    private Crew crew;

    private LocalDateTime appliedAt;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;

    public void approve() {
        if (this.status != ApplicationStatus.PENDING) {
            throw new IllegalStateException("대기중인 신청만 승인 가능합니다.");
        }

        this.status = ApplicationStatus.APPROVED;
    }

    public void reject() {
        if (this.status != ApplicationStatus.PENDING) {
            throw new IllegalStateException("대기중인 신청만 거절 가능합니다.");
        }

        this.status = ApplicationStatus.REJECTED;
    }

    public static MemberCrewApplication create(Member member, Crew crew) {
        MemberCrewApplication application = new MemberCrewApplication();

        application.member = member;
        application.crew = crew;
        application.appliedAt = LocalDateTime.now();
        application.status = ApplicationStatus.PENDING;

        return application;
    }
}

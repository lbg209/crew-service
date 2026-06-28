package com.lbg0146.crew_service.repository;

import com.lbg0146.crew_service.domain.MemberCrewApplication;
import com.lbg0146.crew_service.domain.enums.ApplicationStatus;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.lbg0146.crew_service.domain.QMemberCrewApplication.*;

@Repository
@RequiredArgsConstructor
public class MemberCrewApplicationRepositoryImpl implements MemberCrewApplicationRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<MemberCrewApplication> searchByCrew(Long crewId, ApplicationStatus status, Pageable pageable) {

        List<MemberCrewApplication> content = queryFactory.selectFrom(memberCrewApplication)
                .leftJoin(memberCrewApplication.member).fetchJoin()
                .leftJoin(memberCrewApplication.crew).fetchJoin()
                .where(
                        memberCrewApplication.crew.id.eq(crewId),
                        statusEq(status)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = queryFactory.select(memberCrewApplication.count())
                .from(memberCrewApplication)
                .where(
                        memberCrewApplication.crew.id.eq(crewId),
                        statusEq(status)
                )
                .fetchOne();

        return new PageImpl<MemberCrewApplication>(content, pageable, count);
    }




    @Override
    public Page<MemberCrewApplication> searchByMember(Long memberId, ApplicationStatus status, Pageable pageable) {

        List<MemberCrewApplication> content = queryFactory.selectFrom(memberCrewApplication)
                .leftJoin(memberCrewApplication.member).fetchJoin()
                .leftJoin(memberCrewApplication.crew).fetchJoin()
                .where(
                        memberCrewApplication.member.id.eq(memberId),
                        statusEq(status)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = queryFactory.select(memberCrewApplication.count())
                .from(memberCrewApplication)
                .where(
                        memberCrewApplication.member.id.eq(memberId),
                        statusEq(status)
                )
                .fetchOne();

        return new PageImpl<MemberCrewApplication>(content, pageable, count);

    }


    private BooleanExpression statusEq(ApplicationStatus status) {
        return status != null ? memberCrewApplication.status.eq(status) : null;
    }
}

package com.lbg0146.crew_service.repository;

import com.lbg0146.crew_service.domain.Crew;
import com.lbg0146.crew_service.domain.enums.RecruitmentStatus;
import com.lbg0146.crew_service.domain.enums.Region;
import com.lbg0146.crew_service.domain.enums.SubCategory;
import com.lbg0146.crew_service.dto.CrewSearchCondition;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.lbg0146.crew_service.domain.QCrew.crew;

@Repository
@RequiredArgsConstructor
public class CrewRepositoryImpl implements CrewRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Crew> search(CrewSearchCondition condition, Pageable pageable) {
        List<Crew> content = queryFactory
                .selectFrom(crew)
                .where(
                        regionEq(condition.getRegion()),
                        subCategoryEq(condition.getSubCategory()),
                        titleContains(condition.getTitle()),
                        recruitmentStatusEq(condition.getRecruitmentStatus())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = queryFactory
                .select(crew.count())
                .from(crew)
                .where(
                        regionEq(condition.getRegion()),
                        subCategoryEq(condition.getSubCategory()),
                        titleContains(condition.getTitle()),
                        recruitmentStatusEq(condition.getRecruitmentStatus())
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, count);
    }



    private BooleanExpression regionEq(Region region) {
        return region != null ? crew.region.eq(region) : null;
    }

    private BooleanExpression subCategoryEq(SubCategory subCategory) {
        return subCategory != null ? crew.subCategory.eq(subCategory) : null;
    }

    private BooleanExpression titleContains(String title) {
        return StringUtils.hasText(title) ? crew.title.contains(title) : null;
    }

    private BooleanExpression recruitmentStatusEq(RecruitmentStatus status) {
        return status != null ? crew.recruitmentStatus.eq(status) : null;
    }
}

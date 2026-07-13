package com.lbg0146.crew_service.repository;

import com.lbg0146.crew_service.domain.Crew;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CrewRepository extends JpaRepository<Crew, Long>, CrewRepositoryCustom {
    //Page<T> findAll(Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE) // 비관적 락
    @Query("select c from Crew c where c.id = :crewId")
    Optional<Crew> findByIdWithLock(@Param("crewId") Long crewId);
}

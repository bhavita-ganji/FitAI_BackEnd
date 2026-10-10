package com.fitai.repository;

import com.fitai.entity.WorkoutSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface WorkoutSessionRepository extends JpaRepository<WorkoutSession, Long> {
    List<WorkoutSession> findByUserIdOrderBySessionDateDescCreatedAtDesc(Long userId);

    Page<WorkoutSession> findByUserIdOrderBySessionDateDesc(Long userId, Pageable pageable);

    List<WorkoutSession> findByUserIdAndSessionDateBetweenOrderBySessionDateAsc(Long userId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT COUNT(s) FROM WorkoutSession s WHERE s.user.id = :userId AND s.status = 'COMPLETED'")
    long countCompletedSessionsByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(s) FROM WorkoutSession s WHERE s.status = 'COMPLETED'")
    long countAllCompletedSessions();
}

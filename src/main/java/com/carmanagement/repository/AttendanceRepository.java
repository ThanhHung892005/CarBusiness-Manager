package com.carmanagement.repository;

import com.carmanagement.entity.Attendance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    boolean existsByEmployeeIdAndDate(Long employeeId, LocalDate date);

    @Query("""
        SELECT a FROM Attendance a
        JOIN FETCH a.employee e
        JOIN FETCH e.user
        WHERE a.id = :id
        """)
    Optional<Attendance> findWithDetailsById(@Param("id") Long id);

    @Query("""
        SELECT a FROM Attendance a
        WHERE a.employee.id = :employeeId
          AND a.date >= :startDate
          AND a.date <= :endDate
        ORDER BY a.date
        """)
    List<Attendance> findByEmployeeAndDateRange(@Param("employeeId") Long employeeId,
                                               @Param("startDate") LocalDate startDate,
                                               @Param("endDate") LocalDate endDate);

    @Query(value = """
        SELECT a FROM Attendance a
        JOIN FETCH a.employee e
        JOIN FETCH e.user
        WHERE (:employeeId IS NULL OR a.employee.id = :employeeId)
          AND (:startDate IS NULL OR a.date >= :startDate)
          AND (:endDate IS NULL OR a.date <= :endDate)
        """,
        countQuery = """
        SELECT COUNT(a) FROM Attendance a
        WHERE (:employeeId IS NULL OR a.employee.id = :employeeId)
          AND (:startDate IS NULL OR a.date >= :startDate)
          AND (:endDate IS NULL OR a.date <= :endDate)
        """)
    Page<Attendance> search(@Param("employeeId") Long employeeId,
                            @Param("startDate") LocalDate startDate,
                            @Param("endDate") LocalDate endDate,
                            Pageable pageable);
}

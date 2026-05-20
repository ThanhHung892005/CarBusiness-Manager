package com.carmanagement.repository;

import com.carmanagement.entity.Payroll;
import com.carmanagement.enums.PayrollStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PayrollRepository extends JpaRepository<Payroll, Long> {

    Optional<Payroll> findByEmployeeIdAndMonthAndYear(Long employeeId, int month, int year);

    @Query("""
        SELECT p FROM Payroll p
        JOIN FETCH p.employee e
        JOIN FETCH e.user
        WHERE p.id = :id
        """)
    Optional<Payroll> findWithDetailsById(@Param("id") Long id);

    @Query(value = """
        SELECT p FROM Payroll p
        JOIN FETCH p.employee e
        JOIN FETCH e.user
        WHERE (:month IS NULL OR p.month = :month)
          AND (:year IS NULL OR p.year = :year)
          AND (:status IS NULL OR p.status = :status)
        """,
        countQuery = """
        SELECT COUNT(p) FROM Payroll p
        WHERE (:month IS NULL OR p.month = :month)
          AND (:year IS NULL OR p.year = :year)
          AND (:status IS NULL OR p.status = :status)
        """)
    Page<Payroll> search(@Param("month") Integer month,
                         @Param("year") Integer year,
                         @Param("status") PayrollStatus status,
                         Pageable pageable);
}

package com.carmanagement.repository;

import com.carmanagement.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByEmployeeCode(String employeeCode);

    Optional<Employee> findByUserId(Long userId);

    Optional<Employee> findTopByOrderByIdDesc();

    @Query("""
        SELECT e FROM Employee e
        JOIN FETCH e.user
        LEFT JOIN FETCH e.showroom
        LEFT JOIN FETCH e.department
        WHERE e.id = :id
        """)
    Optional<Employee> findWithDetailsById(@Param("id") Long id);

    @Query(value = """
        SELECT e FROM Employee e
        JOIN FETCH e.user u
        LEFT JOIN FETCH e.showroom
        LEFT JOIN FETCH e.department
        WHERE (:keyword IS NULL
               OR LOWER(e.employeeCode) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%'))
               OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%')))
          AND (:showroomId IS NULL OR e.showroom.id = :showroomId)
          AND (:deptId IS NULL OR e.department.id = :deptId)
          AND e.active = true
        """,
        countQuery = """
        SELECT COUNT(e) FROM Employee e
        JOIN e.user u
        WHERE (:keyword IS NULL
               OR LOWER(e.employeeCode) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%'))
               OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%')))
          AND (:showroomId IS NULL OR e.showroom.id = :showroomId)
          AND (:deptId IS NULL OR e.department.id = :deptId)
          AND e.active = true
        """)
    Page<Employee> search(@Param("keyword") String keyword,
                          @Param("showroomId") Long showroomId,
                          @Param("deptId") Long deptId,
                          Pageable pageable);
}

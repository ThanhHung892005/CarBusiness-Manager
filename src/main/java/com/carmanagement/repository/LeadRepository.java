package com.carmanagement.repository;

import com.carmanagement.entity.Lead;
import com.carmanagement.enums.LeadSource;
import com.carmanagement.enums.LeadStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LeadRepository extends JpaRepository<Lead, Long> {

    @Query("""
        SELECT l FROM Lead l
        LEFT JOIN FETCH l.assignedEmployee ae
        LEFT JOIN FETCH ae.user
        LEFT JOIN FETCH l.convertedCustomer
        WHERE l.id = :id
        """)
    Optional<Lead> findWithDetailsById(@Param("id") Long id);

    @Query(value = """
        SELECT l FROM Lead l
        LEFT JOIN FETCH l.assignedEmployee ae
        LEFT JOIN FETCH ae.user
        WHERE (:keyword IS NULL
               OR LOWER(l.fullName) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%'))
               OR LOWER(l.phone) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%')))
          AND (:status IS NULL OR l.status = :status)
          AND (:source IS NULL OR l.source = :source)
        """,
        countQuery = """
        SELECT COUNT(l) FROM Lead l
        WHERE (:keyword IS NULL
               OR LOWER(l.fullName) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%'))
               OR LOWER(l.phone) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%')))
          AND (:status IS NULL OR l.status = :status)
          AND (:source IS NULL OR l.source = :source)
        """)
    Page<Lead> search(@Param("keyword") String keyword,
                      @Param("status") LeadStatus status,
                      @Param("source") LeadSource source,
                      Pageable pageable);
}

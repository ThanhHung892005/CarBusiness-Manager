package com.carmanagement.repository;

import com.carmanagement.entity.AuditLog;
import com.carmanagement.enums.AuditAction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    @Query("""
        SELECT al FROM AuditLog al
        WHERE (:action IS NULL OR al.action = :action)
          AND LOWER(al.username) LIKE LOWER(CONCAT('%', CAST(:username AS string), '%'))
          AND al.createdAt >= :from
          AND al.createdAt <= :to
        ORDER BY al.createdAt DESC
        """)
    Page<AuditLog> search(@Param("username") String username,
                          @Param("action") AuditAction action,
                          @Param("from") LocalDateTime from,
                          @Param("to") LocalDateTime to,
                          Pageable pageable);
}

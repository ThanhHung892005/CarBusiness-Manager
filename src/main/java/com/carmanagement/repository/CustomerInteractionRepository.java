package com.carmanagement.repository;

import com.carmanagement.entity.CustomerInteraction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerInteractionRepository extends JpaRepository<CustomerInteraction, Long> {

    @Query("""
        SELECT ci FROM CustomerInteraction ci
        LEFT JOIN FETCH ci.employee e
        LEFT JOIN FETCH e.user
        WHERE ci.lead.id = :leadId
        ORDER BY ci.interactionDate DESC
        """)
    List<CustomerInteraction> findByLeadIdWithDetails(@Param("leadId") Long leadId);
}

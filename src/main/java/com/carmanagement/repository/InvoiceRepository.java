package com.carmanagement.repository;

import com.carmanagement.entity.Invoice;
import com.carmanagement.enums.InvoiceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    Optional<Invoice> findByInvoiceCode(String invoiceCode);

    Optional<Invoice> findByOrderId(Long orderId);

    @Query(value = """
        SELECT DISTINCT i FROM Invoice i
        LEFT JOIN FETCH i.order o
        LEFT JOIN FETCH o.customer
        WHERE (:status IS NULL OR i.status = :status)
          AND (CAST(:keyword AS string) = '' OR
               LOWER(i.invoiceCode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
               LOWER(o.orderCode) LIKE LOWER(CONCAT('%', :keyword, '%')))
        ORDER BY i.issuedDate DESC
        """,
        countQuery = """
        SELECT COUNT(DISTINCT i) FROM Invoice i
        LEFT JOIN i.order o
        LEFT JOIN o.customer
        WHERE (:status IS NULL OR i.status = :status)
          AND (CAST(:keyword AS string) = '' OR
               LOWER(i.invoiceCode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
               LOWER(o.orderCode) LIKE LOWER(CONCAT('%', :keyword, '%')))
        """)
    Page<Invoice> search(@Param("keyword") String keyword,
                         @Param("status") InvoiceStatus status,
                         Pageable pageable);

    @Query("""
        SELECT i FROM Invoice i
        LEFT JOIN FETCH i.order o
        LEFT JOIN FETCH o.customer
        LEFT JOIN FETCH o.employee e
        LEFT JOIN FETCH e.user
        LEFT JOIN FETCH i.payments
        WHERE i.id = :id
        """)
    Optional<Invoice> findWithDetailsById(@Param("id") Long id);
}

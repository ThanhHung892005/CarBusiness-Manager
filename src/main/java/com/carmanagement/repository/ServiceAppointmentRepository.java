package com.carmanagement.repository;

import com.carmanagement.entity.ServiceAppointment;
import com.carmanagement.enums.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceAppointmentRepository extends JpaRepository<ServiceAppointment, Long> {

    @Query(value = """
        SELECT a FROM ServiceAppointment a
        JOIN FETCH a.customer c
        LEFT JOIN FETCH a.employee e
        LEFT JOIN FETCH e.user
        LEFT JOIN FETCH a.showroom
        WHERE (:keyword IS NULL
               OR LOWER(a.appointmentCode) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%'))
               OR LOWER(c.fullName) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%')))
          AND (:status IS NULL OR a.status = :status)
          AND a.appointmentDate >= :from
          AND a.appointmentDate <= :to
        """,
        countQuery = """
        SELECT COUNT(a) FROM ServiceAppointment a
        JOIN a.customer c
        WHERE (:keyword IS NULL
               OR LOWER(a.appointmentCode) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%'))
               OR LOWER(c.fullName) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%')))
          AND (:status IS NULL OR a.status = :status)
          AND a.appointmentDate >= :from
          AND a.appointmentDate <= :to
        """)
    Page<ServiceAppointment> search(@Param("keyword") String keyword,
                                    @Param("status") AppointmentStatus status,
                                    @Param("from") LocalDateTime from,
                                    @Param("to") LocalDateTime to,
                                    Pageable pageable);

    @Query("""
        SELECT a FROM ServiceAppointment a
        JOIN FETCH a.customer
        JOIN FETCH a.vehicle v
        JOIN FETCH v.carModel m
        JOIN FETCH m.brand
        LEFT JOIN FETCH a.employee e
        LEFT JOIN FETCH e.user
        LEFT JOIN FETCH a.showroom
        WHERE a.id = :id
        """)
    Optional<ServiceAppointment> findWithDetailsById(@Param("id") Long id);

    @Query("""
        SELECT a FROM ServiceAppointment a
        JOIN FETCH a.customer
        WHERE a.customer.user.username = :username
          AND a.appointmentDate >= :from
        ORDER BY a.appointmentDate ASC
        """)
    List<ServiceAppointment> findUpcomingByCustomerUsername(@Param("username") String username,
                                                            @Param("from") LocalDateTime from,
                                                            Pageable pageable);
}

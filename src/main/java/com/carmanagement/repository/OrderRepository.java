package com.carmanagement.repository;

import com.carmanagement.entity.Order;
import com.carmanagement.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderCode(String orderCode);

    @Query(value = """
        SELECT o FROM Order o
        JOIN FETCH o.customer c
        LEFT JOIN FETCH o.employee e
        LEFT JOIN FETCH e.user
        WHERE (:keyword IS NULL
               OR LOWER(o.orderCode) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%'))
               OR LOWER(c.fullName) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%')))
          AND (:status IS NULL OR o.status = :status)
          AND o.orderDate >= :from
          AND o.orderDate <= :to
        """,
        countQuery = """
        SELECT COUNT(o) FROM Order o
        JOIN o.customer c
        WHERE (:keyword IS NULL
               OR LOWER(o.orderCode) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%'))
               OR LOWER(c.fullName) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%')))
          AND (:status IS NULL OR o.status = :status)
          AND o.orderDate >= :from
          AND o.orderDate <= :to
        """)
    Page<Order> search(@Param("keyword") String keyword,
                       @Param("status") OrderStatus status,
                       @Param("from") LocalDateTime from,
                       @Param("to") LocalDateTime to,
                       Pageable pageable);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.status = 'COMPLETED' AND o.orderDate BETWEEN :from AND :to")
    BigDecimal sumRevenueByDateRange(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    long countByStatus(OrderStatus status);

    long countByOrderDateBetween(LocalDateTime from, LocalDateTime to);

    @Query("SELECT o FROM Order o JOIN FETCH o.customer WHERE o.customer.user.username = :username ORDER BY o.orderDate DESC")
    List<Order> findByCustomerUsername(@Param("username") String username, Pageable pageable);

    @Query("SELECT o FROM Order o JOIN FETCH o.customer ORDER BY o.orderDate DESC")
    List<Order> findRecentOrders(Pageable pageable);

    @Query(value = """
        SELECT CAST(EXTRACT(MONTH FROM order_date) AS int) AS month,
               COALESCE(SUM(total_amount), 0) AS revenue
        FROM orders
        WHERE status = 'COMPLETED'
          AND EXTRACT(YEAR FROM order_date) = :year
        GROUP BY CAST(EXTRACT(MONTH FROM order_date) AS int)
        ORDER BY month
        """, nativeQuery = true)
    List<Object[]> findMonthlyRevenue(@Param("year") int year);

    @Query("""
        SELECT o FROM Order o
        JOIN FETCH o.customer
        LEFT JOIN FETCH o.employee e
        LEFT JOIN FETCH e.user
        LEFT JOIN FETCH o.showroom
        LEFT JOIN FETCH o.orderItems oi
        JOIN FETCH oi.vehicle v
        JOIN FETCH v.carModel m
        JOIN FETCH m.brand
        WHERE o.id = :id
        """)
    Optional<Order> findWithDetailsById(@Param("id") Long id);
}

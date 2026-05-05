package com.carmanagement.repository;

import com.carmanagement.entity.Customer;
import com.carmanagement.enums.CustomerType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByCustomerCode(String customerCode);

    Optional<Customer> findByUserUsername(String username);

    Optional<Customer> findByPhone(String phone);

    boolean existsByPhone(String phone);

    boolean existsByEmail(String email);

    @Query("""
        SELECT c FROM Customer c
        WHERE (:keyword IS NULL
               OR LOWER(c.fullName) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%'))
               OR LOWER(c.phone) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%'))
               OR LOWER(c.email) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%')))
          AND (:type IS NULL OR c.customerType = :type)
        """)
    Page<Customer> search(@Param("keyword") String keyword,
                          @Param("type") CustomerType type,
                          Pageable pageable);
}

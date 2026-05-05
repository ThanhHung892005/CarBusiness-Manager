package com.carmanagement.service;

import com.carmanagement.dto.request.CustomerCreateRequest;
import com.carmanagement.entity.Customer;
import com.carmanagement.enums.CustomerType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomerService {
    Page<Customer> search(String keyword, CustomerType type, Pageable pageable);
    Customer findById(Long id);
    Customer create(CustomerCreateRequest request);
    Customer update(Long id, CustomerCreateRequest request);
    void addLoyaltyPoints(Long customerId, int points);
    String generateCustomerCode();
}

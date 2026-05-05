package com.carmanagement.service.impl;

import com.carmanagement.dto.request.CustomerCreateRequest;
import com.carmanagement.entity.Customer;
import com.carmanagement.enums.CustomerType;
import com.carmanagement.exception.BusinessException;
import com.carmanagement.exception.ResourceNotFoundException;
import com.carmanagement.repository.CustomerRepository;
import com.carmanagement.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    @Override
    public Page<Customer> search(String keyword, CustomerType type, Pageable pageable) {
        return customerRepository.search(keyword, type, pageable);
    }

    @Override
    public Customer findById(Long id) {
        return customerRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Customer", id));
    }

    @Override
    @Transactional
    public Customer create(CustomerCreateRequest req) {
        if (customerRepository.existsByPhone(req.getPhone())) {
            throw new BusinessException("Số điện thoại đã được đăng ký: " + req.getPhone());
        }

        Customer customer = Customer.builder()
            .customerCode(generateCustomerCode())
            .fullName(req.getFullName())
            .phone(req.getPhone())
            .email(req.getEmail())
            .address(req.getAddress())
            .city(req.getCity())
            .idNumber(req.getIdNumber())
            .taxCode(req.getTaxCode())
            .companyName(req.getCompanyName())
            .isCorporate(Boolean.TRUE.equals(req.getIsCorporate()))
            .notes(req.getNotes())
            .customerType(CustomerType.NEW)
            .loyaltyPoints(0)
            .build();

        return customerRepository.save(customer);
    }

    @Override
    @Transactional
    public Customer update(Long id, CustomerCreateRequest req) {
        Customer customer = findById(id);
        customer.setFullName(req.getFullName());
        customer.setEmail(req.getEmail());
        customer.setAddress(req.getAddress());
        customer.setCity(req.getCity());
        customer.setIdNumber(req.getIdNumber());
        customer.setTaxCode(req.getTaxCode());
        customer.setCompanyName(req.getCompanyName());
        customer.setNotes(req.getNotes());
        return customerRepository.save(customer);
    }

    @Override
    @Transactional
    public void addLoyaltyPoints(Long customerId, int points) {
        Customer customer = findById(customerId);
        int newPoints = customer.getLoyaltyPoints() + points;
        customer.setLoyaltyPoints(newPoints);

        // Upgrade tier
        if (newPoints >= 5000) {
            customer.setCustomerType(CustomerType.VIP);
        } else if (newPoints >= 1000) {
            customer.setCustomerType(CustomerType.REGULAR);
        }

        customerRepository.save(customer);
    }

    @Override
    public String generateCustomerCode() {
        String prefix = "KH" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyMM"));
        long count = customerRepository.count() + 1;
        return prefix + String.format("%04d", count);
    }
}

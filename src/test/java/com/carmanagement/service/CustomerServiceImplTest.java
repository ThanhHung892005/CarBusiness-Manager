package com.carmanagement.service;

import com.carmanagement.dto.request.CustomerCreateRequest;
import com.carmanagement.entity.Customer;
import com.carmanagement.enums.CustomerType;
import com.carmanagement.exception.BusinessException;
import com.carmanagement.exception.ResourceNotFoundException;
import com.carmanagement.repository.CustomerRepository;
import com.carmanagement.service.impl.CustomerServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock CustomerRepository customerRepository;

    @InjectMocks CustomerServiceImpl customerService;

    // ── findById ──────────────────────────────────────────────────────────────

    @Test
    void findById_found_returnsCustomer() {
        Customer customer = Customer.builder().id(1L).fullName("Nguyễn Văn A").build();
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        Customer result = customerService.findById(1L);

        assertThat(result.getFullName()).isEqualTo("Nguyễn Văn A");
    }

    @Test
    void findById_notFound_throwsResourceNotFoundException() {
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.findById(99L))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Customer");
    }

    // ── search ────────────────────────────────────────────────────────────────

    @Test
    void search_delegatesToRepository() {
        Page<Customer> page = new PageImpl<>(List.of());
        when(customerRepository.search(any(), any(), any())).thenReturn(page);

        customerService.search("keyword", CustomerType.VIP, Pageable.unpaged());

        verify(customerRepository).search(eq("keyword"), eq(CustomerType.VIP), any());
    }

    // ── create ────────────────────────────────────────────────────────────────

    @Test
    void create_duplicatePhone_throwsBusinessException() {
        when(customerRepository.existsByPhone("0901234567")).thenReturn(true);

        CustomerCreateRequest req = new CustomerCreateRequest();
        req.setFullName("Nguyễn Văn A");
        req.setPhone("0901234567");

        assertThatThrownBy(() -> customerService.create(req))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("Số điện thoại đã được đăng ký");
    }

    @Test
    void create_success_setsCustomerTypeNew() {
        when(customerRepository.existsByPhone(any())).thenReturn(false);
        when(customerRepository.count()).thenReturn(10L);
        Customer saved = Customer.builder()
            .id(11L).customerType(CustomerType.NEW).loyaltyPoints(0).build();
        when(customerRepository.save(any())).thenReturn(saved);

        CustomerCreateRequest req = new CustomerCreateRequest();
        req.setFullName("Trần Thị B");
        req.setPhone("0901234568");
        req.setEmail("b@example.com");

        Customer result = customerService.create(req);

        assertThat(result.getCustomerType()).isEqualTo(CustomerType.NEW);
        assertThat(result.getLoyaltyPoints()).isEqualTo(0);
        verify(customerRepository).save(argThat(c ->
            c.getCustomerType() == CustomerType.NEW && c.getLoyaltyPoints() == 0
        ));
    }

    @Test
    void create_corporate_setsCorporateFlag() {
        when(customerRepository.existsByPhone(any())).thenReturn(false);
        when(customerRepository.count()).thenReturn(5L);
        Customer saved = Customer.builder().id(6L).isCorporate(true).build();
        when(customerRepository.save(any())).thenReturn(saved);

        CustomerCreateRequest req = new CustomerCreateRequest();
        req.setFullName("Công ty ABC");
        req.setPhone("0281234567");
        req.setIsCorporate(true);
        req.setCompanyName("ABC Corp");

        customerService.create(req);

        verify(customerRepository).save(argThat(c -> Boolean.TRUE.equals(c.getIsCorporate())));
    }

    // ── update ────────────────────────────────────────────────────────────────

    @Test
    void update_notFound_throwsResourceNotFoundException() {
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.update(99L, new CustomerCreateRequest()))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void update_success_updatesFields() {
        Customer existing = Customer.builder().id(1L).fullName("Old Name").build();
        when(customerRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(customerRepository.save(any())).thenReturn(existing);

        CustomerCreateRequest req = new CustomerCreateRequest();
        req.setFullName("New Name");
        req.setPhone("0901234567");
        req.setEmail("new@example.com");
        req.setCity("Hà Nội");

        customerService.update(1L, req);

        assertThat(existing.getFullName()).isEqualTo("New Name");
        assertThat(existing.getCity()).isEqualTo("Hà Nội");
    }

    // ── addLoyaltyPoints ──────────────────────────────────────────────────────

    @Test
    void addLoyaltyPoints_below1000_keepsTypeNew() {
        Customer customer = Customer.builder()
            .id(1L).loyaltyPoints(500).customerType(CustomerType.NEW).build();
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(customerRepository.save(any())).thenReturn(customer);

        customerService.addLoyaltyPoints(1L, 300);

        assertThat(customer.getLoyaltyPoints()).isEqualTo(800);
        assertThat(customer.getCustomerType()).isEqualTo(CustomerType.NEW);
    }

    @Test
    void addLoyaltyPoints_reaches1000_upgradestoRegular() {
        Customer customer = Customer.builder()
            .id(1L).loyaltyPoints(700).customerType(CustomerType.NEW).build();
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(customerRepository.save(any())).thenReturn(customer);

        customerService.addLoyaltyPoints(1L, 400);

        assertThat(customer.getLoyaltyPoints()).isEqualTo(1100);
        assertThat(customer.getCustomerType()).isEqualTo(CustomerType.REGULAR);
    }

    @Test
    void addLoyaltyPoints_reaches5000_upgradesToVip() {
        Customer customer = Customer.builder()
            .id(1L).loyaltyPoints(4800).customerType(CustomerType.REGULAR).build();
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(customerRepository.save(any())).thenReturn(customer);

        customerService.addLoyaltyPoints(1L, 300);

        assertThat(customer.getLoyaltyPoints()).isEqualTo(5100);
        assertThat(customer.getCustomerType()).isEqualTo(CustomerType.VIP);
    }

    @Test
    void addLoyaltyPoints_exactly5000_upgradestoVip() {
        Customer customer = Customer.builder()
            .id(1L).loyaltyPoints(4000).customerType(CustomerType.REGULAR).build();
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(customerRepository.save(any())).thenReturn(customer);

        customerService.addLoyaltyPoints(1L, 1000);

        assertThat(customer.getCustomerType()).isEqualTo(CustomerType.VIP);
    }

    // ── generateCustomerCode ──────────────────────────────────────────────────

    @Test
    void generateCustomerCode_startsWithKH() {
        when(customerRepository.count()).thenReturn(5L);

        String code = customerService.generateCustomerCode();

        assertThat(code).startsWith("KH");
        assertThat(code).endsWith("0006");
    }
}

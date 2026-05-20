package com.carmanagement.integration;

import com.carmanagement.dto.request.OrderCreateRequest;
import com.carmanagement.dto.request.PaymentCreateRequest;
import com.carmanagement.entity.*;
import com.carmanagement.enums.*;
import com.carmanagement.repository.*;
import com.carmanagement.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Testcontainers(disabledWithoutDocker = true)
@ActiveProfiles("test")
@Transactional
class OrderFlowIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
        .withDatabaseName("car_management_test")
        .withUsername("testuser")
        .withPassword("testpass");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired OrderService orderService;
    @Autowired VehicleRepository vehicleRepository;
    @Autowired CustomerRepository customerRepository;
    @Autowired OrderRepository orderRepository;
    @Autowired InvoiceRepository invoiceRepository;
    @Autowired BrandRepository brandRepository;
    @Autowired CarModelRepository carModelRepository;
    @Autowired ShowroomRepository showroomRepository;

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Brand savedBrand() {
        return brandRepository.save(Brand.builder().name("TestBrand").active(true).build());
    }

    private CarModel savedModel(Brand brand) {
        return carModelRepository.save(CarModel.builder()
            .brand(brand).name("TestModel").year(2024)
            .carType(CarType.SEDAN).basePrice(BigDecimal.valueOf(1_000_000_000))
            .active(true).build());
    }

    private Vehicle savedVehicle(CarModel model) {
        return vehicleRepository.save(Vehicle.builder()
            .vin("VNTEST000000000001")
            .carModel(model).color("White")
            .importPrice(BigDecimal.valueOf(900_000_000))
            .sellingPrice(BigDecimal.valueOf(1_200_000_000))
            .status(VehicleStatus.AVAILABLE).build());
    }

    private Customer savedCustomer() {
        return customerRepository.save(Customer.builder()
            .customerCode("KHTEST001")
            .fullName("Khách Hàng Test")
            .phone("0909123456")
            .customerType(CustomerType.NEW)
            .loyaltyPoints(0).build());
    }

    // ── Tests ─────────────────────────────────────────────────────────────────

    @Test
    void createOrder_success_vehicleBecomesReserved() {
        Brand brand = savedBrand();
        CarModel model = savedModel(brand);
        Vehicle vehicle = savedVehicle(model);
        Customer customer = savedCustomer();

        OrderCreateRequest req = new OrderCreateRequest();
        req.setVehicleId(vehicle.getId());
        req.setCustomerId(customer.getId());

        Order order = orderService.create(req);

        assertThat(order.getId()).isNotNull();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.DRAFT);
        assertThat(order.getOrderCode()).startsWith("DH");

        Vehicle updated = vehicleRepository.findById(vehicle.getId()).orElseThrow();
        assertThat(updated.getStatus()).isEqualTo(VehicleStatus.RESERVED);
    }

    @Test
    void createOrder_thenDeliver_createsInvoice() {
        Brand brand = savedBrand();
        CarModel model = savedModel(brand);
        Vehicle vehicle = savedVehicle(model);
        Customer customer = savedCustomer();

        OrderCreateRequest req = new OrderCreateRequest();
        req.setVehicleId(vehicle.getId());
        req.setCustomerId(customer.getId());

        Order order = orderService.create(req);
        Order delivered = orderService.updateStatus(order.getId(), OrderStatus.DELIVERED);

        assertThat(delivered.getStatus()).isEqualTo(OrderStatus.DELIVERED);
        assertThat(delivered.getDeliveryDate()).isNotNull();

        boolean invoiceExists = invoiceRepository.findByOrderId(order.getId()).isPresent();
        assertThat(invoiceExists).isTrue();

        Vehicle sold = vehicleRepository.findById(vehicle.getId()).orElseThrow();
        assertThat(sold.getStatus()).isEqualTo(VehicleStatus.SOLD);
    }

    @Test
    void fullFlow_createOrderDeliverPay_completesOrder() {
        Brand brand = savedBrand();
        CarModel model = savedModel(brand);
        Vehicle vehicle = savedVehicle(model);
        Customer customer = savedCustomer();

        // 1. Create order
        OrderCreateRequest orderReq = new OrderCreateRequest();
        orderReq.setVehicleId(vehicle.getId());
        orderReq.setCustomerId(customer.getId());
        Order order = orderService.create(orderReq);

        // 2. Deliver → invoice created
        orderService.updateStatus(order.getId(), OrderStatus.DELIVERED);
        Invoice invoice = invoiceRepository.findByOrderId(order.getId()).orElseThrow();
        assertThat(invoice.getStatus()).isEqualTo(InvoiceStatus.UNPAID);

        // 3. Full payment → order COMPLETED
        PaymentCreateRequest payReq = new PaymentCreateRequest();
        payReq.setInvoiceId(invoice.getId());
        payReq.setAmount(order.getTotalAmount());
        payReq.setPaymentMethod(PaymentMethod.BANK_TRANSFER);
        orderService.addPayment(payReq);

        Invoice paid = invoiceRepository.findById(invoice.getId()).orElseThrow();
        assertThat(paid.getStatus()).isEqualTo(InvoiceStatus.PAID);

        Order completed = orderRepository.findById(order.getId()).orElseThrow();
        assertThat(completed.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @Test
    void cancelOrder_releasesVehicle() {
        Brand brand = savedBrand();
        CarModel model = savedModel(brand);
        Vehicle vehicle = savedVehicle(model);
        Customer customer = savedCustomer();

        OrderCreateRequest req = new OrderCreateRequest();
        req.setVehicleId(vehicle.getId());
        req.setCustomerId(customer.getId());

        Order order = orderService.create(req);
        orderService.updateStatus(order.getId(), OrderStatus.CANCELLED);

        Vehicle released = vehicleRepository.findById(vehicle.getId()).orElseThrow();
        assertThat(released.getStatus()).isEqualTo(VehicleStatus.AVAILABLE);
    }

    @Test
    void partialPayment_marksInvoicePartial() {
        Brand brand = savedBrand();
        CarModel model = savedModel(brand);
        Vehicle vehicle = savedVehicle(model);
        Customer customer = savedCustomer();

        OrderCreateRequest orderReq = new OrderCreateRequest();
        orderReq.setVehicleId(vehicle.getId());
        orderReq.setCustomerId(customer.getId());
        Order order = orderService.create(orderReq);
        orderService.updateStatus(order.getId(), OrderStatus.DELIVERED);

        Invoice invoice = invoiceRepository.findByOrderId(order.getId()).orElseThrow();
        BigDecimal half = order.getTotalAmount().divide(BigDecimal.valueOf(2));

        PaymentCreateRequest payReq = new PaymentCreateRequest();
        payReq.setInvoiceId(invoice.getId());
        payReq.setAmount(half);
        payReq.setPaymentMethod(PaymentMethod.CASH);
        orderService.addPayment(payReq);

        Invoice partial = invoiceRepository.findById(invoice.getId()).orElseThrow();
        assertThat(partial.getStatus()).isEqualTo(InvoiceStatus.PARTIAL);
        assertThat(partial.getPaidAmount()).isEqualByComparingTo(half);
    }
}

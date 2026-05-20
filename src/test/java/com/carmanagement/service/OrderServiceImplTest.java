package com.carmanagement.service;

import com.carmanagement.dto.request.OrderCreateRequest;
import com.carmanagement.dto.request.PaymentCreateRequest;
import com.carmanagement.entity.*;
import com.carmanagement.enums.*;
import com.carmanagement.exception.BusinessException;
import com.carmanagement.exception.ResourceNotFoundException;
import com.carmanagement.repository.*;
import com.carmanagement.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock OrderRepository orderRepository;
    @Mock CustomerRepository customerRepository;
    @Mock EmployeeRepository employeeRepository;
    @Mock ShowroomRepository showroomRepository;
    @Mock VehicleRepository vehicleRepository;
    @Mock InvoiceRepository invoiceRepository;
    @Mock PaymentRepository paymentRepository;
    @Mock EmailService emailService;

    @InjectMocks OrderServiceImpl orderService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(orderService, "loyaltyPointsPerMillion", 100);
    }

    // ── search ──────────────────────────────────────────────────────────────

    @Test
    void search_withNullDates_usesSentinelRange() {
        Page<Order> page = new PageImpl<>(List.of());
        when(orderRepository.search(any(), any(), any(), any(), any())).thenReturn(page);

        orderService.search(null, null, null, null, Pageable.unpaged());

        verify(orderRepository).search(
            isNull(),
            isNull(),
            eq(LocalDateTime.of(2000, 1, 1, 0, 0)),
            eq(LocalDateTime.of(2099, 12, 31, 23, 59)),
            any()
        );
    }

    @Test
    void search_withDates_usesProvidedDates() {
        LocalDateTime from = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime to   = LocalDateTime.of(2024, 12, 31, 23, 59);
        when(orderRepository.search(any(), any(), eq(from), eq(to), any()))
            .thenReturn(new PageImpl<>(List.of()));

        orderService.search("keyword", OrderStatus.CONFIRMED, from, to, Pageable.unpaged());

        verify(orderRepository).search(eq("keyword"), eq(OrderStatus.CONFIRMED), eq(from), eq(to), any());
    }

    // ── findById ─────────────────────────────────────────────────────────────

    @Test
    void findById_found_returnsOrder() {
        Order order = Order.builder().id(1L).orderCode("DH001").build();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        Order result = orderService.findById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getOrderCode()).isEqualTo("DH001");
    }

    @Test
    void findById_notFound_throwsResourceNotFoundException() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.findById(99L))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("99");
    }

    // ── create ───────────────────────────────────────────────────────────────

    @Test
    void create_vehicleNotFound_throwsResourceNotFoundException() {
        when(vehicleRepository.findById(1L)).thenReturn(Optional.empty());

        OrderCreateRequest req = new OrderCreateRequest();
        req.setVehicleId(1L);
        req.setCustomerId(1L);

        assertThatThrownBy(() -> orderService.create(req))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Vehicle");
    }

    @Test
    void create_vehicleSold_throwsBusinessException() {
        Vehicle vehicle = Vehicle.builder().id(1L).status(VehicleStatus.SOLD).build();
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));

        OrderCreateRequest req = new OrderCreateRequest();
        req.setVehicleId(1L);
        req.setCustomerId(1L);

        assertThatThrownBy(() -> orderService.create(req))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("không còn sẵn sàng");
    }

    @Test
    void create_vehicleReserved_throwsBusinessException() {
        Vehicle vehicle = Vehicle.builder().id(1L).status(VehicleStatus.RESERVED).build();
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));

        OrderCreateRequest req = new OrderCreateRequest();
        req.setVehicleId(1L);
        req.setCustomerId(1L);

        assertThatThrownBy(() -> orderService.create(req))
            .isInstanceOf(BusinessException.class);
    }

    @Test
    void create_customerNotFound_throwsResourceNotFoundException() {
        Vehicle vehicle = Vehicle.builder().id(1L).status(VehicleStatus.AVAILABLE)
            .sellingPrice(BigDecimal.valueOf(500_000_000)).build();
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));
        when(customerRepository.findById(2L)).thenReturn(Optional.empty());

        OrderCreateRequest req = new OrderCreateRequest();
        req.setVehicleId(1L);
        req.setCustomerId(2L);

        assertThatThrownBy(() -> orderService.create(req))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Customer");
    }

    @Test
    void create_success_setsVehicleReservedAndSendsEmail() {
        Vehicle vehicle = Vehicle.builder()
            .id(1L).status(VehicleStatus.AVAILABLE)
            .sellingPrice(BigDecimal.valueOf(500_000_000)).build();
        Customer customer = Customer.builder().id(2L).build();
        Order savedOrder = Order.builder()
            .id(10L).orderCode("DH20240101001").customer(customer).build();

        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));
        when(customerRepository.findById(2L)).thenReturn(Optional.of(customer));
        when(orderRepository.count()).thenReturn(0L);
        when(orderRepository.save(any())).thenReturn(savedOrder);
        doNothing().when(emailService).sendOrderConfirmation(any());

        OrderCreateRequest req = new OrderCreateRequest();
        req.setVehicleId(1L);
        req.setCustomerId(2L);

        Order result = orderService.create(req);

        assertThat(result.getId()).isEqualTo(10L);
        assertThat(vehicle.getStatus()).isEqualTo(VehicleStatus.RESERVED);
        verify(vehicleRepository).save(vehicle);
        verify(orderRepository).save(any(Order.class));
        verify(emailService).sendOrderConfirmation(savedOrder);
    }

    @Test
    void create_withDiscount_calculatesCorrectTotal() {
        BigDecimal price = BigDecimal.valueOf(1_000_000_000);
        BigDecimal discount = BigDecimal.valueOf(50_000_000);

        Vehicle vehicle = Vehicle.builder()
            .id(1L).status(VehicleStatus.AVAILABLE).sellingPrice(price).build();
        Customer customer = Customer.builder().id(2L).build();
        Order savedOrder = Order.builder().id(5L).build();

        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));
        when(customerRepository.findById(2L)).thenReturn(Optional.of(customer));
        when(orderRepository.count()).thenReturn(0L);
        when(orderRepository.save(any())).thenReturn(savedOrder);
        doNothing().when(emailService).sendOrderConfirmation(any());

        OrderCreateRequest req = new OrderCreateRequest();
        req.setVehicleId(1L);
        req.setCustomerId(2L);
        req.setDiscountAmount(discount);

        orderService.create(req);

        verify(orderRepository).save(argThat(order ->
            order.getTotalAmount().compareTo(price.subtract(discount)) == 0
        ));
    }

    // ── updateStatus ──────────────────────────────────────────────────────────

    @Test
    void updateStatus_delivered_createsInvoiceAndMarksSold() {
        Vehicle vehicle = Vehicle.builder().id(1L).status(VehicleStatus.RESERVED).build();
        OrderItem item = OrderItem.builder().vehicle(vehicle).build();
        Order order = Order.builder()
            .id(1L).orderCode("DH20240101001")
            .status(OrderStatus.CONFIRMED)
            .totalAmount(BigDecimal.valueOf(500_000_000))
            .orderItems(new ArrayList<>(List.of(item)))
            .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any())).thenReturn(order);

        orderService.updateStatus(1L, OrderStatus.DELIVERED);

        assertThat(vehicle.getStatus()).isEqualTo(VehicleStatus.SOLD);
        assertThat(order.getDeliveryDate()).isNotNull();
        verify(invoiceRepository).save(argThat(inv ->
            inv.getInvoiceCode() != null && inv.getStatus() == InvoiceStatus.UNPAID
        ));
    }

    @Test
    void updateStatus_delivered_noDoubleInvoice_whenInvoiceExists() {
        Invoice existing = Invoice.builder().id(99L).build();
        Order order = Order.builder()
            .id(1L).orderCode("DH001")
            .status(OrderStatus.CONFIRMED)
            .totalAmount(BigDecimal.valueOf(500_000_000))
            .invoice(existing)
            .orderItems(new ArrayList<>())
            .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any())).thenReturn(order);

        orderService.updateStatus(1L, OrderStatus.DELIVERED);

        verify(invoiceRepository, never()).save(any());
    }

    @Test
    void updateStatus_cancelled_releasesReservedVehicles() {
        Vehicle v1 = Vehicle.builder().id(1L).status(VehicleStatus.RESERVED).build();
        Vehicle v2 = Vehicle.builder().id(2L).status(VehicleStatus.RESERVED).build();
        Order order = Order.builder()
            .id(1L).status(OrderStatus.CONFIRMED)
            .orderItems(new ArrayList<>(List.of(
                OrderItem.builder().vehicle(v1).build(),
                OrderItem.builder().vehicle(v2).build()
            ))).build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any())).thenReturn(order);

        orderService.updateStatus(1L, OrderStatus.CANCELLED);

        assertThat(v1.getStatus()).isEqualTo(VehicleStatus.AVAILABLE);
        assertThat(v2.getStatus()).isEqualTo(VehicleStatus.AVAILABLE);
        verify(vehicleRepository, times(2)).save(any());
    }

    @Test
    void updateStatus_cancelled_doesNotReleaseAlreadySoldVehicle() {
        Vehicle vehicle = Vehicle.builder().id(1L).status(VehicleStatus.SOLD).build();
        Order order = Order.builder()
            .id(1L).status(OrderStatus.DELIVERED)
            .orderItems(new ArrayList<>(List.of(OrderItem.builder().vehicle(vehicle).build())))
            .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any())).thenReturn(order);

        orderService.updateStatus(1L, OrderStatus.CANCELLED);

        assertThat(vehicle.getStatus()).isEqualTo(VehicleStatus.SOLD);
        verify(vehicleRepository, never()).save(any());
    }

    // ── addPayment ────────────────────────────────────────────────────────────

    @Test
    void addPayment_invoiceNotFound_throwsResourceNotFoundException() {
        when(invoiceRepository.findById(99L)).thenReturn(Optional.empty());

        PaymentCreateRequest req = new PaymentCreateRequest();
        req.setInvoiceId(99L);
        req.setAmount(BigDecimal.valueOf(100));

        assertThatThrownBy(() -> orderService.addPayment(req))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void addPayment_alreadyPaid_throwsBusinessException() {
        Invoice invoice = Invoice.builder()
            .id(1L).status(InvoiceStatus.PAID)
            .totalAmount(BigDecimal.valueOf(100)).paidAmount(BigDecimal.valueOf(100)).build();
        when(invoiceRepository.findById(1L)).thenReturn(Optional.of(invoice));

        PaymentCreateRequest req = new PaymentCreateRequest();
        req.setInvoiceId(1L);
        req.setAmount(BigDecimal.valueOf(50));

        assertThatThrownBy(() -> orderService.addPayment(req))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("đã được thanh toán đầy đủ");
    }

    @Test
    void addPayment_amountExceedsRemaining_throwsBusinessException() {
        Invoice invoice = Invoice.builder()
            .id(1L).status(InvoiceStatus.UNPAID)
            .totalAmount(BigDecimal.valueOf(1_000_000))
            .paidAmount(BigDecimal.valueOf(800_000)).build();
        when(invoiceRepository.findById(1L)).thenReturn(Optional.of(invoice));

        PaymentCreateRequest req = new PaymentCreateRequest();
        req.setInvoiceId(1L);
        req.setAmount(BigDecimal.valueOf(300_000)); // exceeds remaining 200k

        assertThatThrownBy(() -> orderService.addPayment(req))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("vượt quá");
    }

    @Test
    void addPayment_fullPayment_marksInvoicePaidCompletesOrderAddsLoyaltyPoints() {
        Customer customer = Customer.builder().id(1L).loyaltyPoints(0).build();
        Order order = Order.builder()
            .id(1L).status(OrderStatus.DELIVERED)
            .totalAmount(BigDecimal.valueOf(2_000_000_000L))
            .customer(customer).build();
        Invoice invoice = Invoice.builder()
            .id(1L).status(InvoiceStatus.UNPAID).order(order)
            .totalAmount(BigDecimal.valueOf(2_000_000_000L))
            .paidAmount(BigDecimal.ZERO).build();

        when(invoiceRepository.findById(1L)).thenReturn(Optional.of(invoice));
        when(invoiceRepository.save(any())).thenReturn(invoice);

        PaymentCreateRequest req = new PaymentCreateRequest();
        req.setInvoiceId(1L);
        req.setAmount(BigDecimal.valueOf(2_000_000_000L));
        req.setPaymentMethod(PaymentMethod.CASH);

        orderService.addPayment(req);

        assertThat(invoice.getStatus()).isEqualTo(InvoiceStatus.PAID);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        assertThat(customer.getLoyaltyPoints()).isEqualTo(200_000); // 2000M/1M * 100
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    void addPayment_partialPayment_marksInvoicePartial() {
        Order order = Order.builder().id(1L).build();
        Invoice invoice = Invoice.builder()
            .id(1L).status(InvoiceStatus.UNPAID).order(order)
            .totalAmount(BigDecimal.valueOf(1_000_000))
            .paidAmount(BigDecimal.ZERO).build();

        when(invoiceRepository.findById(1L)).thenReturn(Optional.of(invoice));
        when(invoiceRepository.save(any())).thenReturn(invoice);

        PaymentCreateRequest req = new PaymentCreateRequest();
        req.setInvoiceId(1L);
        req.setAmount(BigDecimal.valueOf(500_000));
        req.setPaymentMethod(PaymentMethod.BANK_TRANSFER);

        orderService.addPayment(req);

        assertThat(invoice.getStatus()).isEqualTo(InvoiceStatus.PARTIAL);
        assertThat(invoice.getPaidAmount()).isEqualByComparingTo(BigDecimal.valueOf(500_000));
    }

    // ── generateOrderCode ─────────────────────────────────────────────────────

    @Test
    void generateOrderCode_startsWithDH() {
        when(orderRepository.count()).thenReturn(5L);

        String code = orderService.generateOrderCode();

        assertThat(code).startsWith("DH");
        assertThat(code).endsWith("0006");
    }
}

package com.carmanagement.service;

import com.carmanagement.entity.*;
import com.carmanagement.enums.*;
import com.carmanagement.exception.ResourceNotFoundException;
import com.carmanagement.repository.InvoiceRepository;
import com.carmanagement.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PdfExportServiceTest {

    @Mock OrderRepository orderRepository;
    @Mock InvoiceRepository invoiceRepository;

    @InjectMocks PdfExportService pdfExportService;

    private Order buildOrderWithDetails() {
        Brand brand = Brand.builder().id(1L).name("Toyota").build();
        CarModel model = CarModel.builder()
            .id(1L).name("Camry").year(2024).carType(CarType.SEDAN).brand(brand).build();
        Vehicle vehicle = Vehicle.builder()
            .id(1L).vin("VN1234567890123456")
            .carModel(model).color("White")
            .sellingPrice(BigDecimal.valueOf(1_200_000_000)).build();
        Customer customer = Customer.builder()
            .id(1L).customerCode("KH2024001")
            .fullName("Nguyễn Văn A").phone("0901234567")
            .email("a@example.com").address("123 Lý Thường Kiệt").build();
        OrderItem item = OrderItem.builder()
            .vehicle(vehicle)
            .unitPrice(BigDecimal.valueOf(1_200_000_000))
            .discount(BigDecimal.ZERO)
            .lineTotal(BigDecimal.valueOf(1_200_000_000)).build();
        return Order.builder()
            .id(1L).orderCode("DH20240101001")
            .customer(customer)
            .status(OrderStatus.DELIVERED)
            .subtotal(BigDecimal.valueOf(1_200_000_000))
            .discountAmount(BigDecimal.ZERO)
            .totalAmount(BigDecimal.valueOf(1_200_000_000))
            .orderDate(LocalDateTime.of(2024, 1, 1, 10, 0))
            .orderItems(new ArrayList<>(List.of(item))).build();
    }

    // ── exportInvoice ─────────────────────────────────────────────────────────

    @Test
    void exportInvoice_orderNotFound_throwsResourceNotFoundException() {
        when(orderRepository.findWithDetailsById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pdfExportService.exportInvoice(99L))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void exportInvoice_noInvoice_returnsValidPdf() {
        Order order = buildOrderWithDetails();
        when(orderRepository.findWithDetailsById(1L)).thenReturn(Optional.of(order));
        when(invoiceRepository.findByOrderId(1L)).thenReturn(Optional.empty());

        byte[] pdf = pdfExportService.exportInvoice(1L);

        assertThat(pdf).isNotNull().isNotEmpty();
        // PDF files start with %PDF
        assertThat(new String(pdf, 0, 4)).isEqualTo("%PDF");
    }

    @Test
    void exportInvoice_withInvoiceAndPayments_returnsValidPdf() {
        Order order = buildOrderWithDetails();
        Payment payment = Payment.builder()
            .id(1L)
            .amount(BigDecimal.valueOf(600_000_000))
            .paymentMethod(PaymentMethod.CASH)
            .referenceNo("REF001")
            .createdAt(LocalDateTime.of(2024, 1, 5, 14, 30)).build();
        Invoice invoice = Invoice.builder()
            .id(1L).invoiceCode("INV20240101001")
            .order(order)
            .totalAmount(BigDecimal.valueOf(1_200_000_000))
            .paidAmount(BigDecimal.valueOf(600_000_000))
            .status(InvoiceStatus.PARTIAL)
            .issuedDate(LocalDateTime.of(2024, 1, 1, 10, 0))
            .payments(new ArrayList<>(List.of(payment))).build();

        when(orderRepository.findWithDetailsById(1L)).thenReturn(Optional.of(order));
        when(invoiceRepository.findByOrderId(1L)).thenReturn(Optional.of(invoice));

        byte[] pdf = pdfExportService.exportInvoice(1L);

        assertThat(pdf).isNotNull().isNotEmpty();
        assertThat(new String(pdf, 0, 4)).isEqualTo("%PDF");
    }

    @Test
    void exportInvoice_withShowroom_returnsValidPdf() {
        Order order = buildOrderWithDetails();
        Showroom showroom = Showroom.builder().id(1L).name("HCM Showroom").build();
        order.setShowroom(showroom);

        when(orderRepository.findWithDetailsById(1L)).thenReturn(Optional.of(order));
        when(invoiceRepository.findByOrderId(1L)).thenReturn(Optional.empty());

        byte[] pdf = pdfExportService.exportInvoice(1L);

        assertThat(pdf).isNotNull().isNotEmpty();
    }

    @Test
    void exportInvoice_paidInvoice_returnsValidPdf() {
        Order order = buildOrderWithDetails();
        Invoice invoice = Invoice.builder()
            .id(1L).invoiceCode("INV20240101001")
            .order(order)
            .totalAmount(BigDecimal.valueOf(1_200_000_000))
            .paidAmount(BigDecimal.valueOf(1_200_000_000))
            .status(InvoiceStatus.PAID)
            .issuedDate(LocalDateTime.of(2024, 1, 1, 10, 0))
            .payments(new ArrayList<>()).build();

        when(orderRepository.findWithDetailsById(1L)).thenReturn(Optional.of(order));
        when(invoiceRepository.findByOrderId(1L)).thenReturn(Optional.of(invoice));

        byte[] pdf = pdfExportService.exportInvoice(1L);

        assertThat(pdf).isNotNull().isNotEmpty();
        assertThat(new String(pdf, 0, 4)).isEqualTo("%PDF");
    }
}

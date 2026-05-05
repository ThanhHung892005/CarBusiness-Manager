package com.carmanagement.service.impl;

import com.carmanagement.dto.request.OrderCreateRequest;
import com.carmanagement.dto.request.PaymentCreateRequest;
import com.carmanagement.entity.*;
import com.carmanagement.enums.InvoiceStatus;
import com.carmanagement.enums.OrderStatus;
import com.carmanagement.enums.VehicleStatus;
import com.carmanagement.exception.BusinessException;
import com.carmanagement.exception.ResourceNotFoundException;
import com.carmanagement.repository.*;
import com.carmanagement.service.EmailService;
import com.carmanagement.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final EmployeeRepository employeeRepository;
    private final ShowroomRepository showroomRepository;
    private final VehicleRepository vehicleRepository;
    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    private final EmailService emailService;

    @Value("${app.loyalty.points-per-million:100}")
    private int loyaltyPointsPerMillion;

    @Override
    public Page<Order> search(String keyword, OrderStatus status, LocalDateTime from, LocalDateTime to, Pageable pageable) {
        LocalDateTime effectiveFrom = from != null ? from : LocalDateTime.of(2000, 1, 1, 0, 0);
        LocalDateTime effectiveTo   = to   != null ? to   : LocalDateTime.of(2099, 12, 31, 23, 59);
        return orderRepository.search(keyword, status, effectiveFrom, effectiveTo, pageable);
    }

    @Override
    public Order findById(Long id) {
        return orderRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Order", id));
    }

    @Override
    public Order findWithDetailsById(Long id) {
        Order order = orderRepository.findWithDetailsById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Order", id));
        if (order.getInvoice() != null) {
            Hibernate.initialize(order.getInvoice());
            Hibernate.initialize(order.getInvoice().getPayments());
        }
        return order;
    }

    @Override
    @Transactional
    public Order create(OrderCreateRequest req) {
        Vehicle vehicle = vehicleRepository.findById(req.getVehicleId())
            .orElseThrow(() -> new ResourceNotFoundException("Vehicle", req.getVehicleId()));

        if (vehicle.getStatus() != VehicleStatus.AVAILABLE) {
            throw new BusinessException("Xe không còn sẵn sàng để bán (trạng thái: " + vehicle.getStatus() + ")");
        }

        Customer customer = customerRepository.findById(req.getCustomerId())
            .orElseThrow(() -> new ResourceNotFoundException("Customer", req.getCustomerId()));

        BigDecimal unitPrice = vehicle.getSellingPrice();
        BigDecimal discount = req.getDiscountAmount() != null ? req.getDiscountAmount() : BigDecimal.ZERO;
        BigDecimal lineTotal = unitPrice.subtract(discount);
        BigDecimal total = lineTotal;

        Order order = Order.builder()
            .orderCode(generateOrderCode())
            .customer(customer)
            .status(OrderStatus.DRAFT)
            .subtotal(unitPrice)
            .discountAmount(discount)
            .discountPct(req.getDiscountPct() != null ? req.getDiscountPct() : BigDecimal.ZERO)
            .totalAmount(total)
            .notes(req.getNotes())
            .build();

        if (req.getEmployeeId() != null) {
            Employee employee = employeeRepository.findById(req.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee", req.getEmployeeId()));
            order.setEmployee(employee);
            order.setCommissionAmt(total.multiply(employee.getCommissionRate()));
        }

        if (req.getShowroomId() != null) {
            order.setShowroom(showroomRepository.findById(req.getShowroomId())
                .orElseThrow(() -> new ResourceNotFoundException("Showroom", req.getShowroomId())));
        }

        OrderItem item = OrderItem.builder()
            .order(order)
            .vehicle(vehicle)
            .unitPrice(unitPrice)
            .discount(discount)
            .lineTotal(lineTotal)
            .build();

        order.getOrderItems().add(item);

        vehicle.setStatus(VehicleStatus.RESERVED);
        vehicleRepository.save(vehicle);

        Order saved = orderRepository.save(order);
        emailService.sendOrderConfirmation(saved);
        return saved;
    }

    @Override
    @Transactional
    public Order updateStatus(Long id, OrderStatus newStatus) {
        Order order = findById(id);
        order.setStatus(newStatus);

        if (newStatus == OrderStatus.DELIVERED) {
            order.setDeliveryDate(LocalDateTime.now());
            order.getOrderItems().forEach(item -> {
                Vehicle v = item.getVehicle();
                v.setStatus(VehicleStatus.SOLD);
                v.setSoldDate(LocalDate.now());
                vehicleRepository.save(v);
            });

            // Create invoice if not exists
            if (order.getInvoice() == null) {
                Invoice invoice = Invoice.builder()
                    .invoiceCode("INV" + order.getOrderCode().substring(3))
                    .order(order)
                    .totalAmount(order.getTotalAmount())
                    .paidAmount(BigDecimal.ZERO)
                    .status(InvoiceStatus.UNPAID)
                    .build();
                invoiceRepository.save(invoice);
            }
        }

        if (newStatus == OrderStatus.CANCELLED) {
            order.getOrderItems().forEach(item -> {
                Vehicle v = item.getVehicle();
                if (v.getStatus() == VehicleStatus.RESERVED) {
                    v.setStatus(VehicleStatus.AVAILABLE);
                    vehicleRepository.save(v);
                }
            });
        }

        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Invoice addPayment(PaymentCreateRequest req) {
        Invoice invoice = invoiceRepository.findById(req.getInvoiceId())
            .orElseThrow(() -> new ResourceNotFoundException("Invoice", req.getInvoiceId()));

        if (invoice.getStatus() == InvoiceStatus.PAID) {
            throw new BusinessException("Hóa đơn đã được thanh toán đầy đủ");
        }

        if (req.getAmount().compareTo(invoice.getRemaining()) > 0) {
            throw new BusinessException("Số tiền thanh toán vượt quá số tiền còn lại");
        }

        Payment payment = Payment.builder()
            .invoice(invoice)
            .amount(req.getAmount())
            .paymentMethod(req.getPaymentMethod())
            .referenceNo(req.getReferenceNo())
            .notes(req.getNotes())
            .build();

        paymentRepository.save(payment);

        invoice.setPaidAmount(invoice.getPaidAmount().add(req.getAmount()));
        if (invoice.getRemaining().compareTo(BigDecimal.ZERO) <= 0) {
            invoice.setStatus(InvoiceStatus.PAID);
            // Complete the order
            Order order = invoice.getOrder();
            order.setStatus(OrderStatus.COMPLETED);
            orderRepository.save(order);

            // Add loyalty points (100 pts per million VND)
            long millions = order.getTotalAmount().divide(BigDecimal.valueOf(1_000_000)).longValue();
            int points = (int) (millions * loyaltyPointsPerMillion);
            if (points > 0) {
                Customer customer = order.getCustomer();
                customer.setLoyaltyPoints(customer.getLoyaltyPoints() + points);
            }
        } else {
            invoice.setStatus(InvoiceStatus.PARTIAL);
        }

        return invoiceRepository.save(invoice);
    }

    @Override
    public byte[] exportInvoicePdf(Long orderId) {
        // PDF generation implemented in PdfExportService
        throw new UnsupportedOperationException("Use PdfExportService.exportInvoice()");
    }

    @Override
    public String generateOrderCode() {
        String prefix = "DH" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long count = orderRepository.count() + 1;
        return prefix + String.format("%04d", count);
    }
}

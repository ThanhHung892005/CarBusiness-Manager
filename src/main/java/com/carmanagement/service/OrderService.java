package com.carmanagement.service;

import com.carmanagement.dto.request.OrderCreateRequest;
import com.carmanagement.dto.request.PaymentCreateRequest;
import com.carmanagement.entity.Invoice;
import com.carmanagement.entity.Order;
import com.carmanagement.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface OrderService {
    Page<Order> search(String keyword, OrderStatus status, LocalDateTime from, LocalDateTime to, Pageable pageable);
    Order findById(Long id);
    Order findWithDetailsById(Long id);
    Order create(OrderCreateRequest request);
    Order updateStatus(Long id, OrderStatus newStatus);
    Invoice addPayment(PaymentCreateRequest request);
    byte[] exportInvoicePdf(Long orderId);
    String generateOrderCode();
}

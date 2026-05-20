package com.carmanagement.service;

import com.carmanagement.entity.Invoice;
import com.carmanagement.enums.InvoiceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InvoiceService {
    Page<Invoice> search(String keyword, InvoiceStatus status, Pageable pageable);
    Invoice findById(Long id);
    Invoice findWithDetailsById(Long id);
}

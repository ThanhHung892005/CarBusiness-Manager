package com.carmanagement.service.impl;

import com.carmanagement.entity.Invoice;
import com.carmanagement.enums.InvoiceStatus;
import com.carmanagement.exception.ResourceNotFoundException;
import com.carmanagement.repository.InvoiceRepository;
import com.carmanagement.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<Invoice> search(String keyword, InvoiceStatus status, Pageable pageable) {
        String kw = (keyword == null) ? "" : keyword.trim();
        return invoiceRepository.search(kw, status, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Invoice findById(Long id) {
        return invoiceRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Invoice not found: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Invoice findWithDetailsById(Long id) {
        return invoiceRepository.findWithDetailsById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Invoice not found: " + id));
    }
}

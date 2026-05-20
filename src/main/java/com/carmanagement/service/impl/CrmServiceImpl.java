package com.carmanagement.service.impl;

import com.carmanagement.dto.request.InteractionCreateRequest;
import com.carmanagement.dto.request.LeadCreateRequest;
import com.carmanagement.entity.Customer;
import com.carmanagement.entity.CustomerInteraction;
import com.carmanagement.entity.Lead;
import com.carmanagement.enums.CustomerType;
import com.carmanagement.enums.LeadSource;
import com.carmanagement.enums.LeadStatus;
import com.carmanagement.exception.BusinessException;
import com.carmanagement.exception.ResourceNotFoundException;
import com.carmanagement.repository.CustomerInteractionRepository;
import com.carmanagement.repository.CustomerRepository;
import com.carmanagement.repository.EmployeeRepository;
import com.carmanagement.repository.LeadRepository;
import com.carmanagement.service.CrmService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CrmServiceImpl implements CrmService {

    private final LeadRepository leadRepository;
    private final CustomerInteractionRepository interactionRepository;
    private final EmployeeRepository employeeRepository;
    private final CustomerRepository customerRepository;

    @Override
    public Page<Lead> searchLeads(String keyword, LeadStatus status, LeadSource source, Pageable pageable) {
        return leadRepository.search(keyword, status, source, pageable);
    }

    @Override
    public Lead findLeadById(Long id) {
        return leadRepository.findWithDetailsById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Lead", id));
    }

    @Override
    public List<CustomerInteraction> findInteractionsByLead(Long leadId) {
        return interactionRepository.findByLeadIdWithDetails(leadId);
    }

    @Override
    @Transactional
    public Lead createLead(LeadCreateRequest req) {
        Lead lead = Lead.builder()
            .fullName(req.getFullName())
            .phone(req.getPhone())
            .email(req.getEmail())
            .source(req.getSource())
            .status(req.getStatus() != null ? req.getStatus() : LeadStatus.NEW)
            .notes(req.getNotes())
            .build();
        if (req.getAssignedEmployeeId() != null) {
            employeeRepository.findById(req.getAssignedEmployeeId()).ifPresent(lead::setAssignedEmployee);
        }
        return leadRepository.save(lead);
    }

    @Override
    @Transactional
    public Lead updateLead(Long id, LeadCreateRequest req) {
        Lead lead = leadRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Lead", id));
        lead.setFullName(req.getFullName());
        lead.setPhone(req.getPhone());
        lead.setEmail(req.getEmail());
        lead.setSource(req.getSource());
        if (req.getStatus() != null) lead.setStatus(req.getStatus());
        lead.setNotes(req.getNotes());
        if (req.getAssignedEmployeeId() != null) {
            employeeRepository.findById(req.getAssignedEmployeeId()).ifPresent(lead::setAssignedEmployee);
        } else {
            lead.setAssignedEmployee(null);
        }
        return leadRepository.save(lead);
    }

    @Override
    @Transactional
    public CustomerInteraction addInteraction(Long leadId, InteractionCreateRequest req, String username) {
        Lead lead = leadRepository.findById(leadId)
            .orElseThrow(() -> new ResourceNotFoundException("Lead", leadId));
        CustomerInteraction ci = CustomerInteraction.builder()
            .lead(lead)
            .type(req.getType())
            .content(req.getContent())
            .interactionDate(req.getInteractionDate() != null ? req.getInteractionDate() : LocalDateTime.now())
            .build();
        employeeRepository.findByUserUsername(username).ifPresent(ci::setEmployee);
        return interactionRepository.save(ci);
    }

    @Override
    @Transactional
    public Lead convertToCustomer(Long leadId) {
        Lead lead = leadRepository.findById(leadId)
            .orElseThrow(() -> new ResourceNotFoundException("Lead", leadId));
        if (lead.getConvertedCustomer() != null) {
            throw new BusinessException("Lead này đã được chuyển đổi thành khách hàng");
        }
        Customer customer = customerRepository.findByPhone(lead.getPhone()).orElseGet(() -> {
            String prefix = "KH" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyMM"));
            String code = prefix + String.format("%04d", customerRepository.count() + 1);
            return customerRepository.save(Customer.builder()
                .customerCode(code)
                .fullName(lead.getFullName())
                .phone(lead.getPhone())
                .email(lead.getEmail())
                .notes("Chuyển từ lead #" + lead.getId())
                .customerType(CustomerType.NEW)
                .loyaltyPoints(0)
                .build());
        });
        lead.setStatus(LeadStatus.CLOSED_WON);
        lead.setConvertedCustomer(customer);
        return leadRepository.save(lead);
    }

    @Override
    @Transactional
    public void updateLeadStatus(Long id, LeadStatus status) {
        Lead lead = leadRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Lead", id));
        lead.setStatus(status);
        leadRepository.save(lead);
    }
}

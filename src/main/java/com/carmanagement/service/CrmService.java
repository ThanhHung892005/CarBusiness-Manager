package com.carmanagement.service;

import com.carmanagement.dto.request.InteractionCreateRequest;
import com.carmanagement.dto.request.LeadCreateRequest;
import com.carmanagement.entity.CustomerInteraction;
import com.carmanagement.entity.Lead;
import com.carmanagement.enums.LeadSource;
import com.carmanagement.enums.LeadStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CrmService {
    Page<Lead> searchLeads(String keyword, LeadStatus status, LeadSource source, Pageable pageable);
    Lead findLeadById(Long id);
    List<CustomerInteraction> findInteractionsByLead(Long leadId);
    Lead createLead(LeadCreateRequest request);
    Lead updateLead(Long id, LeadCreateRequest request);
    CustomerInteraction addInteraction(Long leadId, InteractionCreateRequest request, String username);
    Lead convertToCustomer(Long leadId);
    void updateLeadStatus(Long id, LeadStatus status);
}

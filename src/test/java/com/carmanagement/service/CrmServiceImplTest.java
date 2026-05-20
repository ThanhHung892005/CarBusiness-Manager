package com.carmanagement.service;

import com.carmanagement.dto.request.InteractionCreateRequest;
import com.carmanagement.dto.request.LeadCreateRequest;
import com.carmanagement.entity.Customer;
import com.carmanagement.entity.CustomerInteraction;
import com.carmanagement.entity.Employee;
import com.carmanagement.entity.Lead;
import com.carmanagement.entity.User;
import com.carmanagement.enums.InteractionType;
import com.carmanagement.enums.LeadSource;
import com.carmanagement.enums.LeadStatus;
import com.carmanagement.exception.BusinessException;
import com.carmanagement.exception.ResourceNotFoundException;
import com.carmanagement.repository.CustomerInteractionRepository;
import com.carmanagement.repository.CustomerRepository;
import com.carmanagement.repository.EmployeeRepository;
import com.carmanagement.repository.LeadRepository;
import com.carmanagement.service.impl.CrmServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CrmServiceImplTest {

    @Mock LeadRepository leadRepository;
    @Mock CustomerInteractionRepository interactionRepository;
    @Mock EmployeeRepository employeeRepository;
    @Mock CustomerRepository customerRepository;

    @InjectMocks CrmServiceImpl crmService;

    // ── findLeadById ───────────────────────────────────────────────────────────

    @Test
    void findLeadById_notFound_throwsResourceNotFoundException() {
        when(leadRepository.findWithDetailsById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> crmService.findLeadById(99L))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Lead");
    }

    @Test
    void findLeadById_found_returnsLead() {
        Lead lead = Lead.builder().id(1L).fullName("Nguyen Van A").build();
        when(leadRepository.findWithDetailsById(1L)).thenReturn(Optional.of(lead));

        Lead result = crmService.findLeadById(1L);

        assertThat(result.getFullName()).isEqualTo("Nguyen Van A");
    }

    // ── createLead ─────────────────────────────────────────────────────────────

    @Test
    void createLead_withoutEmployee_savesLead() {
        Lead saved = Lead.builder().id(10L).fullName("Tran B").phone("0901234567").build();
        when(leadRepository.save(any())).thenReturn(saved);

        LeadCreateRequest req = new LeadCreateRequest();
        req.setFullName("Tran B");
        req.setPhone("0901234567");
        req.setSource(LeadSource.WALK_IN);

        Lead result = crmService.createLead(req);

        assertThat(result.getId()).isEqualTo(10L);
        verify(leadRepository).save(any(Lead.class));
        verify(employeeRepository, never()).findById(any());
    }

    @Test
    void createLead_withEmployee_assignsEmployee() {
        Employee emp = Employee.builder().id(5L).build();
        Lead saved = Lead.builder().id(10L).build();
        when(employeeRepository.findById(5L)).thenReturn(Optional.of(emp));
        when(leadRepository.save(any())).thenReturn(saved);

        LeadCreateRequest req = new LeadCreateRequest();
        req.setFullName("Le C");
        req.setPhone("0911111111");
        req.setSource(LeadSource.FACEBOOK);
        req.setAssignedEmployeeId(5L);

        crmService.createLead(req);

        verify(employeeRepository).findById(5L);
        verify(leadRepository).save(argThat(lead -> lead.getAssignedEmployee() == emp));
    }

    @Test
    void createLead_statusDefaultsToNew_whenNotProvided() {
        when(leadRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        LeadCreateRequest req = new LeadCreateRequest();
        req.setFullName("Test");
        req.setPhone("0900000000");
        req.setSource(LeadSource.OTHER);
        // no status set

        Lead result = crmService.createLead(req);

        assertThat(result.getStatus()).isEqualTo(LeadStatus.NEW);
    }

    // ── updateLead ─────────────────────────────────────────────────────────────

    @Test
    void updateLead_notFound_throwsResourceNotFoundException() {
        when(leadRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> crmService.updateLead(99L, new LeadCreateRequest()))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateLead_clearsEmployee_whenAssignedIdIsNull() {
        Lead existing = Lead.builder().id(1L).fullName("Old Name").build();
        Employee emp = Employee.builder().id(5L).build();
        existing.setAssignedEmployee(emp);

        when(leadRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(leadRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        LeadCreateRequest req = new LeadCreateRequest();
        req.setFullName("New Name");
        req.setPhone("0900000000");
        req.setSource(LeadSource.REFERRAL);
        req.setAssignedEmployeeId(null);

        Lead result = crmService.updateLead(1L, req);

        assertThat(result.getAssignedEmployee()).isNull();
        assertThat(result.getFullName()).isEqualTo("New Name");
    }

    // ── addInteraction ──────────────────────────────────────────────────────────

    @Test
    void addInteraction_leadNotFound_throwsResourceNotFoundException() {
        when(leadRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> crmService.addInteraction(99L, new InteractionCreateRequest(), "user"))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void addInteraction_attributesToLoggedInEmployee() {
        Lead lead = Lead.builder().id(1L).build();
        User user = User.builder().username("sales1").build();
        Employee emp = Employee.builder().id(3L).user(user).build();

        when(leadRepository.findById(1L)).thenReturn(Optional.of(lead));
        when(employeeRepository.findByUserUsername("sales1")).thenReturn(Optional.of(emp));
        when(interactionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        InteractionCreateRequest req = new InteractionCreateRequest();
        req.setType(InteractionType.CALL);
        req.setContent("Called to schedule test drive");
        req.setInteractionDate(LocalDateTime.now());

        CustomerInteraction result = crmService.addInteraction(1L, req, "sales1");

        assertThat(result.getEmployee()).isEqualTo(emp);
        assertThat(result.getLead()).isEqualTo(lead);
    }

    @Test
    void addInteraction_usesCurrentTimeWhenDateNotProvided() {
        Lead lead = Lead.builder().id(1L).build();
        when(leadRepository.findById(1L)).thenReturn(Optional.of(lead));
        when(employeeRepository.findByUserUsername(any())).thenReturn(Optional.empty());
        when(interactionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        InteractionCreateRequest req = new InteractionCreateRequest();
        req.setType(InteractionType.NOTE);
        req.setContent("Some note");
        req.setInteractionDate(null);

        LocalDateTime before = LocalDateTime.now().minusSeconds(1);
        CustomerInteraction result = crmService.addInteraction(1L, req, "unknown");
        LocalDateTime after = LocalDateTime.now().plusSeconds(1);

        assertThat(result.getInteractionDate()).isBetween(before, after);
    }

    // ── convertToCustomer ───────────────────────────────────────────────────────

    @Test
    void convertToCustomer_leadNotFound_throwsResourceNotFoundException() {
        when(leadRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> crmService.convertToCustomer(99L))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void convertToCustomer_alreadyConverted_throwsBusinessException() {
        Customer existing = Customer.builder().id(1L).build();
        Lead lead = Lead.builder().id(1L).convertedCustomer(existing).build();
        when(leadRepository.findById(1L)).thenReturn(Optional.of(lead));

        assertThatThrownBy(() -> crmService.convertToCustomer(1L))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("đã được chuyển đổi");
    }

    @Test
    void convertToCustomer_existingCustomerByPhone_linksWithoutCreating() {
        Lead lead = Lead.builder().id(1L).fullName("Nguyen A").phone("0901234567").build();
        Customer existing = Customer.builder().id(5L).phone("0901234567").build();

        when(leadRepository.findById(1L)).thenReturn(Optional.of(lead));
        when(customerRepository.findByPhone("0901234567")).thenReturn(Optional.of(existing));
        when(leadRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Lead result = crmService.convertToCustomer(1L);

        assertThat(result.getConvertedCustomer()).isEqualTo(existing);
        assertThat(result.getStatus()).isEqualTo(LeadStatus.CLOSED_WON);
        verify(customerRepository, never()).save(any());
    }

    @Test
    void convertToCustomer_noExistingCustomer_createsNewCustomer() {
        Lead lead = Lead.builder().id(1L).fullName("New Buyer").phone("0999999999").build();
        Customer created = Customer.builder().id(10L).phone("0999999999").build();

        when(leadRepository.findById(1L)).thenReturn(Optional.of(lead));
        when(customerRepository.findByPhone("0999999999")).thenReturn(Optional.empty());
        when(customerRepository.count()).thenReturn(5L);
        when(customerRepository.save(any())).thenReturn(created);
        when(leadRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Lead result = crmService.convertToCustomer(1L);

        assertThat(result.getConvertedCustomer()).isEqualTo(created);
        verify(customerRepository).save(argThat(c -> c.getPhone().equals("0999999999")));
    }

    // ── updateLeadStatus ────────────────────────────────────────────────────────

    @Test
    void updateLeadStatus_notFound_throwsResourceNotFoundException() {
        when(leadRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> crmService.updateLeadStatus(99L, LeadStatus.CONTACTED))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateLeadStatus_updatesAndSaves() {
        Lead lead = Lead.builder().id(1L).status(LeadStatus.NEW).build();
        when(leadRepository.findById(1L)).thenReturn(Optional.of(lead));

        crmService.updateLeadStatus(1L, LeadStatus.CONTACTED);

        assertThat(lead.getStatus()).isEqualTo(LeadStatus.CONTACTED);
        verify(leadRepository).save(lead);
    }
}

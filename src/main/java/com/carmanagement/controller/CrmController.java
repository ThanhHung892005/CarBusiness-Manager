package com.carmanagement.controller;

import com.carmanagement.dto.request.InteractionCreateRequest;
import com.carmanagement.dto.request.LeadCreateRequest;
import com.carmanagement.enums.InteractionType;
import com.carmanagement.enums.LeadSource;
import com.carmanagement.enums.LeadStatus;
import com.carmanagement.repository.EmployeeRepository;
import com.carmanagement.service.CrmService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/crm")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('GIAM_DOC','NV_KINH_DOANH')")
public class CrmController {

    private final CrmService crmService;
    private final EmployeeRepository employeeRepository;

    @GetMapping({"", "/leads"})
    public String leadList(@RequestParam(defaultValue = "") String keyword,
                           @RequestParam(required = false) LeadStatus status,
                           @RequestParam(required = false) LeadSource source,
                           @RequestParam(defaultValue = "0") int page,
                           Model model) {
        var pageable = PageRequest.of(page, 15, Sort.by("createdAt").descending());
        model.addAttribute("leads", crmService.searchLeads(keyword, status, source, pageable));
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedSource", source);
        model.addAttribute("statuses", LeadStatus.values());
        model.addAttribute("sources", LeadSource.values());
        return "crm/leads/list";
    }

    @GetMapping("/leads/new")
    public String newLeadForm(Model model) {
        model.addAttribute("lead", new LeadCreateRequest());
        model.addAttribute("employees", employeeRepository.findAllActiveWithUser());
        model.addAttribute("statuses", LeadStatus.values());
        model.addAttribute("sources", LeadSource.values());
        return "crm/leads/form";
    }

    @PostMapping("/leads")
    public String createLead(@Valid @ModelAttribute("lead") LeadCreateRequest request,
                             BindingResult result,
                             Model model,
                             RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("employees", employeeRepository.findAllActiveWithUser());
            model.addAttribute("statuses", LeadStatus.values());
            model.addAttribute("sources", LeadSource.values());
            return "crm/leads/form";
        }
        var lead = crmService.createLead(request);
        ra.addFlashAttribute("success", "Thêm lead thành công: " + lead.getFullName());
        return "redirect:/crm/leads/" + lead.getId();
    }

    @GetMapping("/leads/{id}")
    public String leadDetail(@PathVariable Long id, Model model) {
        model.addAttribute("lead", crmService.findLeadById(id));
        model.addAttribute("interactions", crmService.findInteractionsByLead(id));
        model.addAttribute("interactionForm", new InteractionCreateRequest());
        model.addAttribute("interactionTypes", InteractionType.values());
        model.addAttribute("statuses", LeadStatus.values());
        return "crm/leads/detail";
    }

    @GetMapping("/leads/{id}/edit")
    public String editLeadForm(@PathVariable Long id, Model model) {
        var lead = crmService.findLeadById(id);
        var req = new LeadCreateRequest();
        req.setFullName(lead.getFullName());
        req.setPhone(lead.getPhone());
        req.setEmail(lead.getEmail());
        req.setSource(lead.getSource());
        req.setStatus(lead.getStatus());
        req.setNotes(lead.getNotes());
        if (lead.getAssignedEmployee() != null) {
            req.setAssignedEmployeeId(lead.getAssignedEmployee().getId());
        }
        model.addAttribute("lead", req);
        model.addAttribute("leadId", id);
        model.addAttribute("employees", employeeRepository.findAllActiveWithUser());
        model.addAttribute("statuses", LeadStatus.values());
        model.addAttribute("sources", LeadSource.values());
        return "crm/leads/form";
    }

    @PostMapping("/leads/{id}")
    public String updateLead(@PathVariable Long id,
                             @Valid @ModelAttribute("lead") LeadCreateRequest request,
                             BindingResult result,
                             Model model,
                             RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("leadId", id);
            model.addAttribute("employees", employeeRepository.findAllActiveWithUser());
            model.addAttribute("statuses", LeadStatus.values());
            model.addAttribute("sources", LeadSource.values());
            return "crm/leads/form";
        }
        crmService.updateLead(id, request);
        ra.addFlashAttribute("success", "Cập nhật lead thành công");
        return "redirect:/crm/leads/" + id;
    }

    @PostMapping("/leads/{id}/interactions")
    public String addInteraction(@PathVariable Long id,
                                 @Valid @ModelAttribute("interactionForm") InteractionCreateRequest request,
                                 BindingResult result,
                                 Authentication authentication,
                                 RedirectAttributes ra) {
        if (result.hasErrors()) {
            ra.addFlashAttribute("error", "Vui lòng điền đủ thông tin tương tác");
            return "redirect:/crm/leads/" + id;
        }
        crmService.addInteraction(id, request, authentication.getName());
        ra.addFlashAttribute("success", "Ghi nhận tương tác thành công");
        return "redirect:/crm/leads/" + id;
    }

    @PostMapping("/leads/{id}/convert")
    public String convertToCustomer(@PathVariable Long id, RedirectAttributes ra) {
        try {
            var lead = crmService.convertToCustomer(id);
            ra.addFlashAttribute("success",
                "Chuyển đổi thành công! Khách hàng: " + lead.getConvertedCustomer().getCustomerCode());
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/crm/leads/" + id;
    }

    @PostMapping("/leads/{id}/status")
    public String updateStatus(@PathVariable Long id,
                               @RequestParam LeadStatus status,
                               RedirectAttributes ra) {
        crmService.updateLeadStatus(id, status);
        ra.addFlashAttribute("success", "Cập nhật trạng thái: " + status.getDisplayName());
        return "redirect:/crm/leads/" + id;
    }
}

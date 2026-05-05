package com.carmanagement.controller;

import com.carmanagement.dto.request.CustomerCreateRequest;
import com.carmanagement.enums.CustomerType;
import com.carmanagement.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/sales/customers")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','MANAGER','SALES')")
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    public String list(@RequestParam(defaultValue = "") String keyword,
                       @RequestParam(required = false) CustomerType type,
                       @RequestParam(defaultValue = "0") int page,
                       Model model) {
        var pageable = PageRequest.of(page, 15, Sort.by("createdAt").descending());
        model.addAttribute("customers", customerService.search(keyword, type, pageable));
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedType", type);
        model.addAttribute("types", CustomerType.values());
        return "sales/customers/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("customer", customerService.findById(id));
        return "sales/customers/detail";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("customer", new CustomerCreateRequest());
        return "sales/customers/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("customer") CustomerCreateRequest request,
                         BindingResult result,
                         RedirectAttributes ra) {
        if (result.hasErrors()) return "sales/customers/form";
        var customer = customerService.create(request);
        ra.addFlashAttribute("success", "Thêm khách hàng thành công: " + customer.getCustomerCode());
        return "redirect:/sales/customers/" + customer.getId();
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        var customer = customerService.findById(id);
        var request = new CustomerCreateRequest();
        request.setFullName(customer.getFullName());
        request.setPhone(customer.getPhone());
        request.setEmail(customer.getEmail());
        request.setAddress(customer.getAddress());
        request.setCity(customer.getCity());
        request.setIdNumber(customer.getIdNumber());
        request.setNotes(customer.getNotes());
        model.addAttribute("customer", request);
        model.addAttribute("customerId", id);
        return "sales/customers/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("customer") CustomerCreateRequest request,
                         BindingResult result,
                         RedirectAttributes ra) {
        if (result.hasErrors()) return "sales/customers/form";
        customerService.update(id, request);
        ra.addFlashAttribute("success", "Cập nhật khách hàng thành công");
        return "redirect:/sales/customers/" + id;
    }
}

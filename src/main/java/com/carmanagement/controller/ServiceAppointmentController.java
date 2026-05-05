package com.carmanagement.controller;

import com.carmanagement.dto.request.AppointmentCreateRequest;
import com.carmanagement.dto.request.VehicleSearchRequest;
import com.carmanagement.entity.ServiceAppointment;
import com.carmanagement.enums.AppointmentStatus;
import com.carmanagement.service.CustomerService;
import com.carmanagement.service.EmployeeService;
import com.carmanagement.service.ServiceAppointmentService;
import com.carmanagement.service.ShowroomService;
import com.carmanagement.service.VehicleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/sales/appointments")
@PreAuthorize("hasAnyRole('ADMIN','MANAGER','SALES')")
@RequiredArgsConstructor
public class ServiceAppointmentController {

    private final ServiceAppointmentService appointmentService;
    private final CustomerService customerService;
    private final VehicleService vehicleService;
    private final EmployeeService employeeService;
    private final ShowroomService showroomService;

    @GetMapping
    public String list(@RequestParam(required = false) String keyword,
                       @RequestParam(required = false) AppointmentStatus status,
                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
                       @RequestParam(defaultValue = "0") int page,
                       Model model) {
        var pageable = PageRequest.of(page, 15, Sort.by("appointmentDate").descending());
        model.addAttribute("appointments", appointmentService.search(keyword, status, fromDate, toDate, pageable));
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("fromDate", fromDate);
        model.addAttribute("toDate", toDate);
        model.addAttribute("statuses", AppointmentStatus.values());
        return "sales/appointments/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("appointmentRequest", new AppointmentCreateRequest());
        populateFormModel(model);
        return "sales/appointments/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("appointmentRequest") AppointmentCreateRequest request,
                         BindingResult result,
                         Model model,
                         RedirectAttributes flash) {
        if (result.hasErrors()) {
            populateFormModel(model);
            return "sales/appointments/form";
        }
        try {
            ServiceAppointment saved = appointmentService.create(request);
            flash.addFlashAttribute("success", "Đặt lịch hẹn thành công: " + saved.getAppointmentCode());
            return "redirect:/sales/appointments";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            populateFormModel(model);
            return "sales/appointments/form";
        }
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("appointment", appointmentService.findWithDetailsById(id));
        model.addAttribute("statuses", AppointmentStatus.values());
        return "sales/appointments/detail";
    }

    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id,
                               @RequestParam AppointmentStatus status,
                               RedirectAttributes flash) {
        appointmentService.updateStatus(id, status);
        flash.addFlashAttribute("success", "Cập nhật trạng thái thành công");
        return "redirect:/sales/appointments/" + id;
    }

    private void populateFormModel(Model model) {
        VehicleSearchRequest vehicleReq = new VehicleSearchRequest();
        vehicleReq.setSize(500);
        model.addAttribute("vehicles", vehicleService.search(vehicleReq).getContent());
        model.addAttribute("customers",
            customerService.search("", null, PageRequest.of(0, 500, Sort.by("fullName"))).getContent());
        model.addAttribute("employees",
            employeeService.search("", null, null, PageRequest.of(0, 200, Sort.by("id"))).getContent());
        model.addAttribute("showrooms", showroomService.findAllActive());
        model.addAttribute("serviceTypes", com.carmanagement.enums.ServiceType.values());
    }
}

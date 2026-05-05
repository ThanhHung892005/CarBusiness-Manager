package com.carmanagement.controller;

import com.carmanagement.dto.request.OrderCreateRequest;
import com.carmanagement.dto.request.PaymentCreateRequest;
import com.carmanagement.dto.request.VehicleSearchRequest;
import com.carmanagement.enums.OrderStatus;
import com.carmanagement.enums.VehicleStatus;
import com.carmanagement.service.CustomerService;
import com.carmanagement.service.EmployeeService;
import com.carmanagement.service.OrderService;
import com.carmanagement.service.PdfExportService;
import com.carmanagement.service.ShowroomService;
import com.carmanagement.service.VehicleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/sales/orders")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','MANAGER','SALES')")
public class OrderController {

    private final OrderService orderService;
    private final CustomerService customerService;
    private final VehicleService vehicleService;
    private final EmployeeService employeeService;
    private final ShowroomService showroomService;
    private final PdfExportService pdfExportService;

    @GetMapping
    public String list(@RequestParam(defaultValue = "") String keyword,
                       @RequestParam(required = false) OrderStatus status,
                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
                       @RequestParam(defaultValue = "0") int page,
                       Model model) {
        var pageable = PageRequest.of(page, 15, Sort.by("orderDate").descending());
        var from = fromDate != null ? fromDate.atStartOfDay() : null;
        var to   = toDate   != null ? toDate.atTime(23, 59, 59) : null;
        model.addAttribute("orders", orderService.search(keyword, status, from, to, pageable));
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("fromDate", fromDate);
        model.addAttribute("toDate", toDate);
        model.addAttribute("statuses", OrderStatus.values());
        return "sales/orders/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("order", orderService.findWithDetailsById(id));
        model.addAttribute("statuses", OrderStatus.values());
        model.addAttribute("paymentRequest", new PaymentCreateRequest());
        return "sales/orders/detail";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("order", new OrderCreateRequest());
        populateFormModel(model);
        return "sales/orders/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("order") OrderCreateRequest request,
                         BindingResult result,
                         RedirectAttributes ra,
                         Model model) {
        if (result.hasErrors()) {
            populateFormModel(model);
            return "sales/orders/form";
        }
        var order = orderService.create(request);
        ra.addFlashAttribute("success", "Tạo đơn hàng thành công: " + order.getOrderCode());
        return "redirect:/sales/orders/" + order.getId();
    }

    private void populateFormModel(Model model) {
        var availableReq = new VehicleSearchRequest();
        availableReq.setStatus(VehicleStatus.AVAILABLE);
        availableReq.setSize(500);
        model.addAttribute("availableVehicles", vehicleService.search(availableReq).getContent());
        model.addAttribute("customers", customerService.search("", null, PageRequest.of(0, 500, Sort.by("fullName"))).getContent());
        model.addAttribute("employees", employeeService.search("", null, null, PageRequest.of(0, 200, Sort.by("id"))).getContent());
        model.addAttribute("showrooms", showroomService.findAllActive());
    }

    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id,
                               @RequestParam OrderStatus status,
                               RedirectAttributes ra) {
        orderService.updateStatus(id, status);
        ra.addFlashAttribute("success", "Cập nhật trạng thái đơn hàng thành công");
        return "redirect:/sales/orders/" + id;
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable Long id) {
        byte[] pdf = pdfExportService.exportInvoice(id);
        String filename = "invoice-order-" + id + ".pdf";
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdf);
    }

    @PostMapping("/payment")
    public String addPayment(@Valid @ModelAttribute PaymentCreateRequest request,
                             BindingResult result,
                             RedirectAttributes ra) {
        if (result.hasErrors()) {
            ra.addFlashAttribute("error", "Dữ liệu thanh toán không hợp lệ");
            return "redirect:/sales/orders";
        }
        var invoice = orderService.addPayment(request);
        ra.addFlashAttribute("success", "Ghi nhận thanh toán thành công");
        return "redirect:/sales/orders/" + invoice.getOrder().getId();
    }
}

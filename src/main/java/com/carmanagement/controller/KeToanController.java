package com.carmanagement.controller;

import com.carmanagement.dto.request.PaymentCreateRequest;
import com.carmanagement.enums.InvoiceStatus;
import com.carmanagement.service.InvoiceService;
import com.carmanagement.service.OrderService;
import com.carmanagement.service.PdfExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/ke-toan")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('GIAM_DOC','KE_TOAN')")
public class KeToanController {

    private final InvoiceService invoiceService;
    private final OrderService orderService;
    private final PdfExportService pdfExportService;

    @GetMapping("/invoices")
    public String list(@RequestParam(defaultValue = "") String keyword,
                       @RequestParam(required = false) InvoiceStatus status,
                       @RequestParam(defaultValue = "0") int page,
                       Model model) {
        var invoices = invoiceService.search(
            keyword, status,
            PageRequest.of(page, 15, Sort.by(Sort.Direction.DESC, "issuedDate"))
        );
        model.addAttribute("invoices", invoices);
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("statuses", InvoiceStatus.values());
        return "ke-toan/invoices/list";
    }

    @GetMapping("/invoices/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("invoice", invoiceService.findWithDetailsById(id));
        return "ke-toan/invoices/detail";
    }

    @PostMapping("/invoices/{id}/payment")
    public String addPayment(@PathVariable Long id,
                             @ModelAttribute PaymentCreateRequest request,
                             RedirectAttributes ra) {
        request.setInvoiceId(id);
        orderService.addPayment(request);
        ra.addFlashAttribute("success", "Ghi nhận thanh toán thành công");
        return "redirect:/ke-toan/invoices/" + id;
    }

    @GetMapping("/invoices/{id}/pdf")
    public ResponseEntity<byte[]> exportPdf(@PathVariable Long id) throws Exception {
        var invoice = invoiceService.findById(id);
        byte[] pdf = pdfExportService.exportInvoice(invoice.getOrder().getId());
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"hoa-don-" + invoice.getInvoiceCode() + ".pdf\"")
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdf);
    }
}

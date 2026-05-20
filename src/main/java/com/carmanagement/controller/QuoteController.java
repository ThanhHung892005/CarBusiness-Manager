package com.carmanagement.controller;

import com.carmanagement.dto.request.QuoteRequest;
import com.carmanagement.enums.VehicleStatus;
import com.carmanagement.repository.VehicleRepository;
import com.carmanagement.service.PdfExportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;

@Controller
@RequestMapping("/sales/quotes")
@PreAuthorize("hasAnyRole('GIAM_DOC','NV_KINH_DOANH')")
@RequiredArgsConstructor
public class QuoteController {

    private final VehicleRepository vehicleRepository;
    private final PdfExportService pdfExportService;

    @GetMapping
    public String form(Model model) {
        model.addAttribute("quoteRequest", new QuoteRequest());
        model.addAttribute("availableVehicles",
            vehicleRepository.findAvailableByStatus(VehicleStatus.AVAILABLE));
        return "sales/quotes/form";
    }

    @PostMapping
    public ResponseEntity<byte[]> generate(@Valid @ModelAttribute QuoteRequest quoteRequest,
                                           BindingResult bindingResult,
                                           Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("availableVehicles",
                vehicleRepository.findAvailableByStatus(VehicleStatus.AVAILABLE));
            return ResponseEntity.badRequest().build();
        }

        byte[] pdf = pdfExportService.exportQuotePdf(quoteRequest);
        String filename = "bao-gia-" + LocalDate.now() + ".pdf";
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdf);
    }
}

package com.carmanagement.controller;

import com.carmanagement.service.ExcelExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
@RequestMapping("/manager/reports")
@PreAuthorize("hasAnyRole('GIAM_DOC','KE_TOAN')")
@RequiredArgsConstructor
public class ReportController {

    private final ExcelExportService excelExportService;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("currentYear", LocalDate.now().getYear());
        return "manager/reports";
    }

    @GetMapping("/inventory.xlsx")
    public ResponseEntity<byte[]> downloadInventory() {
        byte[] data = excelExportService.exportVehicleInventory();
        String filename = "vehicle-inventory-" + LocalDate.now() + ".xlsx";
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
            .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
            .body(data);
    }

    @GetMapping("/revenue.xlsx")
    public ResponseEntity<byte[]> downloadRevenue(@RequestParam(defaultValue = "0") int year) {
        int reportYear = year == 0 ? LocalDate.now().getYear() : year;
        byte[] data = excelExportService.exportMonthlyRevenue(reportYear);
        String filename = "revenue-" + reportYear + ".xlsx";
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
            .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
            .body(data);
    }
}

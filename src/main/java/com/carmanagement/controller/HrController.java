package com.carmanagement.controller;

import com.carmanagement.dto.request.AttendanceCreateRequest;
import com.carmanagement.dto.request.CalculatePayrollRequest;
import com.carmanagement.dto.request.PayrollUpdateRequest;
import com.carmanagement.entity.Payroll;
import com.carmanagement.enums.AttendanceStatus;
import com.carmanagement.enums.PayrollStatus;
import java.math.BigDecimal;
import java.math.RoundingMode;
import com.carmanagement.repository.EmployeeRepository;
import com.carmanagement.service.HrService;
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

import java.time.LocalDate;
import java.time.YearMonth;

@Controller
@RequestMapping("/admin/hr")
@RequiredArgsConstructor
@PreAuthorize("hasRole('GIAM_DOC')")
public class HrController {

    private final HrService hrService;
    private final EmployeeRepository employeeRepository;

    @GetMapping({"", "/"})
    public String index() {
        return "redirect:/admin/hr/attendance";
    }

    // ─── Attendance ──────────────────────────────────────────────────────────

    @GetMapping("/attendance")
    public String attendanceList(@RequestParam(required = false) Long employeeId,
                                 @RequestParam(required = false) Integer month,
                                 @RequestParam(required = false) Integer year,
                                 @RequestParam(defaultValue = "0") int page,
                                 Model model) {
        int y = year != null ? year : LocalDate.now().getYear();
        int m = month != null ? month : LocalDate.now().getMonthValue();
        LocalDate start = LocalDate.of(y, m, 1);
        LocalDate end = YearMonth.of(y, m).atEndOfMonth();

        var pageable = PageRequest.of(page, 20, Sort.by("date").descending());
        model.addAttribute("attendances", hrService.searchAttendance(employeeId, start, end, pageable));
        model.addAttribute("employees", employeeRepository.findAllActiveWithUser());
        model.addAttribute("selectedEmployeeId", employeeId);
        model.addAttribute("selectedMonth", m);
        model.addAttribute("selectedYear", y);
        model.addAttribute("months", new int[]{1,2,3,4,5,6,7,8,9,10,11,12});
        return "admin/hr/attendance/list";
    }

    @GetMapping("/attendance/new")
    public String newAttendanceForm(Model model) {
        var req = new AttendanceCreateRequest();
        req.setDate(LocalDate.now());
        req.setStatus(AttendanceStatus.PRESENT);
        model.addAttribute("attendance", req);
        model.addAttribute("employees", employeeRepository.findAllActiveWithUser());
        model.addAttribute("statuses", AttendanceStatus.values());
        return "admin/hr/attendance/form";
    }

    @PostMapping("/attendance")
    public String createAttendance(@Valid @ModelAttribute("attendance") AttendanceCreateRequest request,
                                   BindingResult result,
                                   Model model,
                                   RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("employees", employeeRepository.findAllActiveWithUser());
            model.addAttribute("statuses", AttendanceStatus.values());
            return "admin/hr/attendance/form";
        }
        try {
            hrService.logAttendance(request);
            ra.addFlashAttribute("success", "Ghi nhận chấm công thành công");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/hr/attendance";
    }

    @GetMapping("/attendance/{id}/edit")
    public String editAttendanceForm(@PathVariable Long id, Model model) {
        var att = hrService.findAttendanceById(id);
        var req = new AttendanceCreateRequest();
        req.setEmployeeId(att.getEmployee().getId());
        req.setDate(att.getDate());
        req.setCheckIn(att.getCheckIn());
        req.setCheckOut(att.getCheckOut());
        req.setStatus(att.getStatus());
        req.setNotes(att.getNotes());
        model.addAttribute("attendance", req);
        model.addAttribute("attendanceId", id);
        model.addAttribute("employeeName", att.getEmployee().getUser().getFullName());
        model.addAttribute("statuses", AttendanceStatus.values());
        return "admin/hr/attendance/form";
    }

    @PostMapping("/attendance/{id}")
    public String updateAttendance(@PathVariable Long id,
                                   @Valid @ModelAttribute("attendance") AttendanceCreateRequest request,
                                   BindingResult result,
                                   Model model,
                                   RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("statuses", AttendanceStatus.values());
            return "admin/hr/attendance/form";
        }
        hrService.updateAttendance(id, request);
        ra.addFlashAttribute("success", "Cập nhật chấm công thành công");
        return "redirect:/admin/hr/attendance";
    }

    // ─── Payroll ─────────────────────────────────────────────────────────────

    @GetMapping("/payroll")
    public String payrollList(@RequestParam(required = false) Integer month,
                              @RequestParam(required = false) Integer year,
                              @RequestParam(required = false) PayrollStatus status,
                              @RequestParam(defaultValue = "0") int page,
                              Model model) {
        var pageable = PageRequest.of(page, 15, Sort.by("year").descending().and(Sort.by("month").descending()));
        model.addAttribute("payrolls", hrService.searchPayroll(month, year, status, pageable));
        model.addAttribute("selectedMonth", month);
        model.addAttribute("selectedYear", year);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("statuses", PayrollStatus.values());
        model.addAttribute("months", new int[]{1,2,3,4,5,6,7,8,9,10,11,12});
        model.addAttribute("currentYear", LocalDate.now().getYear());
        return "admin/hr/payroll/list";
    }

    @GetMapping("/payroll/calculate")
    public String calculateForm(Model model) {
        var req = new CalculatePayrollRequest();
        req.setMonth(LocalDate.now().getMonthValue());
        req.setYear(LocalDate.now().getYear());
        model.addAttribute("calcRequest", req);
        model.addAttribute("employees", employeeRepository.findAllActiveWithUser());
        model.addAttribute("months", new int[]{1,2,3,4,5,6,7,8,9,10,11,12});
        return "admin/hr/payroll/calculate";
    }

    @PostMapping("/payroll/calculate")
    public String doCalculate(@Valid @ModelAttribute("calcRequest") CalculatePayrollRequest request,
                              BindingResult result,
                              Model model,
                              RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("employees", employeeRepository.findAllActiveWithUser());
            model.addAttribute("months", new int[]{1,2,3,4,5,6,7,8,9,10,11,12});
            return "admin/hr/payroll/calculate";
        }
        try {
            var payroll = hrService.calculatePayroll(request);
            ra.addFlashAttribute("success", "Tính lương thành công cho tháng " + request.getMonth() + "/" + request.getYear());
            return "redirect:/admin/hr/payroll/" + payroll.getId();
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/hr/payroll/calculate";
        }
    }

    @GetMapping("/payroll/{id}")
    public String payrollDetail(@PathVariable Long id, Model model) {
        Payroll payroll = hrService.findPayrollById(id);
        model.addAttribute("payroll", payroll);
        model.addAttribute("updateForm", new PayrollUpdateRequest());
        BigDecimal baseSalaryEarned = payroll.getWorkDays() > 0
            ? payroll.getBaseSalary()
                .multiply(payroll.getEffectiveDays())
                .divide(BigDecimal.valueOf(payroll.getWorkDays()), 0, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;
        model.addAttribute("baseSalaryEarned", baseSalaryEarned);
        return "admin/hr/payroll/detail";
    }

    @PostMapping("/payroll/{id}")
    public String updatePayroll(@PathVariable Long id,
                                @ModelAttribute("updateForm") PayrollUpdateRequest request,
                                RedirectAttributes ra) {
        try {
            hrService.updatePayroll(id, request);
            ra.addFlashAttribute("success", "Cập nhật bảng lương thành công");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/hr/payroll/" + id;
    }

    @PostMapping("/payroll/{id}/approve")
    public String approvePayroll(@PathVariable Long id, RedirectAttributes ra) {
        try {
            hrService.approvePayroll(id);
            ra.addFlashAttribute("success", "Đã duyệt bảng lương");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/hr/payroll/" + id;
    }

    @PostMapping("/payroll/{id}/paid")
    public String markPaid(@PathVariable Long id, RedirectAttributes ra) {
        try {
            hrService.markPaid(id);
            ra.addFlashAttribute("success", "Đã đánh dấu thanh toán");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/hr/payroll/" + id;
    }
}

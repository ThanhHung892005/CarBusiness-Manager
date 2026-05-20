package com.carmanagement.controller;

import com.carmanagement.dto.response.EmployeeKpiDto;
import com.carmanagement.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/admin/kpi")
@PreAuthorize("hasRole('GIAM_DOC')")
@RequiredArgsConstructor
public class KpiController {

    private final OrderRepository orderRepository;

    @GetMapping
    public String index(@RequestParam(required = false) Integer month,
                        @RequestParam(required = false) Integer year,
                        Model model) {
        int selectedYear  = year  != null ? year  : LocalDate.now().getYear();
        int selectedMonth = month != null ? month : 0;

        List<EmployeeKpiDto> kpiList = orderRepository
                .findEmployeeKpi(selectedMonth, selectedYear)
                .stream()
                .map(EmployeeKpiDto::fromRow)
                .toList();

        model.addAttribute("kpiList", kpiList);
        model.addAttribute("selectedMonth", selectedMonth);
        model.addAttribute("selectedYear", selectedYear);
        model.addAttribute("currentYear", LocalDate.now().getYear());
        model.addAttribute("months", List.of(1,2,3,4,5,6,7,8,9,10,11,12));
        return "admin/kpi/index";
    }
}

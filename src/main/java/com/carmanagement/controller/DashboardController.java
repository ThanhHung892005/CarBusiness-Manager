package com.carmanagement.controller;

import com.carmanagement.dto.response.DashboardStatsResponse;
import com.carmanagement.enums.VehicleStatus;
import com.carmanagement.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final VehicleRepository vehicleRepository;
    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final EmployeeRepository employeeRepository;
    private final ShowroomRepository showroomRepository;

    @GetMapping("/admin/dashboard")
    @PreAuthorize("hasRole('GIAM_DOC')")
    public String adminDashboard(Model model) {
        model.addAttribute("stats", buildStats());
        model.addAttribute("recentOrders", orderRepository.findRecentOrders(PageRequest.of(0, 10)));
        return "admin/dashboard";
    }

    @GetMapping("/sales/dashboard")
    @PreAuthorize("hasAnyRole('GIAM_DOC','NV_KINH_DOANH')")
    public String salesDashboard(Model model) {
        model.addAttribute("stats", buildStats());
        model.addAttribute("recentOrders", orderRepository.findRecentOrders(PageRequest.of(0, 5)));
        return "sales/dashboard";
    }

    @GetMapping("/ke-toan/dashboard")
    @PreAuthorize("hasAnyRole('GIAM_DOC','KE_TOAN')")
    public String keToanDashboard(Model model) {
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime now = LocalDateTime.now();
        model.addAttribute("revenueThisMonth", orderRepository.sumRevenueByDateRange(startOfMonth, now));
        model.addAttribute("ordersThisMonth", orderRepository.countByOrderDateBetween(startOfMonth, now));
        return "ke-toan/dashboard";
    }

    @GetMapping("/inventory/dashboard")
    @PreAuthorize("hasAnyRole('GIAM_DOC','THU_KHO')")
    public String inventoryDashboard(Model model) {
        model.addAttribute("totalVehicles", vehicleRepository.count());
        model.addAttribute("available", vehicleRepository.countByStatus(com.carmanagement.enums.VehicleStatus.AVAILABLE));
        model.addAttribute("reserved", vehicleRepository.countByStatus(com.carmanagement.enums.VehicleStatus.RESERVED));
        model.addAttribute("maintenance", vehicleRepository.countByStatus(com.carmanagement.enums.VehicleStatus.MAINTENANCE));
        return "inventory/dashboard";
    }

    @GetMapping("/api/stats/revenue-monthly")
    @ResponseBody
    @PreAuthorize("hasAnyRole('GIAM_DOC','KE_TOAN')")
    public Map<String, Object> revenueMonthly(@RequestParam(defaultValue = "0") int year) {
        int reportYear = year == 0 ? LocalDate.now().getYear() : year;
        List<Object[]> rows = orderRepository.findMonthlyRevenue(reportYear);

        String[] monthLabels = {"T1","T2","T3","T4","T5","T6","T7","T8","T9","T10","T11","T12"};
        BigDecimal[] revenues = new BigDecimal[12];
        for (int i = 0; i < 12; i++) revenues[i] = BigDecimal.ZERO;
        for (Object[] r : rows) {
            int month = ((Number) r[0]).intValue();
            revenues[month - 1] = new BigDecimal(r[1].toString());
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("labels", monthLabels);
        result.put("data", revenues);
        result.put("year", reportYear);
        return result;
    }

    @GetMapping("/api/stats/vehicle-status")
    @ResponseBody
    @PreAuthorize("hasAnyRole('GIAM_DOC','KE_TOAN')")
    public Map<String, Object> vehicleStatus() {
        long available  = vehicleRepository.countByStatus(VehicleStatus.AVAILABLE);
        long reserved   = vehicleRepository.countByStatus(VehicleStatus.RESERVED);
        long sold       = vehicleRepository.countByStatus(VehicleStatus.SOLD);
        long maintenance= vehicleRepository.countByStatus(VehicleStatus.MAINTENANCE);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("labels", new String[]{"Còn hàng", "Đã đặt", "Đã bán", "Bảo dưỡng"});
        result.put("data", new long[]{available, reserved, sold, maintenance});
        result.put("colors", new String[]{"#28a745","#ffc107","#6c757d","#17a2b8"});
        return result;
    }

    private DashboardStatsResponse buildStats() {
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime now = LocalDateTime.now();

        return DashboardStatsResponse.builder()
            .totalVehicles(vehicleRepository.count())
            .availableVehicles(vehicleRepository.countByStatus(VehicleStatus.AVAILABLE))
            .totalCustomers(customerRepository.count())
            .ordersThisMonth(orderRepository.countByOrderDateBetween(startOfMonth, now))
            .revenueThisMonth(orderRepository.sumRevenueByDateRange(startOfMonth, now))
            .totalEmployees(employeeRepository.count())
            .totalShowrooms(showroomRepository.count())
            .build();
    }
}

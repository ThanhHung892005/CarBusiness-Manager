package com.carmanagement.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class DashboardStatsResponse {
    private long totalVehicles;
    private long availableVehicles;
    private long totalCustomers;
    private long ordersThisMonth;
    private BigDecimal revenueThisMonth;
    private long pendingAppointments;
    private long totalEmployees;
    private long totalShowrooms;
}

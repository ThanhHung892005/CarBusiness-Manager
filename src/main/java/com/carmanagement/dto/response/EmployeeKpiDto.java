package com.carmanagement.dto.response;

import java.math.BigDecimal;

public record EmployeeKpiDto(
        Long employeeId,
        String employeeCode,
        String fullName,
        long orderCount,
        BigDecimal totalRevenue,
        BigDecimal commission,
        long vehiclesSold
) {
    public static EmployeeKpiDto fromRow(Object[] row) {
        return new EmployeeKpiDto(
                ((Number) row[0]).longValue(),
                (String) row[1],
                (String) row[2],
                ((Number) row[3]).longValue(),
                new BigDecimal(row[4].toString()),
                new BigDecimal(row[5].toString()),
                ((Number) row[6]).longValue()
        );
    }
}

package com.carmanagement.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PayrollUpdateRequest {
    private BigDecimal commission;
    private BigDecimal bonuses;
    private BigDecimal deductions;
    private String notes;
}

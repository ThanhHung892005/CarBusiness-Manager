package com.carmanagement.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CalculatePayrollRequest {

    @NotNull
    private Long employeeId;

    @NotNull
    @Min(1) @Max(12)
    private Integer month;

    @NotNull
    @Min(2020)
    private Integer year;
}

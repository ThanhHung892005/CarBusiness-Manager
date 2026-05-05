package com.carmanagement.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderCreateRequest {

    @NotNull
    private Long customerId;

    private Long employeeId;

    private Long showroomId;

    @NotNull
    private Long vehicleId;

    @PositiveOrZero
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @PositiveOrZero
    private BigDecimal discountPct = BigDecimal.ZERO;

    private String notes;
}

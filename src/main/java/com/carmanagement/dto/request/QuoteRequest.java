package com.carmanagement.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class QuoteRequest {

    @NotBlank
    private String customerName;

    @NotBlank
    private String customerPhone;

    private String customerEmail;

    @NotNull
    private Long vehicleId;

    private BigDecimal discountAmount;

    private BigDecimal discountPct;

    private String notes;

    private Integer validDays = 7;
}

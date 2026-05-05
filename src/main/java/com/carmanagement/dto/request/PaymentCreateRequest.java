package com.carmanagement.dto.request;

import com.carmanagement.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentCreateRequest {

    @NotNull
    private Long invoiceId;

    @NotNull
    @Positive
    private BigDecimal amount;

    @NotNull
    private PaymentMethod paymentMethod;

    private String referenceNo;
    private String notes;
}

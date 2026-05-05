package com.carmanagement.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class VehicleCreateRequest {

    @NotBlank
    @Size(min = 17, max = 17, message = "VIN phải đúng 17 ký tự")
    private String vin;

    @NotNull
    private Long carModelId;

    private Long showroomId;

    @NotBlank
    private String color;

    private String colorCode;

    @NotNull
    @Positive
    private BigDecimal importPrice;

    @NotNull
    @Positive
    private BigDecimal sellingPrice;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate importDate;

    private String notes;
}

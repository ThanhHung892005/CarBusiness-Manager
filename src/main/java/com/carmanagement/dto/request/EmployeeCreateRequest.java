package com.carmanagement.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class EmployeeCreateRequest {

    @NotBlank
    private String employeeCode;

    @NotNull
    private Long userId;

    @NotBlank
    private String position;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate hireDate;

    @NotNull
    private BigDecimal salary;

    private BigDecimal commissionRate = new BigDecimal("0.02");

    private Long showroomId;

    private Long departmentId;
}

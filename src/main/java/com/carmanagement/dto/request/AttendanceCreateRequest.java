package com.carmanagement.dto.request;

import com.carmanagement.enums.AttendanceStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class AttendanceCreateRequest {

    @NotNull
    private Long employeeId;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime checkIn;

    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime checkOut;

    @NotNull
    private AttendanceStatus status;

    private String notes;
}

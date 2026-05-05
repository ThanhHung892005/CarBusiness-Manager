package com.carmanagement.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public class AppointmentCreateRequest {

    @NotNull
    private Long customerId;

    @NotNull
    private Long vehicleId;

    private Long employeeId;

    private Long showroomId;

    @NotNull
    @Future
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime appointmentDate;

    @NotNull
    private String serviceType;

    private String description;

    private String notes;
}

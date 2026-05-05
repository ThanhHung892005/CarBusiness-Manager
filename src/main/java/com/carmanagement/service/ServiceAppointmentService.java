package com.carmanagement.service;

import com.carmanagement.dto.request.AppointmentCreateRequest;
import com.carmanagement.entity.ServiceAppointment;
import com.carmanagement.enums.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface ServiceAppointmentService {
    Page<ServiceAppointment> search(String keyword, AppointmentStatus status, LocalDate from, LocalDate to, Pageable pageable);
    ServiceAppointment findById(Long id);
    ServiceAppointment findWithDetailsById(Long id);
    ServiceAppointment create(AppointmentCreateRequest request);
    ServiceAppointment updateStatus(Long id, AppointmentStatus status);
}

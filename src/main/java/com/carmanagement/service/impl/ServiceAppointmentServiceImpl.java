package com.carmanagement.service.impl;

import com.carmanagement.dto.request.AppointmentCreateRequest;
import com.carmanagement.entity.Customer;
import com.carmanagement.entity.Employee;
import com.carmanagement.entity.ServiceAppointment;
import com.carmanagement.entity.Showroom;
import com.carmanagement.entity.Vehicle;
import com.carmanagement.enums.AppointmentStatus;
import com.carmanagement.enums.ServiceType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import com.carmanagement.exception.ResourceNotFoundException;
import com.carmanagement.repository.CustomerRepository;
import com.carmanagement.repository.EmployeeRepository;
import com.carmanagement.repository.ServiceAppointmentRepository;
import com.carmanagement.repository.ShowroomRepository;
import com.carmanagement.repository.VehicleRepository;
import com.carmanagement.service.EmailService;
import com.carmanagement.service.ServiceAppointmentService;
import com.carmanagement.util.CodeGeneratorUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ServiceAppointmentServiceImpl implements ServiceAppointmentService {

    private final ServiceAppointmentRepository appointmentRepository;
    private final CustomerRepository customerRepository;
    private final VehicleRepository vehicleRepository;
    private final EmployeeRepository employeeRepository;
    private final ShowroomRepository showroomRepository;
    private final EmailService emailService;

    @Override
    public Page<ServiceAppointment> search(String keyword, AppointmentStatus status, LocalDate from, LocalDate to, Pageable pageable) {
        LocalDateTime dtFrom = from != null ? from.atStartOfDay() : LocalDateTime.of(2000, 1, 1, 0, 0);
        LocalDateTime dtTo   = to   != null ? to.atTime(23, 59, 59) : LocalDateTime.of(2099, 12, 31, 23, 59);
        return appointmentRepository.search(keyword, status, dtFrom, dtTo, pageable);
    }

    @Override
    public ServiceAppointment findById(Long id) {
        return appointmentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("ServiceAppointment", id));
    }

    @Override
    public ServiceAppointment findWithDetailsById(Long id) {
        return appointmentRepository.findWithDetailsById(id)
            .orElseThrow(() -> new ResourceNotFoundException("ServiceAppointment", id));
    }

    @Override
    @Transactional
    public ServiceAppointment create(AppointmentCreateRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
            .orElseThrow(() -> new ResourceNotFoundException("Customer", request.getCustomerId()));
        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
            .orElseThrow(() -> new ResourceNotFoundException("Vehicle", request.getVehicleId()));

        long count = appointmentRepository.count();
        String code = CodeGeneratorUtil.generateCode("SA", count + 1);

        ServiceAppointment appointment = ServiceAppointment.builder()
            .appointmentCode(code)
            .customer(customer)
            .vehicle(vehicle)
            .appointmentDate(request.getAppointmentDate())
            .serviceType(ServiceType.valueOf(request.getServiceType()))
            .description(request.getDescription())
            .notes(request.getNotes())
            .status(AppointmentStatus.SCHEDULED)
            .build();

        if (request.getEmployeeId() != null) {
            Employee emp = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee", request.getEmployeeId()));
            appointment.setEmployee(emp);
        }
        if (request.getShowroomId() != null) {
            Showroom sr = showroomRepository.findById(request.getShowroomId())
                .orElseThrow(() -> new ResourceNotFoundException("Showroom", request.getShowroomId()));
            appointment.setShowroom(sr);
        }

        ServiceAppointment saved = appointmentRepository.save(appointment);
        emailService.sendAppointmentReminder(saved);
        return saved;
    }

    @Override
    @Transactional
    public ServiceAppointment updateStatus(Long id, AppointmentStatus status) {
        ServiceAppointment appointment = findById(id);
        appointment.setStatus(status);
        return appointmentRepository.save(appointment);
    }
}

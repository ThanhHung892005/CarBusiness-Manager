package com.carmanagement.service;

import com.carmanagement.dto.request.AttendanceCreateRequest;
import com.carmanagement.dto.request.CalculatePayrollRequest;
import com.carmanagement.dto.request.PayrollUpdateRequest;
import com.carmanagement.entity.Attendance;
import com.carmanagement.entity.Payroll;
import com.carmanagement.enums.PayrollStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface HrService {
    Page<Attendance> searchAttendance(Long employeeId, LocalDate startDate, LocalDate endDate, Pageable pageable);
    Attendance findAttendanceById(Long id);
    Attendance logAttendance(AttendanceCreateRequest request);
    Attendance updateAttendance(Long id, AttendanceCreateRequest request);

    Page<Payroll> searchPayroll(Integer month, Integer year, PayrollStatus status, Pageable pageable);
    Payroll findPayrollById(Long id);
    Payroll calculatePayroll(CalculatePayrollRequest request);
    Payroll updatePayroll(Long id, PayrollUpdateRequest request);
    Payroll approvePayroll(Long id);
    Payroll markPaid(Long id);
}

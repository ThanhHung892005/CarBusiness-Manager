package com.carmanagement.service.impl;

import com.carmanagement.dto.request.AttendanceCreateRequest;
import com.carmanagement.dto.request.CalculatePayrollRequest;
import com.carmanagement.dto.request.PayrollUpdateRequest;
import com.carmanagement.entity.Attendance;
import com.carmanagement.entity.Payroll;
import com.carmanagement.enums.AttendanceStatus;
import com.carmanagement.enums.PayrollStatus;
import com.carmanagement.exception.BusinessException;
import com.carmanagement.exception.ResourceNotFoundException;
import com.carmanagement.repository.AttendanceRepository;
import com.carmanagement.repository.EmployeeRepository;
import com.carmanagement.repository.OrderRepository;
import com.carmanagement.repository.PayrollRepository;
import com.carmanagement.service.HrService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HrServiceImpl implements HrService {

    private final AttendanceRepository attendanceRepository;
    private final PayrollRepository payrollRepository;
    private final EmployeeRepository employeeRepository;
    private final OrderRepository orderRepository;

    @Override
    public Page<Attendance> searchAttendance(Long employeeId, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return attendanceRepository.search(employeeId, startDate, endDate, pageable);
    }

    @Override
    public Attendance findAttendanceById(Long id) {
        return attendanceRepository.findWithDetailsById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Attendance", id));
    }

    @Override
    @Transactional
    public Attendance logAttendance(AttendanceCreateRequest req) {
        if (attendanceRepository.existsByEmployeeIdAndDate(req.getEmployeeId(), req.getDate())) {
            throw new BusinessException("Đã có bản ghi chấm công cho nhân viên này ngày " + req.getDate());
        }
        var emp = employeeRepository.findById(req.getEmployeeId())
            .orElseThrow(() -> new ResourceNotFoundException("Employee", req.getEmployeeId()));
        Attendance att = Attendance.builder()
            .employee(emp)
            .date(req.getDate())
            .checkIn(req.getCheckIn())
            .checkOut(req.getCheckOut())
            .status(req.getStatus())
            .notes(req.getNotes())
            .build();
        return attendanceRepository.save(att);
    }

    @Override
    @Transactional
    public Attendance updateAttendance(Long id, AttendanceCreateRequest req) {
        Attendance att = attendanceRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Attendance", id));
        att.setCheckIn(req.getCheckIn());
        att.setCheckOut(req.getCheckOut());
        att.setStatus(req.getStatus());
        att.setNotes(req.getNotes());
        return attendanceRepository.save(att);
    }

    @Override
    public Page<Payroll> searchPayroll(Integer month, Integer year, PayrollStatus status, Pageable pageable) {
        return payrollRepository.search(month, year, status, pageable);
    }

    @Override
    public Payroll findPayrollById(Long id) {
        return payrollRepository.findWithDetailsById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Payroll", id));
    }

    @Override
    @Transactional
    public Payroll calculatePayroll(CalculatePayrollRequest req) {
        int month = req.getMonth();
        int year = req.getYear();
        Long employeeId = req.getEmployeeId();

        Optional<Payroll> existing = payrollRepository.findByEmployeeIdAndMonthAndYear(employeeId, month, year);
        if (existing.isPresent() && existing.get().getStatus() == PayrollStatus.PAID) {
            throw new BusinessException("Bảng lương tháng " + month + "/" + year + " đã thanh toán, không thể tính lại");
        }

        var emp = employeeRepository.findWithDetailsById(employeeId)
            .orElseThrow(() -> new ResourceNotFoundException("Employee", employeeId));

        int workDays = countWorkDays(year, month);

        YearMonth ym = YearMonth.of(year, month);
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();
        List<Attendance> records = attendanceRepository.findByEmployeeAndDateRange(employeeId, start, end);

        double effectiveDouble = 0;
        for (Attendance a : records) {
            if (a.getStatus() == AttendanceStatus.PRESENT || a.getStatus() == AttendanceStatus.LATE) {
                effectiveDouble += 1.0;
            } else if (a.getStatus() == AttendanceStatus.HALF_DAY) {
                effectiveDouble += 0.5;
            }
        }
        BigDecimal effectiveDays = BigDecimal.valueOf(effectiveDouble);

        BigDecimal baseSalary = emp.getSalary();
        BigDecimal baseSalaryEarned = workDays > 0
            ? baseSalary.multiply(effectiveDays)
                .divide(BigDecimal.valueOf(workDays), 0, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;

        BigDecimal commission = orderRepository.sumCommissionByEmployeeAndMonth(employeeId, month, year);
        if (commission == null) commission = BigDecimal.ZERO;

        BigDecimal bonuses = existing.map(Payroll::getBonuses).orElse(BigDecimal.ZERO);
        BigDecimal deductions = existing.map(Payroll::getDeductions).orElse(BigDecimal.ZERO);
        BigDecimal netSalary = baseSalaryEarned.add(commission).add(bonuses).subtract(deductions);

        Payroll payroll = existing.orElseGet(Payroll::new);
        payroll.setEmployee(emp);
        payroll.setMonth(month);
        payroll.setYear(year);
        payroll.setBaseSalary(baseSalary);
        payroll.setWorkDays(workDays);
        payroll.setEffectiveDays(effectiveDays);
        payroll.setCommission(commission);
        payroll.setBonuses(bonuses);
        payroll.setDeductions(deductions);
        payroll.setNetSalary(netSalary);
        if (payroll.getStatus() == null) payroll.setStatus(PayrollStatus.DRAFT);
        return payrollRepository.save(payroll);
    }

    @Override
    @Transactional
    public Payroll updatePayroll(Long id, PayrollUpdateRequest req) {
        Payroll p = payrollRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Payroll", id));
        if (p.getStatus() == PayrollStatus.PAID) {
            throw new BusinessException("Bảng lương đã thanh toán, không thể chỉnh sửa");
        }
        BigDecimal commission = req.getCommission() != null ? req.getCommission() : BigDecimal.ZERO;
        BigDecimal bonuses = req.getBonuses() != null ? req.getBonuses() : BigDecimal.ZERO;
        BigDecimal deductions = req.getDeductions() != null ? req.getDeductions() : BigDecimal.ZERO;
        p.setCommission(commission);
        p.setBonuses(bonuses);
        p.setDeductions(deductions);
        p.setNotes(req.getNotes());

        BigDecimal baseSalaryEarned = p.getWorkDays() > 0
            ? p.getBaseSalary().multiply(p.getEffectiveDays())
                .divide(BigDecimal.valueOf(p.getWorkDays()), 0, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;
        p.setNetSalary(baseSalaryEarned.add(commission).add(bonuses).subtract(deductions));
        return payrollRepository.save(p);
    }

    @Override
    @Transactional
    public Payroll approvePayroll(Long id) {
        Payroll p = payrollRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Payroll", id));
        if (p.getStatus() == PayrollStatus.PAID) {
            throw new BusinessException("Bảng lương đã thanh toán");
        }
        p.setStatus(PayrollStatus.APPROVED);
        return payrollRepository.save(p);
    }

    @Override
    @Transactional
    public Payroll markPaid(Long id) {
        Payroll p = payrollRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Payroll", id));
        p.setStatus(PayrollStatus.PAID);
        return payrollRepository.save(p);
    }

    private int countWorkDays(int year, int month) {
        YearMonth ym = YearMonth.of(year, month);
        int count = 0;
        for (int day = 1; day <= ym.lengthOfMonth(); day++) {
            DayOfWeek dow = ym.atDay(day).getDayOfWeek();
            if (dow != DayOfWeek.SATURDAY && dow != DayOfWeek.SUNDAY) count++;
        }
        return count;
    }
}

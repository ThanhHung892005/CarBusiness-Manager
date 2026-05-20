package com.carmanagement.service;

import com.carmanagement.dto.request.AttendanceCreateRequest;
import com.carmanagement.dto.request.CalculatePayrollRequest;
import com.carmanagement.dto.request.PayrollUpdateRequest;
import com.carmanagement.entity.Attendance;
import com.carmanagement.entity.Employee;
import com.carmanagement.entity.Payroll;
import com.carmanagement.enums.AttendanceStatus;
import com.carmanagement.enums.PayrollStatus;
import com.carmanagement.exception.BusinessException;
import com.carmanagement.exception.ResourceNotFoundException;
import com.carmanagement.repository.AttendanceRepository;
import com.carmanagement.repository.EmployeeRepository;
import com.carmanagement.repository.OrderRepository;
import com.carmanagement.repository.PayrollRepository;
import com.carmanagement.service.impl.HrServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HrServiceImplTest {

    @Mock AttendanceRepository attendanceRepository;
    @Mock PayrollRepository payrollRepository;
    @Mock EmployeeRepository employeeRepository;
    @Mock OrderRepository orderRepository;

    @InjectMocks HrServiceImpl hrService;

    // ── logAttendance ─────────────────────────────────────────────────────────

    @Test
    void logAttendance_duplicateDate_throwsBusinessException() {
        when(attendanceRepository.existsByEmployeeIdAndDate(1L, LocalDate.of(2025, 5, 1))).thenReturn(true);

        AttendanceCreateRequest req = new AttendanceCreateRequest();
        req.setEmployeeId(1L);
        req.setDate(LocalDate.of(2025, 5, 1));
        req.setStatus(AttendanceStatus.PRESENT);

        assertThatThrownBy(() -> hrService.logAttendance(req))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("Đã có bản ghi chấm công");
    }

    @Test
    void logAttendance_newRecord_savesAndReturns() {
        Employee emp = Employee.builder().id(1L).employeeCode("EMP001").build();
        when(attendanceRepository.existsByEmployeeIdAndDate(1L, LocalDate.of(2025, 5, 1))).thenReturn(false);
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(emp));

        Attendance saved = Attendance.builder()
            .id(10L).employee(emp).date(LocalDate.of(2025, 5, 1))
            .status(AttendanceStatus.PRESENT).build();
        when(attendanceRepository.save(any())).thenReturn(saved);

        AttendanceCreateRequest req = new AttendanceCreateRequest();
        req.setEmployeeId(1L);
        req.setDate(LocalDate.of(2025, 5, 1));
        req.setCheckIn(LocalTime.of(8, 0));
        req.setCheckOut(LocalTime.of(17, 0));
        req.setStatus(AttendanceStatus.PRESENT);

        Attendance result = hrService.logAttendance(req);

        assertThat(result.getId()).isEqualTo(10L);
        verify(attendanceRepository).save(any(Attendance.class));
    }

    @Test
    void logAttendance_employeeNotFound_throwsResourceNotFoundException() {
        when(attendanceRepository.existsByEmployeeIdAndDate(eq(99L), any())).thenReturn(false);
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

        AttendanceCreateRequest req = new AttendanceCreateRequest();
        req.setEmployeeId(99L);
        req.setDate(LocalDate.of(2025, 5, 1));
        req.setStatus(AttendanceStatus.PRESENT);

        assertThatThrownBy(() -> hrService.logAttendance(req))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Employee");
    }

    // ── calculatePayroll — effective days ────────────────────────────────────

    @Test
    void calculatePayroll_allPresent_correctEffectiveDays() {
        // May 2025: 22 working days. 22 PRESENT records → effective = 22
        Employee emp = Employee.builder().id(1L).salary(BigDecimal.valueOf(20_000_000))
            .commissionRate(new BigDecimal("0.02")).build();

        List<Attendance> records = List.of(
            att(AttendanceStatus.PRESENT), att(AttendanceStatus.PRESENT),
            att(AttendanceStatus.PRESENT), att(AttendanceStatus.PRESENT),
            att(AttendanceStatus.PRESENT)
        );

        CalculatePayrollRequest req = new CalculatePayrollRequest();
        req.setEmployeeId(1L);
        req.setMonth(5);
        req.setYear(2025);

        when(payrollRepository.findByEmployeeIdAndMonthAndYear(1L, 5, 2025)).thenReturn(Optional.empty());
        when(employeeRepository.findWithDetailsById(1L)).thenReturn(Optional.of(emp));
        when(attendanceRepository.findByEmployeeAndDateRange(eq(1L), any(), any())).thenReturn(records);
        when(orderRepository.sumCommissionByEmployeeAndMonth(1L, 5, 2025)).thenReturn(BigDecimal.ZERO);
        when(payrollRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Payroll result = hrService.calculatePayroll(req);

        // 5 PRESENT → effectiveDays = 5.0
        assertThat(result.getEffectiveDays()).isEqualByComparingTo(new BigDecimal("5.0"));
        assertThat(result.getStatus()).isEqualTo(PayrollStatus.DRAFT);
    }

    @Test
    void calculatePayroll_mixedStatuses_halfDayCountsHalf() {
        // 1 PRESENT + 1 LATE + 1 HALF_DAY + 1 ABSENT + 1 LEAVE = 1 + 1 + 0.5 + 0 + 0 = 2.5
        List<Attendance> records = List.of(
            att(AttendanceStatus.PRESENT),
            att(AttendanceStatus.LATE),
            att(AttendanceStatus.HALF_DAY),
            att(AttendanceStatus.ABSENT),
            att(AttendanceStatus.LEAVE)
        );

        Employee emp = Employee.builder().id(1L).salary(BigDecimal.valueOf(22_000_000))
            .commissionRate(new BigDecimal("0.02")).build();

        CalculatePayrollRequest req = new CalculatePayrollRequest();
        req.setEmployeeId(1L);
        req.setMonth(5);
        req.setYear(2025);

        when(payrollRepository.findByEmployeeIdAndMonthAndYear(1L, 5, 2025)).thenReturn(Optional.empty());
        when(employeeRepository.findWithDetailsById(1L)).thenReturn(Optional.of(emp));
        when(attendanceRepository.findByEmployeeAndDateRange(eq(1L), any(), any())).thenReturn(records);
        when(orderRepository.sumCommissionByEmployeeAndMonth(1L, 5, 2025)).thenReturn(BigDecimal.ZERO);
        when(payrollRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Payroll result = hrService.calculatePayroll(req);

        assertThat(result.getEffectiveDays()).isEqualByComparingTo(new BigDecimal("2.5"));
    }

    @Test
    void calculatePayroll_withCommission_addsToNetSalary() {
        BigDecimal salary = BigDecimal.valueOf(10_000_000);
        BigDecimal commission = BigDecimal.valueOf(5_000_000);
        // May 2025 workDays = 22, attendances = 22 PRESENT → full salary
        List<Attendance> fullMonth = java.util.stream.IntStream.range(0, 22)
            .mapToObj(i -> att(AttendanceStatus.PRESENT))
            .toList();

        Employee emp = Employee.builder().id(1L).salary(salary)
            .commissionRate(new BigDecimal("0.02")).build();

        CalculatePayrollRequest req = new CalculatePayrollRequest();
        req.setEmployeeId(1L);
        req.setMonth(5);
        req.setYear(2025);

        when(payrollRepository.findByEmployeeIdAndMonthAndYear(1L, 5, 2025)).thenReturn(Optional.empty());
        when(employeeRepository.findWithDetailsById(1L)).thenReturn(Optional.of(emp));
        when(attendanceRepository.findByEmployeeAndDateRange(eq(1L), any(), any())).thenReturn(fullMonth);
        when(orderRepository.sumCommissionByEmployeeAndMonth(1L, 5, 2025)).thenReturn(commission);
        when(payrollRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Payroll result = hrService.calculatePayroll(req);

        assertThat(result.getCommission()).isEqualByComparingTo(commission);
        // netSalary = baseSalaryEarned + commission (bonuses=0, deductions=0)
        assertThat(result.getNetSalary()).isEqualByComparingTo(salary.add(commission));
    }

    @Test
    void calculatePayroll_paidStatus_throwsBusinessException() {
        Payroll paid = new Payroll();
        paid.setStatus(PayrollStatus.PAID);
        when(payrollRepository.findByEmployeeIdAndMonthAndYear(1L, 5, 2025)).thenReturn(Optional.of(paid));

        CalculatePayrollRequest req = new CalculatePayrollRequest();
        req.setEmployeeId(1L);
        req.setMonth(5);
        req.setYear(2025);

        assertThatThrownBy(() -> hrService.calculatePayroll(req))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("đã thanh toán");
    }

    @Test
    void calculatePayroll_existingDraft_preservesBonusesAndDeductions() {
        BigDecimal bonuses    = BigDecimal.valueOf(500_000);
        BigDecimal deductions = BigDecimal.valueOf(200_000);
        Payroll existing = new Payroll();
        existing.setStatus(PayrollStatus.DRAFT);
        existing.setBonuses(bonuses);
        existing.setDeductions(deductions);

        Employee emp = Employee.builder().id(1L).salary(BigDecimal.valueOf(10_000_000))
            .commissionRate(new BigDecimal("0.02")).build();

        when(payrollRepository.findByEmployeeIdAndMonthAndYear(1L, 5, 2025)).thenReturn(Optional.of(existing));
        when(employeeRepository.findWithDetailsById(1L)).thenReturn(Optional.of(emp));
        when(attendanceRepository.findByEmployeeAndDateRange(eq(1L), any(), any())).thenReturn(List.of());
        when(orderRepository.sumCommissionByEmployeeAndMonth(1L, 5, 2025)).thenReturn(BigDecimal.ZERO);
        when(payrollRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        CalculatePayrollRequest req = new CalculatePayrollRequest();
        req.setEmployeeId(1L);
        req.setMonth(5);
        req.setYear(2025);

        Payroll result = hrService.calculatePayroll(req);

        assertThat(result.getBonuses()).isEqualByComparingTo(bonuses);
        assertThat(result.getDeductions()).isEqualByComparingTo(deductions);
    }

    // ── updatePayroll ──────────────────────────────────────────────────────────

    @Test
    void updatePayroll_paidStatus_throwsBusinessException() {
        Payroll p = new Payroll();
        p.setStatus(PayrollStatus.PAID);
        when(payrollRepository.findById(1L)).thenReturn(Optional.of(p));

        assertThatThrownBy(() -> hrService.updatePayroll(1L, new PayrollUpdateRequest()))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("đã thanh toán");
    }

    @Test
    void updatePayroll_recalculatesNetSalary() {
        Payroll p = new Payroll();
        p.setStatus(PayrollStatus.DRAFT);
        p.setBaseSalary(BigDecimal.valueOf(10_000_000));
        p.setWorkDays(22);
        p.setEffectiveDays(new BigDecimal("22"));

        when(payrollRepository.findById(1L)).thenReturn(Optional.of(p));
        when(payrollRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        PayrollUpdateRequest req = new PayrollUpdateRequest();
        req.setCommission(BigDecimal.valueOf(1_000_000));
        req.setBonuses(BigDecimal.valueOf(500_000));
        req.setDeductions(BigDecimal.valueOf(200_000));

        Payroll result = hrService.updatePayroll(1L, req);

        // net = 10M (full month) + 1M commission + 0.5M bonus - 0.2M deduction = 11.3M
        assertThat(result.getNetSalary()).isEqualByComparingTo(BigDecimal.valueOf(11_300_000));
    }

    // ── approvePayroll / markPaid ──────────────────────────────────────────────

    @Test
    void approvePayroll_transitions_draftToApproved() {
        Payroll p = new Payroll();
        p.setStatus(PayrollStatus.DRAFT);
        when(payrollRepository.findById(1L)).thenReturn(Optional.of(p));
        when(payrollRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Payroll result = hrService.approvePayroll(1L);

        assertThat(result.getStatus()).isEqualTo(PayrollStatus.APPROVED);
    }

    @Test
    void approvePayroll_alreadyPaid_throwsBusinessException() {
        Payroll p = new Payroll();
        p.setStatus(PayrollStatus.PAID);
        when(payrollRepository.findById(1L)).thenReturn(Optional.of(p));

        assertThatThrownBy(() -> hrService.approvePayroll(1L))
            .isInstanceOf(BusinessException.class);
    }

    @Test
    void markPaid_transitions_approvedToPaid() {
        Payroll p = new Payroll();
        p.setStatus(PayrollStatus.APPROVED);
        when(payrollRepository.findById(1L)).thenReturn(Optional.of(p));
        when(payrollRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Payroll result = hrService.markPaid(1L);

        assertThat(result.getStatus()).isEqualTo(PayrollStatus.PAID);
    }

    // ── countWorkDays (via calculatePayroll) ──────────────────────────────────

    @Test
    void calculatePayroll_may2025_has22WorkDays() {
        Employee emp = Employee.builder().id(1L).salary(BigDecimal.valueOf(22_000_000))
            .commissionRate(new BigDecimal("0.02")).build();

        when(payrollRepository.findByEmployeeIdAndMonthAndYear(1L, 5, 2025)).thenReturn(Optional.empty());
        when(employeeRepository.findWithDetailsById(1L)).thenReturn(Optional.of(emp));
        when(attendanceRepository.findByEmployeeAndDateRange(eq(1L), any(), any())).thenReturn(List.of());
        when(orderRepository.sumCommissionByEmployeeAndMonth(1L, 5, 2025)).thenReturn(BigDecimal.ZERO);
        when(payrollRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        CalculatePayrollRequest req = new CalculatePayrollRequest();
        req.setEmployeeId(1L);
        req.setMonth(5);
        req.setYear(2025);

        Payroll result = hrService.calculatePayroll(req);

        assertThat(result.getWorkDays()).isEqualTo(22);
    }

    // ── helper ───────────────────────────────────────────────────────────────

    private Attendance att(AttendanceStatus status) {
        return Attendance.builder().status(status).date(LocalDate.now()).build();
    }
}

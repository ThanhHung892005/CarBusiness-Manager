package com.carmanagement.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class EmployeeCreateRequest {

    // ── Thông tin tài khoản hệ thống ──────────────────────────────────────
    @NotBlank
    private String username;

    private String password; // bắt buộc khi tạo mới, tùy chọn khi sửa

    @NotBlank
    private String fullName;

    @NotBlank
    @Email
    private String email;

    private String phone;

    @NotBlank
    private String roleName; // ROLE_GIAM_DOC | ROLE_NV_KINH_DOANH | ROLE_KE_TOAN | ROLE_THU_KHO

    private Boolean enabled = true;
    private Boolean locked  = false;

    // ── Thông tin nhân viên ───────────────────────────────────────────────
    @NotBlank
    private String employeeCode;

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

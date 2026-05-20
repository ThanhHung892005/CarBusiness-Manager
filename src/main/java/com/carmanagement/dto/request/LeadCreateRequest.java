package com.carmanagement.dto.request;

import com.carmanagement.enums.LeadSource;
import com.carmanagement.enums.LeadStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class LeadCreateRequest {

    @NotBlank
    private String fullName;

    @NotBlank
    @Pattern(regexp = "^[0-9]{10,11}$", message = "Số điện thoại không hợp lệ")
    private String phone;

    @Email
    private String email;

    @NotNull
    private LeadSource source;

    private LeadStatus status;

    private Long assignedEmployeeId;

    private String notes;
}

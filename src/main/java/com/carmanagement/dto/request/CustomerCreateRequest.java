package com.carmanagement.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CustomerCreateRequest {

    @NotBlank
    private String fullName;

    @NotBlank
    @Pattern(regexp = "^[0-9]{10,11}$", message = "Số điện thoại không hợp lệ")
    private String phone;

    @Email
    private String email;

    private String address;
    private String city;
    private String idNumber;
    private String taxCode;
    private String companyName;
    private Boolean isCorporate = false;
    private String notes;
}

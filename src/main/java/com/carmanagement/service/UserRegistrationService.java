package com.carmanagement.service;

import com.carmanagement.dto.request.UserRegistrationRequest;
import org.springframework.validation.BindingResult;

public interface UserRegistrationService {
    void register(UserRegistrationRequest request, BindingResult bindingResult);
}

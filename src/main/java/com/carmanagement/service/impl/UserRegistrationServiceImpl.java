package com.carmanagement.service.impl;

import com.carmanagement.dto.request.UserRegistrationRequest;
import com.carmanagement.entity.Customer;
import com.carmanagement.entity.Role;
import com.carmanagement.entity.User;
import com.carmanagement.enums.CustomerType;
import com.carmanagement.repository.CustomerRepository;
import com.carmanagement.repository.RoleRepository;
import com.carmanagement.repository.UserRepository;
import com.carmanagement.service.CustomerService;
import com.carmanagement.service.EmailService;
import com.carmanagement.service.UserRegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

@Service
@RequiredArgsConstructor
public class UserRegistrationServiceImpl implements UserRegistrationService {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomerService customerService;
    private final EmailService emailService;

    @Override
    @Transactional
    public void register(UserRegistrationRequest request, BindingResult bindingResult) {
        // Cross-field and uniqueness validations before any DB write
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "passwords.mismatch",
                "Mật khẩu xác nhận không khớp");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            bindingResult.rejectValue("username", "username.exists",
                "Tên đăng nhập đã tồn tại");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            bindingResult.rejectValue("email", "email.exists",
                "Email đã được sử dụng");
        }
        if (customerRepository.existsByPhone(request.getPhone())) {
            bindingResult.rejectValue("phone", "phone.exists",
                "Số điện thoại đã được đăng ký");
        }

        if (bindingResult.hasErrors()) {
            return;
        }

        Role customerRole = roleRepository.findByName("ROLE_CUSTOMER")
            .orElseThrow(() -> new IllegalStateException("ROLE_CUSTOMER chưa được khởi tạo trong database"));

        User user = User.builder()
            .username(request.getUsername())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .fullName(request.getFullName())
            .phone(request.getPhone())
            .enabled(true)
            .locked(false)
            .build();
        user.getRoles().add(customerRole);
        User savedUser = userRepository.save(user);

        Customer customer = Customer.builder()
            .user(savedUser)
            .customerCode(customerService.generateCustomerCode())
            .fullName(request.getFullName())
            .email(request.getEmail())
            .phone(request.getPhone())
            .customerType(CustomerType.NEW)
            .loyaltyPoints(0)
            .isCorporate(false)
            .build();
        customerRepository.save(customer);

        emailService.sendWelcomeEmail(request.getEmail(), request.getFullName(), request.getUsername());
    }
}

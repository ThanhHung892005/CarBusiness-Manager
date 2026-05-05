package com.carmanagement.controller;

import com.carmanagement.dto.request.UserCreateRequest;
import com.carmanagement.entity.User;
import com.carmanagement.repository.RoleRepository;
import com.carmanagement.repository.UserRepository;
import com.carmanagement.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public String list(@RequestParam(defaultValue = "") String keyword,
                       @RequestParam(defaultValue = "") String roleName,
                       @RequestParam(defaultValue = "0") int page,
                       Model model) {
        var pageable = PageRequest.of(page, 15, Sort.by("id").descending());
        model.addAttribute("users", userService.search(keyword, roleName, pageable));
        model.addAttribute("keyword", keyword);
        model.addAttribute("roleName", roleName);
        model.addAttribute("roles", roleRepository.findAll());
        return "admin/users/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("userRequest", new UserCreateRequest());
        model.addAttribute("roles", roleRepository.findAll());
        return "admin/users/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("userRequest") UserCreateRequest request,
                         BindingResult result,
                         RedirectAttributes ra,
                         Model model) {
        // Password required on create
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            result.rejectValue("password", "required", "Mật khẩu là bắt buộc khi tạo mới");
        } else if (request.getPassword().length() < 8) {
            result.rejectValue("password", "size", "Mật khẩu phải có ít nhất 8 ký tự");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            result.rejectValue("username", "exists", "Tên đăng nhập đã tồn tại");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            result.rejectValue("email", "exists", "Email đã được sử dụng");
        }
        if (request.getRoleIds() == null || request.getRoleIds().isEmpty()) {
            result.rejectValue("roleIds", "required", "Phải chọn ít nhất một vai trò");
        }
        if (result.hasErrors()) {
            model.addAttribute("roles", roleRepository.findAll());
            return "admin/users/form";
        }

        var roleList = roleRepository.findAllById(request.getRoleIds());
        User user = User.builder()
            .username(request.getUsername())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .fullName(request.getFullName())
            .phone(request.getPhone())
            .enabled(Boolean.TRUE.equals(request.getEnabled()))
            .locked(Boolean.TRUE.equals(request.getLocked()))
            .build();
        user.getRoles().addAll(roleList);
        userService.save(user);

        ra.addFlashAttribute("success", "Tạo tài khoản thành công: " + request.getUsername());
        return "redirect:/admin/users";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        var user = userService.findById(id);
        var request = new UserCreateRequest();
        request.setUsername(user.getUsername());
        request.setEmail(user.getEmail());
        request.setFullName(user.getFullName());
        request.setPhone(user.getPhone());
        request.setEnabled(user.getEnabled());
        request.setLocked(user.getLocked());
        request.setRoleIds(user.getRoles().stream()
            .map(r -> r.getId())
            .collect(Collectors.toSet()));
        model.addAttribute("userRequest", request);
        model.addAttribute("userId", id);
        model.addAttribute("editUsername", user.getUsername());
        model.addAttribute("roles", roleRepository.findAll());
        return "admin/users/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("userRequest") UserCreateRequest request,
                         BindingResult result,
                         RedirectAttributes ra,
                         Model model) {
        var user = userService.findById(id);

        // Password optional on edit, but must meet length if provided
        if (request.getPassword() != null && !request.getPassword().isBlank()
                && request.getPassword().length() < 8) {
            result.rejectValue("password", "size", "Mật khẩu phải có ít nhất 8 ký tự");
        }
        // Email uniqueness — skip check if unchanged
        if (!user.getEmail().equalsIgnoreCase(request.getEmail())
                && userRepository.existsByEmail(request.getEmail())) {
            result.rejectValue("email", "exists", "Email đã được sử dụng");
        }
        if (request.getRoleIds() == null || request.getRoleIds().isEmpty()) {
            result.rejectValue("roleIds", "required", "Phải chọn ít nhất một vai trò");
        }
        if (result.hasErrors()) {
            model.addAttribute("userId", id);
            model.addAttribute("editUsername", user.getUsername());
            model.addAttribute("roles", roleRepository.findAll());
            return "admin/users/form";
        }

        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setEnabled(Boolean.TRUE.equals(request.getEnabled()));
        user.setLocked(Boolean.TRUE.equals(request.getLocked()));
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        var roleList = roleRepository.findAllById(request.getRoleIds());
        user.getRoles().clear();
        user.getRoles().addAll(roleList);
        userService.save(user);

        ra.addFlashAttribute("success", "Cập nhật tài khoản thành công: " + user.getUsername());
        return "redirect:/admin/users";
    }
}

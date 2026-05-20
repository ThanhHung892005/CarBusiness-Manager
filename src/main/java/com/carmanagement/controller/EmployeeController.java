package com.carmanagement.controller;

import com.carmanagement.dto.request.EmployeeCreateRequest;
import com.carmanagement.entity.Employee;
import com.carmanagement.entity.User;
import com.carmanagement.repository.DepartmentRepository;
import com.carmanagement.repository.RoleRepository;
import com.carmanagement.repository.UserRepository;
import com.carmanagement.service.EmployeeService;
import com.carmanagement.service.ShowroomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/admin/employees")
@RequiredArgsConstructor
@PreAuthorize("hasRole('GIAM_DOC')")
public class EmployeeController {

    private final EmployeeService employeeService;
    private final ShowroomService showroomService;
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    private static final List<String> BUSINESS_ROLES = List.of(
        "ROLE_GIAM_DOC", "ROLE_NV_KINH_DOANH", "ROLE_KE_TOAN", "ROLE_THU_KHO"
    );

    @GetMapping
    public String list(@RequestParam(defaultValue = "") String keyword,
                       @RequestParam(required = false) Long showroomId,
                       @RequestParam(required = false) Long deptId,
                       @RequestParam(defaultValue = "0") int page,
                       Model model) {
        var pageable = PageRequest.of(page, 15, Sort.by("id").descending());
        model.addAttribute("employees", employeeService.search(keyword, showroomId, deptId, pageable));
        model.addAttribute("keyword", keyword);
        model.addAttribute("showrooms", showroomService.findAllActive());
        model.addAttribute("departments", departmentRepository.findAll());
        model.addAttribute("selectedShowroomId", showroomId);
        model.addAttribute("selectedDeptId", deptId);
        return "admin/employees/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        var req = new EmployeeCreateRequest();
        req.setEmployeeCode(employeeService.suggestNextCode());
        model.addAttribute("employeeRequest", req);
        populateForm(model);
        return "admin/employees/form";
    }

    @PostMapping
    @Transactional
    public String create(@Valid @ModelAttribute("employeeRequest") EmployeeCreateRequest req,
                         BindingResult result,
                         RedirectAttributes ra,
                         Model model) {
        if (req.getPassword() == null || req.getPassword().isBlank()) {
            result.rejectValue("password", "required", "Mật khẩu là bắt buộc khi tạo mới");
        } else if (req.getPassword().length() < 8) {
            result.rejectValue("password", "size", "Mật khẩu phải có ít nhất 8 ký tự");
        }
        if (userRepository.existsByUsername(req.getUsername())) {
            result.rejectValue("username", "exists", "Tên đăng nhập đã tồn tại");
        }
        if (userRepository.existsByEmail(req.getEmail())) {
            result.rejectValue("email", "exists", "Email đã được sử dụng");
        }
        if (result.hasErrors()) {
            populateForm(model);
            return "admin/employees/form";
        }

        var role = roleRepository.findByName(req.getRoleName()).orElseThrow();
        var user = User.builder()
            .username(req.getUsername())
            .email(req.getEmail())
            .password(passwordEncoder.encode(req.getPassword()))
            .fullName(req.getFullName())
            .phone(req.getPhone())
            .enabled(true)
            .locked(false)
            .build();
        user.getRoles().add(role);
        userRepository.save(user);

        var emp = Employee.builder()
            .employeeCode(req.getEmployeeCode().toUpperCase())
            .user(user)
            .position(req.getPosition())
            .hireDate(req.getHireDate())
            .salary(req.getSalary())
            .commissionRate(req.getCommissionRate() != null ? req.getCommissionRate() : new BigDecimal("0.02"))
            .active(true)
            .build();
        if (req.getShowroomId() != null) {
            emp.setShowroom(showroomService.findById(req.getShowroomId()));
        }
        if (req.getDepartmentId() != null) {
            emp.setDepartment(departmentRepository.findById(req.getDepartmentId()).orElse(null));
        }
        employeeService.save(emp);

        ra.addFlashAttribute("success", "Thêm nhân viên thành công: " + emp.getEmployeeCode());
        return "redirect:/admin/employees";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        var emp = employeeService.findById(id);
        var user = emp.getUser();

        var req = new EmployeeCreateRequest();
        req.setUsername(user.getUsername());
        req.setFullName(user.getFullName());
        req.setEmail(user.getEmail());
        req.setPhone(user.getPhone());
        req.setEnabled(user.getEnabled());
        req.setLocked(user.getLocked());
        req.setRoleName(user.getRoles().stream().findFirst().map(r -> r.getName()).orElse(""));
        req.setEmployeeCode(emp.getEmployeeCode());
        req.setPosition(emp.getPosition());
        req.setHireDate(emp.getHireDate());
        req.setSalary(emp.getSalary());
        req.setCommissionRate(emp.getCommissionRate());
        req.setShowroomId(emp.getShowroom() != null ? emp.getShowroom().getId() : null);
        req.setDepartmentId(emp.getDepartment() != null ? emp.getDepartment().getId() : null);

        model.addAttribute("employeeRequest", req);
        model.addAttribute("employeeId", id);
        model.addAttribute("editUsername", user.getUsername());
        populateForm(model);
        return "admin/employees/form";
    }

    @PostMapping("/{id}")
    @Transactional
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("employeeRequest") EmployeeCreateRequest req,
                         BindingResult result,
                         RedirectAttributes ra,
                         Model model) {
        var emp = employeeService.findById(id);
        var user = emp.getUser();

        if (req.getPassword() != null && !req.getPassword().isBlank()
                && req.getPassword().length() < 8) {
            result.rejectValue("password", "size", "Mật khẩu phải có ít nhất 8 ký tự");
        }
        if (!user.getEmail().equalsIgnoreCase(req.getEmail())
                && userRepository.existsByEmail(req.getEmail())) {
            result.rejectValue("email", "exists", "Email đã được sử dụng");
        }
        if (result.hasErrors()) {
            model.addAttribute("employeeId", id);
            model.addAttribute("editUsername", user.getUsername());
            populateForm(model);
            return "admin/employees/form";
        }

        // Cập nhật User
        user.setFullName(req.getFullName());
        user.setEmail(req.getEmail());
        user.setPhone(req.getPhone());
        user.setEnabled(Boolean.TRUE.equals(req.getEnabled()));
        user.setLocked(Boolean.TRUE.equals(req.getLocked()));
        if (req.getPassword() != null && !req.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(req.getPassword()));
        }
        if (req.getRoleName() != null && !req.getRoleName().isBlank()) {
            var role = roleRepository.findByName(req.getRoleName()).orElseThrow();
            user.getRoles().clear();
            user.getRoles().add(role);
        }
        userRepository.save(user);

        // Cập nhật Employee
        emp.setPosition(req.getPosition());
        emp.setHireDate(req.getHireDate());
        emp.setSalary(req.getSalary());
        if (req.getCommissionRate() != null) emp.setCommissionRate(req.getCommissionRate());
        emp.setShowroom(req.getShowroomId() != null ? showroomService.findById(req.getShowroomId()) : null);
        emp.setDepartment(req.getDepartmentId() != null
            ? departmentRepository.findById(req.getDepartmentId()).orElse(null) : null);
        employeeService.save(emp);

        ra.addFlashAttribute("success", "Cập nhật nhân viên thành công");
        return "redirect:/admin/employees";
    }

    private void populateForm(Model model) {
        model.addAttribute("showrooms", showroomService.findAllActive());
        model.addAttribute("departments", departmentRepository.findAll());
        model.addAttribute("businessRoles", BUSINESS_ROLES);
    }
}

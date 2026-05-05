package com.carmanagement.controller;

import com.carmanagement.dto.request.EmployeeCreateRequest;
import com.carmanagement.entity.Department;
import com.carmanagement.entity.Employee;
import com.carmanagement.repository.DepartmentRepository;
import com.carmanagement.repository.UserRepository;
import com.carmanagement.service.EmployeeService;
import com.carmanagement.service.ShowroomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/employees")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class EmployeeController {

    private final EmployeeService employeeService;
    private final ShowroomService showroomService;
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;

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
        model.addAttribute("employeeRequest", new EmployeeCreateRequest());
        populateForm(model);
        return "admin/employees/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("employeeRequest") EmployeeCreateRequest request,
                         BindingResult result,
                         RedirectAttributes ra,
                         Model model) {
        if (result.hasErrors()) {
            populateForm(model);
            return "admin/employees/form";
        }
        var user = userRepository.findById(request.getUserId()).orElseThrow();
        var employee = new Employee();
        employee.setEmployeeCode(request.getEmployeeCode());
        employee.setPosition(request.getPosition());
        employee.setHireDate(request.getHireDate());
        employee.setSalary(request.getSalary());
        employee.setCommissionRate(request.getCommissionRate());
        employee.setUser(user);
        employee.setActive(true);
        if (request.getShowroomId() != null) {
            employee.setShowroom(showroomService.findById(request.getShowroomId()));
        }
        if (request.getDepartmentId() != null) {
            employee.setDepartment(departmentRepository.findById(request.getDepartmentId()).orElse(null));
        }
        employeeService.save(employee);
        ra.addFlashAttribute("success", "Thêm nhân viên thành công: " + request.getEmployeeCode());
        return "redirect:/admin/employees";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        var emp = employeeService.findById(id);
        var request = new EmployeeCreateRequest();
        request.setEmployeeCode(emp.getEmployeeCode());
        request.setUserId(emp.getUser().getId());
        request.setPosition(emp.getPosition());
        request.setHireDate(emp.getHireDate());
        request.setSalary(emp.getSalary());
        request.setCommissionRate(emp.getCommissionRate());
        request.setShowroomId(emp.getShowroom() != null ? emp.getShowroom().getId() : null);
        request.setDepartmentId(emp.getDepartment() != null ? emp.getDepartment().getId() : null);
        model.addAttribute("employeeRequest", request);
        model.addAttribute("employeeId", id);
        populateForm(model);
        return "admin/employees/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("employeeRequest") EmployeeCreateRequest request,
                         BindingResult result,
                         RedirectAttributes ra,
                         Model model) {
        if (result.hasErrors()) {
            model.addAttribute("employeeId", id);
            populateForm(model);
            return "admin/employees/form";
        }
        var emp = employeeService.findById(id);
        emp.setPosition(request.getPosition());
        emp.setHireDate(request.getHireDate());
        emp.setSalary(request.getSalary());
        emp.setCommissionRate(request.getCommissionRate());
        if (request.getShowroomId() != null) {
            emp.setShowroom(showroomService.findById(request.getShowroomId()));
        } else {
            emp.setShowroom(null);
        }
        if (request.getDepartmentId() != null) {
            emp.setDepartment(departmentRepository.findById(request.getDepartmentId()).orElse(null));
        } else {
            emp.setDepartment(null);
        }
        employeeService.save(emp);
        ra.addFlashAttribute("success", "Cập nhật nhân viên thành công");
        return "redirect:/admin/employees";
    }

    private void populateForm(Model model) {
        model.addAttribute("showrooms", showroomService.findAllActive());
        model.addAttribute("departments", departmentRepository.findAll());
        model.addAttribute("users", userRepository.findAll(Sort.by("fullName")));
    }
}

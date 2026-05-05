package com.carmanagement.service;

import com.carmanagement.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface EmployeeService {
    Page<Employee> search(String keyword, Long showroomId, Long deptId, Pageable pageable);
    Employee findById(Long id);
    Optional<Employee> findByUserId(Long userId);
    Employee save(Employee employee);
}

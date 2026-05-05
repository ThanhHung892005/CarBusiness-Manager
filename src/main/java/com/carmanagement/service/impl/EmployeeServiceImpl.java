package com.carmanagement.service.impl;

import com.carmanagement.entity.Employee;
import com.carmanagement.exception.ResourceNotFoundException;
import com.carmanagement.repository.EmployeeRepository;
import com.carmanagement.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Override
    public Page<Employee> search(String keyword, Long showroomId, Long deptId, Pageable pageable) {
        return employeeRepository.search(keyword, showroomId, deptId, pageable);
    }

    @Override
    public Employee findById(Long id) {
        return employeeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Employee", id));
    }

    @Override
    public Optional<Employee> findByUserId(Long userId) {
        return employeeRepository.findByUserId(userId);
    }

    @Override
    @Transactional
    public Employee save(Employee employee) {
        return employeeRepository.save(employee);
    }
}

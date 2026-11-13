package com.epam.rd.autocode.assessment.appliances.service.impl;

import com.epam.rd.autocode.assessment.appliances.aspect.Loggable;
import com.epam.rd.autocode.assessment.appliances.model.Employee;
import com.epam.rd.autocode.assessment.appliances.repository.EmployeeRepository;
import com.epam.rd.autocode.assessment.appliances.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Page<Employee> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public List<Employee> findAll() {
        return repository.findAll();
    }

    @Override
    public Employee findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Employee not found: " + id));
    }

    @Loggable
    @Override
    public Employee save(Employee employee) {
        if (employee.getPassword() != null && !employee.getPassword().startsWith("$2")) {
            employee.setPassword(passwordEncoder.encode(employee.getPassword()));
        }
        return repository.save(employee);
    }

    @Loggable
    @Override
    public void delete(Long id) {
        String currentEmail = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication().getName();

        Employee employee = findById(id);

        if (employee.getEmail().equalsIgnoreCase(currentEmail)) {
            throw new IllegalStateException("Cannot delete yourself");
        }

        repository.deleteById(id);
    }
}
package com.epam.rd.autocode.assessment.appliances.service;

import com.epam.rd.autocode.assessment.appliances.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EmployeeService {
    Page<Employee> findAll(Pageable pageable);
    List<Employee> findAll();
    Employee findById(Long id);
    Employee save(Employee employee);
    void delete(Long id);
}
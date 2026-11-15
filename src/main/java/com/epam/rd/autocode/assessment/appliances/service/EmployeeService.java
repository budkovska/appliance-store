package com.epam.rd.autocode.assessment.appliances.service;

import com.epam.rd.autocode.assessment.appliances.dto.EmployeeDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EmployeeService {
    Page<EmployeeDto> findAll(Pageable pageable);
    List<EmployeeDto> findAll();
    EmployeeDto findById(Long id);
    EmployeeDto save(EmployeeDto dto);
    void delete(Long id);
}
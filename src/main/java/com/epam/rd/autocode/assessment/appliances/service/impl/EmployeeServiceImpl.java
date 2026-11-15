package com.epam.rd.autocode.assessment.appliances.service.impl;

import com.epam.rd.autocode.assessment.appliances.aspect.Loggable;
import com.epam.rd.autocode.assessment.appliances.dto.EmployeeDto;
import com.epam.rd.autocode.assessment.appliances.model.Employee;
import com.epam.rd.autocode.assessment.appliances.repository.EmployeeRepository;
import com.epam.rd.autocode.assessment.appliances.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository repository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public Page<EmployeeDto> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(this::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeDto> findAll() {
        return repository.findAll().stream().map(this::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeDto findById(Long id) {
        return toDto(find(id));
    }

    @Loggable
    @Override
    public EmployeeDto save(EmployeeDto dto) {
        Employee entity = dto.getId() == null ? new Employee() : find(dto.getId());
        String currentHash = entity.getPassword();

        modelMapper.map(dto, entity);

        entity.setPassword(StringUtils.hasText(dto.getPassword())
                ? passwordEncoder.encode(dto.getPassword())
                : currentHash);
        return toDto(repository.save(entity));
    }

    @Loggable
    @Override
    public void delete(Long id) {
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Employee employee = find(id);

        if (employee.getEmail().equalsIgnoreCase(currentEmail)) {
            throw new IllegalStateException("Cannot delete yourself");
        }
        repository.delete(employee);
        repository.flush();
    }

    private Employee find(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Employee not found: " + id));
    }

    private EmployeeDto toDto(Employee employee) {
        EmployeeDto dto = modelMapper.map(employee, EmployeeDto.class);
        dto.setPassword(null);
        return dto;
    }
}
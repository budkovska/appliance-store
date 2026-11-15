package com.epam.rd.autocode.assessment.appliances.service;

import com.epam.rd.autocode.assessment.appliances.dto.ApplianceDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ApplianceService {
    Page<ApplianceDto> findAll(Pageable pageable);
    List<ApplianceDto> findAll();
    ApplianceDto findById(Long id);
    ApplianceDto save(ApplianceDto dto);
    void delete(Long id);
}

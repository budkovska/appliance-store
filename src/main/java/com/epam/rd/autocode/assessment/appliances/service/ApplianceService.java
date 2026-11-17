package com.epam.rd.autocode.assessment.appliances.service;

import com.epam.rd.autocode.assessment.appliances.dto.ApplianceDto;
import com.epam.rd.autocode.assessment.appliances.model.Category;
import com.epam.rd.autocode.assessment.appliances.model.PowerType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ApplianceService {
    Page<ApplianceDto> search(String search, Category category, PowerType powerType,
                              Long manufacturerId, Pageable pageable);
    List<ApplianceDto> findAll();
    ApplianceDto findById(Long id);
    ApplianceDto save(ApplianceDto dto);
    void delete(Long id);
}

package com.epam.rd.autocode.assessment.appliances.service;

import com.epam.rd.autocode.assessment.appliances.dto.ManufacturerDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ManufacturerService {
    Page<ManufacturerDto> findAll(Pageable pageable);
    List<ManufacturerDto> findAll();
    ManufacturerDto findById(Long id);
    ManufacturerDto save(ManufacturerDto dto);
    void delete(Long id);
}

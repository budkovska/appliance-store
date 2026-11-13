package com.epam.rd.autocode.assessment.appliances.service;

import com.epam.rd.autocode.assessment.appliances.model.Manufacturer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ManufacturerService {
    Page<Manufacturer> findAll(Pageable pageable);
    List<Manufacturer> findAll();
    Manufacturer findById(Long id);
    Manufacturer save(Manufacturer manufacturer);
    void delete(Long id);
}

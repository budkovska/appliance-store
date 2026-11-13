package com.epam.rd.autocode.assessment.appliances.service;

import com.epam.rd.autocode.assessment.appliances.model.Appliance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ApplianceService {
    Page<Appliance> findAll(Pageable pageable);
    List<Appliance> findAll();
    Appliance findById(Long id);
    Appliance save(Appliance appliance);
    void delete(Long id);
}

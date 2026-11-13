package com.epam.rd.autocode.assessment.appliances.service.impl;

import com.epam.rd.autocode.assessment.appliances.aspect.Loggable;
import com.epam.rd.autocode.assessment.appliances.model.Appliance;
import com.epam.rd.autocode.assessment.appliances.repository.ApplianceRepository;
import com.epam.rd.autocode.assessment.appliances.repository.ManufacturerRepository;
import com.epam.rd.autocode.assessment.appliances.service.ApplianceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ApplianceServiceImpl implements ApplianceService {

    private final ApplianceRepository repository;
    private final ManufacturerRepository manufacturerRepository;

    @Override
    public Page<Appliance> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public List<Appliance> findAll() {
        return repository.findAll();
    }

    @Override
    public Appliance findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Appliance not found: " + id));
    }

    @Loggable
    @Override
    public Appliance save(Appliance appliance) {
        Long mId = appliance.getManufacturer().getId();
        appliance.setManufacturer(manufacturerRepository.getReferenceById(mId));
        return repository.save(appliance);
    }

    @Loggable
    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}

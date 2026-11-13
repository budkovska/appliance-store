package com.epam.rd.autocode.assessment.appliances.service.impl;

import com.epam.rd.autocode.assessment.appliances.aspect.Loggable;
import com.epam.rd.autocode.assessment.appliances.model.Manufacturer;
import com.epam.rd.autocode.assessment.appliances.repository.ManufacturerRepository;
import com.epam.rd.autocode.assessment.appliances.service.ManufacturerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ManufacturerServiceImpl implements ManufacturerService {

    private final ManufacturerRepository repository;

    @Override
    public Page<Manufacturer> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public List<Manufacturer> findAll() {
        return repository.findAll();
    }

    @Override
    public Manufacturer findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Manufacturer not found: " + id));
    }

    @Loggable
    @Override
    public Manufacturer save(Manufacturer m) {
        return repository.save(m);
    }

    @Loggable
    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}

package com.epam.rd.autocode.assessment.appliances.service.impl;

import com.epam.rd.autocode.assessment.appliances.aspect.Loggable;
import com.epam.rd.autocode.assessment.appliances.dto.ApplianceDto;
import com.epam.rd.autocode.assessment.appliances.exception.ResourceInUseException;
import com.epam.rd.autocode.assessment.appliances.exception.ResourceNotFoundException;
import com.epam.rd.autocode.assessment.appliances.mapper.ApplianceMapper;
import com.epam.rd.autocode.assessment.appliances.model.Appliance;
import com.epam.rd.autocode.assessment.appliances.model.Manufacturer;
import com.epam.rd.autocode.assessment.appliances.repository.ApplianceRepository;
import com.epam.rd.autocode.assessment.appliances.repository.ManufacturerRepository;
import com.epam.rd.autocode.assessment.appliances.service.ApplianceService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ApplianceServiceImpl implements ApplianceService {

    private final ApplianceRepository repository;
    private final ManufacturerRepository manufacturerRepository;
    private final ApplianceMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public Page<ApplianceDto> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApplianceDto> findAll() {
        return repository.findAll().stream().map(mapper::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ApplianceDto findById(Long id) {
        return mapper.toDto(find(id));
    }

    @Loggable
    @Override
    public ApplianceDto save(ApplianceDto dto) {
        Manufacturer manufacturer = manufacturerRepository.findById(dto.getManufacturerId())
                .orElseThrow(() -> new ResourceNotFoundException("Manufacturer", dto.getManufacturerId()));
        Appliance entity = dto.getId() == null ? new Appliance() : find(dto.getId());
        mapper.copyToEntity(dto, entity, manufacturer);
        return mapper.toDto(repository.save(entity));
    }

    @Loggable
    @Override
    public void delete(Long id) {
        Appliance entity = find(id);
        try {
            repository.delete(entity);
            repository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new ResourceInUseException("Appliance", id);
        }
    }

    private Appliance find(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appliance", id));
    }
}
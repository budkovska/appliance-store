package com.epam.rd.autocode.assessment.appliances.service.impl;

import com.epam.rd.autocode.assessment.appliances.aspect.Loggable;
import com.epam.rd.autocode.assessment.appliances.dto.ManufacturerDto;
import com.epam.rd.autocode.assessment.appliances.model.Manufacturer;
import com.epam.rd.autocode.assessment.appliances.repository.ManufacturerRepository;
import com.epam.rd.autocode.assessment.appliances.service.ManufacturerService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional
public class ManufacturerServiceImpl implements ManufacturerService {

    private final ManufacturerRepository repository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<ManufacturerDto> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(this::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ManufacturerDto> findAll() {
        return repository.findAll().stream().map(this::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ManufacturerDto findById(Long id) {
        return toDto(find(id));
    }

    @Loggable
    @Override
    public ManufacturerDto save(ManufacturerDto dto) {
        Manufacturer entity = dto.getId() == null ? new Manufacturer() : find(dto.getId());
        modelMapper.map(dto, entity);
        return toDto(repository.save(entity));
    }

    @Loggable
    @Override
    public void delete(Long id) {
        repository.deleteById(id);
        repository.flush();
    }

    private Manufacturer find(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Manufacturer not found: " + id));
    }

    private ManufacturerDto toDto(Manufacturer m) {
        return modelMapper.map(m, ManufacturerDto.class);
    }
}
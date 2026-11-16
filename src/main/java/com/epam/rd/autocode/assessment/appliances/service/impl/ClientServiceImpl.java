package com.epam.rd.autocode.assessment.appliances.service.impl;

import com.epam.rd.autocode.assessment.appliances.aspect.Loggable;
import com.epam.rd.autocode.assessment.appliances.dto.ClientDto;
import com.epam.rd.autocode.assessment.appliances.exception.DuplicateEmailException;
import com.epam.rd.autocode.assessment.appliances.exception.ResourceInUseException;
import com.epam.rd.autocode.assessment.appliances.exception.ResourceNotFoundException;
import com.epam.rd.autocode.assessment.appliances.model.Client;
import com.epam.rd.autocode.assessment.appliances.repository.ClientRepository;
import com.epam.rd.autocode.assessment.appliances.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ClientServiceImpl implements ClientService {

    private final ClientRepository repository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('EMPLOYEE')")
    public Page<ClientDto> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(this::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('EMPLOYEE')")
    public List<ClientDto> findAll() {
        return repository.findAll().stream().map(this::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ClientDto findById(Long id) {
        return toDto(find(id));
    }

    @Loggable
    @Override
    @PreAuthorize("isAnonymous()")
    public ClientDto register(ClientDto dto) {
        dto.setId(null);          // реєстрація ніколи не змінює існуючого клієнта
        return persist(dto);
    }

    @Loggable
    @Override
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ClientDto save(ClientDto dto) {
        return persist(dto);
    }

    private ClientDto persist(ClientDto dto) {
        Client entity = dto.getId() == null ? new Client() : find(dto.getId());
        String currentHash = entity.getPassword();

        modelMapper.map(dto, entity);
        entity.setPassword(StringUtils.hasText(dto.getPassword())
                ? passwordEncoder.encode(dto.getPassword())
                : currentHash);

        try {
            return toDto(repository.saveAndFlush(entity));
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEmailException(dto.getEmail());
        }
    }

    @Loggable
    @Override
    @PreAuthorize("hasRole('EMPLOYEE')")
    public void delete(Long id) {
        Client entity = find(id);
        try {
            repository.delete(entity);
            repository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new ResourceInUseException("Client", id);
        }
    }

    private Client find(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client", id));
    }

    private ClientDto toDto(Client client) {
        ClientDto dto = modelMapper.map(client, ClientDto.class);
        dto.setPassword(null);
        return dto;
    }
}
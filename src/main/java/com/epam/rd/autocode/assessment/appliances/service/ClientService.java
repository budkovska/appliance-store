package com.epam.rd.autocode.assessment.appliances.service;

import com.epam.rd.autocode.assessment.appliances.dto.ClientDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ClientService {
    Page<ClientDto> findAll(Pageable pageable);
    List<ClientDto> findAll();
    ClientDto findById(Long id);
    ClientDto register(ClientDto dto);
    ClientDto save(ClientDto dto);
    void delete(Long id);
}

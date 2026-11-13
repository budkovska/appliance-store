package com.epam.rd.autocode.assessment.appliances.service;

import com.epam.rd.autocode.assessment.appliances.model.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ClientService {
    Page<Client> findAll(Pageable pageable);
    List<Client> findAll();
    Client findById(Long id);
    Client save(Client client);
    void delete(Long id);
}

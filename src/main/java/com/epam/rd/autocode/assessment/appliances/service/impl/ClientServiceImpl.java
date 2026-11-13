package com.epam.rd.autocode.assessment.appliances.service.impl;

import com.epam.rd.autocode.assessment.appliances.aspect.Loggable;
import com.epam.rd.autocode.assessment.appliances.model.Client;
import com.epam.rd.autocode.assessment.appliances.repository.ClientRepository;
import com.epam.rd.autocode.assessment.appliances.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository repository;

    @Override
    public Page<Client> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public List<Client> findAll() {
        return repository.findAll();
    }

    @Override
    public Client findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Client not found: " + id));
    }

    @Loggable
    @Override
    public Client save(Client client) {
        return repository.save(client);
    }

    @Loggable
    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
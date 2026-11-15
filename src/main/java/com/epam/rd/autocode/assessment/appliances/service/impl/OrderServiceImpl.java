package com.epam.rd.autocode.assessment.appliances.service.impl;

import com.epam.rd.autocode.assessment.appliances.aspect.Loggable;
import com.epam.rd.autocode.assessment.appliances.dto.OrderDto;
import com.epam.rd.autocode.assessment.appliances.mapper.OrderMapper;
import com.epam.rd.autocode.assessment.appliances.model.Appliance;
import com.epam.rd.autocode.assessment.appliances.model.Client;
import com.epam.rd.autocode.assessment.appliances.model.OrderRow;
import com.epam.rd.autocode.assessment.appliances.model.Orders;
import com.epam.rd.autocode.assessment.appliances.repository.ApplianceRepository;
import com.epam.rd.autocode.assessment.appliances.repository.ClientRepository;
import com.epam.rd.autocode.assessment.appliances.repository.EmployeeRepository;
import com.epam.rd.autocode.assessment.appliances.repository.OrdersRepository;
import com.epam.rd.autocode.assessment.appliances.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrdersRepository ordersRepository;
    private final ClientRepository clientRepository;
    private final EmployeeRepository employeeRepository;
    private final ApplianceRepository applianceRepository;
    private final OrderMapper orderMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<OrderDto> findAllFor(String email, boolean employee, Pageable pageable) {
        Page<Orders> page = employee
                ? ordersRepository.findAll(pageable)
                : ordersRepository.findByClientEmail(email, pageable);
        return page.map(orderMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDto findById(Long id, String email, boolean employee) {
        return orderMapper.toDto(getAccessible(id, email, employee));
    }

    @Loggable
    @Override
    public OrderDto create(String email, boolean employee, Long clientId) {
        Client client;
        if (employee) {
            if (clientId == null) {
                throw new IllegalArgumentException("Client must be selected");
            }
            client = clientRepository.findById(clientId)
                    .orElseThrow(() -> new NoSuchElementException("Client not found: " + clientId));
        } else {
            client = clientRepository.findFirstByEmail(email)
                    .orElseThrow(() -> new NoSuchElementException("Client not found: " + email));
        }
        Orders order = new Orders();
        order.setClient(client);
        return orderMapper.toDto(ordersRepository.save(order));
    }

    @Loggable
    @Override
    public void addRow(Long orderId, Long applianceId, Long number, String email, boolean employee) {
        if (number == null || number <= 0) {
            throw new IllegalArgumentException("Number must be positive");
        }
        Orders order = getEditable(orderId, email, employee);
        Appliance appliance = applianceRepository.findById(applianceId)
                .orElseThrow(() -> new NoSuchElementException("Appliance not found: " + applianceId));

        OrderRow row = new OrderRow();
        row.setAppliance(appliance);
        row.setNumber(number);
        row.setAmount(appliance.getPrice().multiply(BigDecimal.valueOf(number)));
        order.getOrderRowSet().add(row);
    }

    @Loggable
    @Override
    public void removeRow(Long orderId, Long rowId, String email, boolean employee) {
        Orders order = getEditable(orderId, email, employee);
        order.getOrderRowSet().removeIf(r -> r.getId().equals(rowId));
    }

    @Loggable
    @Override
    public void delete(Long orderId, String email, boolean employee) {
        ordersRepository.delete(getAccessible(orderId, email, employee));
    }

    @Loggable
    @Override
    public void approve(Long orderId, String employeeEmail) {
        Orders order = find(orderId);
        order.setEmployee(employeeRepository.findFirstByEmail(employeeEmail)
                .orElseThrow(() -> new NoSuchElementException("Employee not found: " + employeeEmail)));
        order.setApproved(true);
    }

    @Loggable
    @Override
    public void unapprove(Long orderId) {
        Orders order = find(orderId);
        order.setEmployee(null);
        order.setApproved(false);
    }

    private Orders find(Long id) {
        return ordersRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Order not found: " + id));
    }

    private Orders getAccessible(Long id, String email, boolean employee) {
        Orders order = find(id);
        if (!employee && !order.getClient().getEmail().equals(email)) {
            throw new AccessDeniedException("Not your order");
        }
        return order;
    }

    private Orders getEditable(Long id, String email, boolean employee) {
        Orders order = getAccessible(id, email, employee);
        if (Boolean.TRUE.equals(order.getApproved())) {
            throw new IllegalStateException("Approved order cannot be changed");
        }
        return order;
    }
}
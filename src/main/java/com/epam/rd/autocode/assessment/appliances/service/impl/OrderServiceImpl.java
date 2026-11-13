package com.epam.rd.autocode.assessment.appliances.service.impl;

import com.epam.rd.autocode.assessment.appliances.aspect.Loggable;
import com.epam.rd.autocode.assessment.appliances.model.Appliance;
import com.epam.rd.autocode.assessment.appliances.model.Client;
import com.epam.rd.autocode.assessment.appliances.model.OrderRow;
import com.epam.rd.autocode.assessment.appliances.model.Orders;
import com.epam.rd.autocode.assessment.appliances.repository.ApplianceRepository;
import com.epam.rd.autocode.assessment.appliances.repository.ClientRepository;
import com.epam.rd.autocode.assessment.appliances.repository.EmployeeRepository;
import com.epam.rd.autocode.assessment.appliances.repository.OrdersRepository;
import com.epam.rd.autocode.assessment.appliances.service.OrderService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

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

    @Override
    @Transactional(readOnly = true)
    public Page<Orders> findAllFor(String email, boolean employee, Pageable pageable) {
        return employee ? ordersRepository.findAll(pageable)
                : ordersRepository.findByClientEmail(email, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Orders findById(Long id, String email, boolean employee) {
        return getAccessible(id, email, employee);
    }

    @Loggable
    @Override
    public Orders create(String email, boolean employee, Long clientId) {
        Client client = employee
                ? clientRepository.findById(clientId)
                .orElseThrow(() -> new NoSuchElementException("Client not found: " + clientId))
                : clientRepository.findFirstByEmail(email).orElseThrow();
        Orders order = new Orders();
        order.setClient(client);
        return ordersRepository.save(order);
    }

    @Loggable
    @Override
    public void addRow(Long orderId, Long applianceId, Long number, String email, boolean employee) {
        Orders order = getEditable(orderId, email, employee);
        Appliance appliance = applianceRepository.findById(applianceId).orElseThrow();
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
        Orders order = ordersRepository.findById(orderId).orElseThrow();
        order.setEmployee(employeeRepository.findFirstByEmail(employeeEmail).orElseThrow());
        order.setApproved(true);
    }

    @Loggable
    @Override
    public void unapprove(Long orderId) {
        Orders order = ordersRepository.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("Order not found: " + orderId));
        order.setEmployee(null);
        order.setApproved(false);
    }

    private Orders getAccessible(Long id, String email, boolean employee) {
        Orders order = ordersRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Order not found: " + id));
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

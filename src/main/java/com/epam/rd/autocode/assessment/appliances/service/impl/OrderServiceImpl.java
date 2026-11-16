package com.epam.rd.autocode.assessment.appliances.service.impl;

import com.epam.rd.autocode.assessment.appliances.aspect.Loggable;
import com.epam.rd.autocode.assessment.appliances.dto.OrderDto;
import com.epam.rd.autocode.assessment.appliances.exception.InvalidQuantityException;
import com.epam.rd.autocode.assessment.appliances.exception.OrderLockedException;
import com.epam.rd.autocode.assessment.appliances.exception.ResourceNotFoundException;
import com.epam.rd.autocode.assessment.appliances.mapper.OrderMapper;
import com.epam.rd.autocode.assessment.appliances.model.Appliance;
import com.epam.rd.autocode.assessment.appliances.model.Client;
import com.epam.rd.autocode.assessment.appliances.model.OrderRow;
import com.epam.rd.autocode.assessment.appliances.model.Orders;
import com.epam.rd.autocode.assessment.appliances.repository.ApplianceRepository;
import com.epam.rd.autocode.assessment.appliances.repository.ClientRepository;
import com.epam.rd.autocode.assessment.appliances.repository.EmployeeRepository;
import com.epam.rd.autocode.assessment.appliances.repository.OrdersRepository;
import com.epam.rd.autocode.assessment.appliances.security.CurrentUser;
import com.epam.rd.autocode.assessment.appliances.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private static final String EMPLOYEE_OR_OWNER =
            "hasRole('EMPLOYEE') or @orderSecurity.isOwner(#orderId, authentication)";

    private final OrdersRepository ordersRepository;
    private final ClientRepository clientRepository;
    private final EmployeeRepository employeeRepository;
    private final ApplianceRepository applianceRepository;
    private final OrderMapper orderMapper;

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("isAuthenticated()")
    public Page<OrderDto> findAllForCurrentUser(Pageable pageable) {
        Page<Orders> page = CurrentUser.isEmployee()
                ? ordersRepository.findAll(pageable)
                : ordersRepository.findByClientEmail(CurrentUser.email(), pageable);
        return page.map(orderMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('EMPLOYEE') or @orderSecurity.isOwner(#id, authentication)")
    public OrderDto findById(Long id) {
        return orderMapper.toDto(find(id));
    }

    @Loggable
    @Override
    @PreAuthorize("hasRole('EMPLOYEE')")
    public OrderDto createForClient(Long clientId) {
        if (clientId == null) {
            throw new IllegalArgumentException("Client must be selected");
        }
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client", clientId));
        return createFor(client);
    }

    @Loggable
    @Override
    @PreAuthorize("hasRole('CLIENT')")
    public OrderDto createForCurrentClient() {
        String email = CurrentUser.email();
        Client client = clientRepository.findFirstByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("Client", email));
        return createFor(client);
    }

    @Loggable
    @Override
    @PreAuthorize(EMPLOYEE_OR_OWNER)
    public void addRow(Long orderId, Long applianceId, Long number) {
        if (number == null || number <= 0) {
            throw new InvalidQuantityException(number);
        }
        Orders order = getEditable(orderId);
        Appliance appliance = applianceRepository.findById(applianceId)
                .orElseThrow(() -> new ResourceNotFoundException("Appliance", applianceId));

        OrderRow row = new OrderRow();
        row.setAppliance(appliance);
        row.setNumber(number);
        row.setAmount(appliance.getPrice().multiply(BigDecimal.valueOf(number)));
        order.getOrderRowSet().add(row);
    }

    @Loggable
    @Override
    @PreAuthorize(EMPLOYEE_OR_OWNER)
    public void removeRow(Long orderId, Long rowId) {
        Orders order = getEditable(orderId);
        order.getOrderRowSet().removeIf(r -> r.getId().equals(rowId));
    }

    @Loggable
    @Override
    @PreAuthorize(EMPLOYEE_OR_OWNER)
    public void delete(Long orderId) {
        Orders order = find(orderId);
        if (Boolean.TRUE.equals(order.getApproved()) && !CurrentUser.isEmployee()) {
            throw new OrderLockedException(orderId);
        }
        ordersRepository.delete(order);
    }

    @Loggable
    @Override
    @Secured("ROLE_EMPLOYEE")
    public void approve(Long orderId) {
        Orders order = find(orderId);
        String email = CurrentUser.email();
        order.setEmployee(employeeRepository.findFirstByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", email)));
        order.setApproved(true);
    }

    @Loggable
    @Override
    @Secured("ROLE_EMPLOYEE")
    public void unapprove(Long orderId) {
        Orders order = find(orderId);
        order.setEmployee(null);
        order.setApproved(false);
    }

    private OrderDto createFor(Client client) {
        Orders order = new Orders();
        order.setClient(client);
        return orderMapper.toDto(ordersRepository.save(order));
    }

    private Orders find(Long id) {
        return ordersRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));
    }

    private Orders getEditable(Long id) {
        Orders order = find(id);
        if (Boolean.TRUE.equals(order.getApproved())) {
            throw new OrderLockedException(id);
        }
        return order;
    }
}
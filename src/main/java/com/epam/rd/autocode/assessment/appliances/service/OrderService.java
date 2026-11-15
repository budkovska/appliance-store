package com.epam.rd.autocode.assessment.appliances.service;

import com.epam.rd.autocode.assessment.appliances.dto.OrderDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    Page<OrderDto> findAllFor(String email, boolean employee, Pageable pageable);
    OrderDto findById(Long id, String email, boolean employee);
    OrderDto create(String email, boolean employee, Long clientId);
    void addRow(Long orderId, Long applianceId, Long number, String email, boolean employee);
    void removeRow(Long orderId, Long rowId, String email, boolean employee);
    void delete(Long orderId, String email, boolean employee);
    void approve(Long orderId, String employeeEmail);
    void unapprove(Long orderId);
}

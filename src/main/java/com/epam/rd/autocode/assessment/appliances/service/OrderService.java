package com.epam.rd.autocode.assessment.appliances.service;

import com.epam.rd.autocode.assessment.appliances.dto.OrderDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    Page<OrderDto> findAllForCurrentUser(String search, Boolean approved, Pageable pageable);
    OrderDto findById(Long id);
    OrderDto createForClient(Long clientId);
    OrderDto createForCurrentClient();
    void addRow(Long orderId, Long applianceId, Long number);
    void removeRow(Long orderId, Long rowId);
    void delete(Long orderId);
    void approve(Long orderId);
    void unapprove(Long orderId);
}
package com.epam.rd.autocode.assessment.appliances.mapper;

import com.epam.rd.autocode.assessment.appliances.dto.OrderDto;
import com.epam.rd.autocode.assessment.appliances.dto.OrderRowDto;
import com.epam.rd.autocode.assessment.appliances.model.OrderRow;
import com.epam.rd.autocode.assessment.appliances.model.Orders;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Component
public class OrderMapper {

    public OrderDto toDto(Orders order) {
        List<OrderRowDto> rows = order.getOrderRowSet().stream()
                .sorted(Comparator.comparing(OrderRow::getId, Comparator.nullsLast(Comparator.naturalOrder())))
                .map(this::toDto)
                .toList();

        BigDecimal total = rows.stream()
                .map(OrderRowDto::getAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        OrderDto dto = new OrderDto();
        dto.setId(order.getId());
        dto.setClientId(order.getClient().getId());
        dto.setClientName(order.getClient().getName());
        dto.setEmployeeName(order.getEmployee() != null ? order.getEmployee().getName() : null);
        dto.setApproved(Boolean.TRUE.equals(order.getApproved()));
        dto.setTotal(total);
        dto.setRows(rows);
        return dto;
    }

    public OrderRowDto toDto(OrderRow row) {
        OrderRowDto dto = new OrderRowDto();
        dto.setId(row.getId());
        dto.setApplianceId(row.getAppliance().getId());
        dto.setApplianceName(row.getAppliance().getName());
        dto.setNumber(row.getNumber());
        dto.setAmount(row.getAmount());
        return dto;
    }
}
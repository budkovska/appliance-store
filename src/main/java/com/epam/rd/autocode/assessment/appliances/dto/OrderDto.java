package com.epam.rd.autocode.assessment.appliances.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class OrderDto {

    private Long id;
    private Long clientId;
    private String clientName;
    private String employeeName;
    private boolean approved;
    private BigDecimal total;
    private List<OrderRowDto> rows = new ArrayList<>();
}
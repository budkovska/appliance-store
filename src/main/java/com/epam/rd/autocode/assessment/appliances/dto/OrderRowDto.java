package com.epam.rd.autocode.assessment.appliances.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class OrderRowDto {

    private Long id;
    private Long applianceId;
    private String applianceName;
    private Long number;
    private BigDecimal amount;
}
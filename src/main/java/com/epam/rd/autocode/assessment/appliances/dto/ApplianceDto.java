package com.epam.rd.autocode.assessment.appliances.dto;

import com.epam.rd.autocode.assessment.appliances.model.Category;
import com.epam.rd.autocode.assessment.appliances.model.PowerType;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class ApplianceDto {

    private Long id;

    @NotBlank
    @Size(max = 100)
    private String name;

    @NotNull
    private Category category;

    @NotBlank
    @Size(max = 100)
    private String model;

    @NotNull
    private Long manufacturerId;

    private String manufacturerName;

    @NotNull
    private PowerType powerType;

    @NotBlank
    @Size(max = 255)
    private String characteristic;

    @NotBlank
    @Size(max = 255)
    private String description;

    @NotNull
    @Positive
    private Integer power;

    @NotNull
    @Positive
    @Digits(integer = 10, fraction = 2)
    private BigDecimal price;
}
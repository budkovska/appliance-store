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

    @NotBlank(message = "{validation.required}")
    @Size(max = 100, message = "{validation.size.max}")
    private String name;

    @NotNull(message = "{validation.required}")
    private Category category;

    @NotBlank(message = "{validation.required}")
    @Size(max = 100, message = "{validation.size.max}")
    private String model;

    @NotNull(message = "{validation.required}")
    private Long manufacturerId;

    private String manufacturerName;

    @NotNull(message = "{validation.required}")
    private PowerType powerType;

    @NotBlank(message = "{validation.required}")
    @Size(max = 255, message = "{validation.size.max}")
    private String characteristic;

    @NotBlank(message = "{validation.required}")
    @Size(max = 255, message = "{validation.size.max}")
    private String description;

    @NotNull(message = "{validation.required}")
    @Positive(message = "{validation.positive}")
    private Integer power;

    @NotNull(message = "{validation.required}")
    @Positive(message = "{validation.positive}")
    @Digits(integer = 10, fraction = 2, message = "{validation.digits}")
    private BigDecimal price;
}
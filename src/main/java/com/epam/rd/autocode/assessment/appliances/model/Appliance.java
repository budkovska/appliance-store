package com.epam.rd.autocode.assessment.appliances.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Appliance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Category category;

    @NotBlank
    private String model;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "manufacturer_id")
    private Manufacturer manufacturer;

    @NotNull
    @Enumerated(EnumType.STRING)
    private PowerType powerType;

    @NotBlank
    private String characteristic;

    @NotBlank
    private String description;

    @NotNull
    @Positive
    private Integer power;

    @NotNull
    @Positive
    private BigDecimal price;
}

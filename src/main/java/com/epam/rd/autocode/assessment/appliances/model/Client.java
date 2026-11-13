package com.epam.rd.autocode.assessment.appliances.model;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Client extends User {

    @NotBlank
    private String card;
}

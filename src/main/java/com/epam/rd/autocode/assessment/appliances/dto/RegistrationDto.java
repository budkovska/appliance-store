package com.epam.rd.autocode.assessment.appliances.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
public class RegistrationDto extends ClientDto {

    @ToString.Exclude
    private String confirmPassword;
}
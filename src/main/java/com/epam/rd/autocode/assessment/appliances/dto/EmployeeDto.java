package com.epam.rd.autocode.assessment.appliances.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
public class EmployeeDto extends UserDto {

    @NotBlank(message = "{validation.required}")
    @Size(max = 100, message = "{validation.size.max}")
    private String department;
}
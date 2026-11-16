package com.epam.rd.autocode.assessment.appliances.dto;

import com.epam.rd.autocode.assessment.appliances.validation.StrongPassword;
import com.epam.rd.autocode.assessment.appliances.validation.UniqueEmail;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
@UniqueEmail
public abstract class UserDto {

    private Long id;

    @NotBlank(message = "{validation.required}")
    @Size(max = 100, message = "{validation.size.max}")
    private String name;

    @NotBlank(message = "{validation.required}")
    @Email(regexp = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$", message = "{validation.email.invalid}")
    @Size(max = 150, message = "{validation.size.max}")
    private String email;

    @ToString.Exclude
    @StrongPassword
    private String password;
}
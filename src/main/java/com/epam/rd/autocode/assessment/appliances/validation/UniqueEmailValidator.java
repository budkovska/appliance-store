package com.epam.rd.autocode.assessment.appliances.validation;

import com.epam.rd.autocode.assessment.appliances.dto.ClientDto;
import com.epam.rd.autocode.assessment.appliances.dto.EmployeeDto;
import com.epam.rd.autocode.assessment.appliances.dto.UserDto;
import com.epam.rd.autocode.assessment.appliances.repository.ClientRepository;
import com.epam.rd.autocode.assessment.appliances.repository.EmployeeRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, UserDto> {

    private final ClientRepository clients;
    private final EmployeeRepository employees;

    @Override
    public boolean isValid(UserDto dto, ConstraintValidatorContext context) {
        if (dto == null || !StringUtils.hasText(dto.getEmail())) {
            return true;
        }
        String email = dto.getEmail();

        Long ownClientId = dto instanceof ClientDto ? dto.getId() : null;
        Long ownEmployeeId = dto instanceof EmployeeDto ? dto.getId() : null;

        boolean takenByClient = ownClientId == null
                ? clients.existsByEmailIgnoreCase(email)
                : clients.existsByEmailIgnoreCaseAndIdNot(email, ownClientId);

        boolean takenByEmployee = ownEmployeeId == null
                ? employees.existsByEmailIgnoreCase(email)
                : employees.existsByEmailIgnoreCaseAndIdNot(email, ownEmployeeId);

        if (!takenByClient && !takenByEmployee) {
            return true;
        }

        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                .addPropertyNode("email")
                .addConstraintViolation();
        return false;
    }
}
package com.epam.rd.autocode.assessment.appliances.controller;

import com.epam.rd.autocode.assessment.appliances.dto.EmployeeDto;
import com.epam.rd.autocode.assessment.appliances.exception.DuplicateEmailException;
import com.epam.rd.autocode.assessment.appliances.exception.ResourceInUseException;
import com.epam.rd.autocode.assessment.appliances.exception.SelfDeletionException;
import com.epam.rd.autocode.assessment.appliances.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService service;

    @GetMapping
    public String list(@PageableDefault(size = 10, sort = "id") Pageable pageable, Model model) {
        Page<EmployeeDto> page = service.findAll(pageable);
        model.addAttribute("employees", page.getContent());
        model.addAttribute("page", page);
        return "employee/employees";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("employee", new EmployeeDto());
        return "employee/newEmployee";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("employee", service.findById(id));
        return "employee/newEmployee";
    }

    @PostMapping("/add-employee")
    public String save(@Valid @ModelAttribute("employee") EmployeeDto employee, BindingResult result) {
        if (employee.getId() == null && !StringUtils.hasText(employee.getPassword())) {
            result.rejectValue("password", "validation.password.required");
        }
        if (result.hasErrors()) {
            return "employee/newEmployee";
        }
        try {
            service.save(employee);
        } catch (DuplicateEmailException e) {
            result.rejectValue("email", "validation.email.unique");
            return "employee/newEmployee";
        }
        return "redirect:/employees";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        try {
            service.delete(id);
        } catch (SelfDeletionException e) {
            return "redirect:/employees?selfDeleteError";
        } catch (ResourceInUseException e) {
            return "redirect:/employees?deleteError";
        }
        return "redirect:/employees";
    }
}
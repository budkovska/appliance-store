package com.epam.rd.autocode.assessment.appliances.controller;

import com.epam.rd.autocode.assessment.appliances.model.Employee;
import com.epam.rd.autocode.assessment.appliances.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService service;

    @GetMapping
    public String list(@PageableDefault(size = 10, sort = "id") Pageable pageable, Model model) {
        Page<Employee> page = service.findAll(pageable);
        model.addAttribute("employees", page.getContent());
        model.addAttribute("page", page);
        return "employee/employees";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("employee", new Employee());
        return "employee/newEmployee";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("employee", service.findById(id));
        return "employee/newEmployee";
    }

    @PostMapping("/add-employee")
    public String save(@Valid @ModelAttribute("employee") Employee employee,
                       BindingResult result) {
        if (result.hasErrors()) {
            return "employee/newEmployee";
        }
        service.save(employee);
        return "redirect:/employees";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            service.delete(id);
        } catch (IllegalStateException e) {
            redirect.addFlashAttribute("selfDeleteError", true);
        } catch (DataIntegrityViolationException e) {
            redirect.addFlashAttribute("deleteError", true);
        }
        return "redirect:/employees";
    }
}
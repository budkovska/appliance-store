package com.epam.rd.autocode.assessment.appliances.controller;

import com.epam.rd.autocode.assessment.appliances.dto.ManufacturerDto;
import com.epam.rd.autocode.assessment.appliances.service.ManufacturerService;
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

@Controller
@RequestMapping("/manufacturers")
@RequiredArgsConstructor
public class ManufacturerController {

    private final ManufacturerService service;

    @GetMapping
    public String list(@PageableDefault(size = 10, sort = "id") Pageable pageable, Model model) {
        Page<ManufacturerDto> page = service.findAll(pageable);
        model.addAttribute("manufacturers", page.getContent());
        model.addAttribute("page", page);
        return "manufacture/manufacturers";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("manufacturer", new ManufacturerDto());
        return "manufacture/newManufacturer";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("manufacturer", service.findById(id));
        return "manufacture/newManufacturer";
    }

    @PostMapping("/add-manufacturer")
    public String save(@Valid @ModelAttribute("manufacturer") ManufacturerDto manufacturer,
                       BindingResult result) {
        if (result.hasErrors()) {
            return "manufacture/newManufacturer";
        }
        service.save(manufacturer);
        return "redirect:/manufacturers";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        try {
            service.delete(id);
        } catch (DataIntegrityViolationException e) {
            return "redirect:/manufacturers?deleteError";
        }
        return "redirect:/manufacturers";
    }
}
package com.epam.rd.autocode.assessment.appliances.controller;

import com.epam.rd.autocode.assessment.appliances.dto.ApplianceDto;
import com.epam.rd.autocode.assessment.appliances.dto.ApplianceListQuery;
import com.epam.rd.autocode.assessment.appliances.exception.ResourceInUseException;
import com.epam.rd.autocode.assessment.appliances.model.Category;
import com.epam.rd.autocode.assessment.appliances.model.PowerType;
import com.epam.rd.autocode.assessment.appliances.service.ApplianceService;
import com.epam.rd.autocode.assessment.appliances.service.ManufacturerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/appliances")
@RequiredArgsConstructor
public class ApplianceController {

    private final ApplianceService applianceService;
    private final ManufacturerService manufacturerService;

    private static final Map<String, String> SORTABLE = Map.of(
            "id", "id",
            "name", "name",
            "category", "category",
            "model", "model",
            "manufacturer", "manufacturer.name",
            "powerType", "powerType",
            "power", "power",
            "price", "price");

    @GetMapping
    public String list(@ModelAttribute("q") ApplianceListQuery q, Model model) {
        Pageable pageable = q.resolve(SORTABLE, "id");
        Page<ApplianceDto> page = applianceService.search(
                q.getSearch(), q.getCategory(), q.getPowerType(), q.getManufacturerId(), pageable);
        model.addAttribute("appliances", page.getContent());
        model.addAttribute("page", page);
        fillFormData(model);
        return "appliance/appliances";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("appliance", new ApplianceDto());
        fillFormData(model);
        return "appliance/newAppliance";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("appliance", applianceService.findById(id));
        fillFormData(model);
        return "appliance/newAppliance";
    }

    @PostMapping("/add-appliance")
    public String save(@Valid @ModelAttribute("appliance") ApplianceDto appliance,
                       BindingResult result, Model model) {
        if (result.hasErrors()) {
            fillFormData(model);
            return "appliance/newAppliance";
        }
        applianceService.save(appliance);
        return "redirect:/appliances";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        try {
            applianceService.delete(id);
        } catch (ResourceInUseException e) {
            return "redirect:/appliances?deleteError";
        }
        return "redirect:/appliances";
    }

    private void fillFormData(Model model) {
        model.addAttribute("manufacturers", manufacturerService.findAll());
        model.addAttribute("categories", Category.values());
        model.addAttribute("powerTypes", PowerType.values());
    }
}
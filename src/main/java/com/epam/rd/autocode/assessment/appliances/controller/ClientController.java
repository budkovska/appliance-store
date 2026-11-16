package com.epam.rd.autocode.assessment.appliances.controller;

import com.epam.rd.autocode.assessment.appliances.dto.ClientDto;
import com.epam.rd.autocode.assessment.appliances.exception.DuplicateEmailException;
import com.epam.rd.autocode.assessment.appliances.exception.ResourceInUseException;
import com.epam.rd.autocode.assessment.appliances.service.ClientService;
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
@RequestMapping("/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService service;

    @GetMapping
    public String list(@PageableDefault(size = 10, sort = "id") Pageable pageable, Model model) {
        Page<ClientDto> page = service.findAll(pageable);
        model.addAttribute("clients", page.getContent());
        model.addAttribute("page", page);
        return "client/clients";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("client", new ClientDto());
        return "client/newClient";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("client", service.findById(id));
        return "client/newClient";
    }

    @PostMapping("/add-client")
    public String save(@Valid @ModelAttribute("client") ClientDto client, BindingResult result) {
        if (client.getId() == null && !StringUtils.hasText(client.getPassword())) {
            result.rejectValue("password", "validation.password.required");
        }
        if (result.hasErrors()) {
            return "client/newClient";
        }
        try {
            service.save(client);
        } catch (DuplicateEmailException e) {
            result.rejectValue("email", "validation.email.unique");
            return "client/newClient";
        }
        return "redirect:/clients";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        try {
            service.delete(id);
        } catch (ResourceInUseException e) {
            return "redirect:/clients?deleteError";
        }
        return "redirect:/clients";
    }
}
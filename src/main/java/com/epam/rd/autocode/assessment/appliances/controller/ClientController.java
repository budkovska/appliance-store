package com.epam.rd.autocode.assessment.appliances.controller;

import com.epam.rd.autocode.assessment.appliances.model.Client;
import com.epam.rd.autocode.assessment.appliances.service.ClientService;
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
@RequestMapping("/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService service;

    @GetMapping
    public String list(@PageableDefault(size = 10, sort = "id") Pageable pageable, Model model) {
        Page<Client> page = service.findAll(pageable);
        model.addAttribute("clients", page.getContent());
        model.addAttribute("page", page);
        return "client/clients";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("client", new Client());
        return "client/newClient";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("client", service.findById(id));
        return "client/newClient";
    }

    @PostMapping("/add-client")
    public String save(@Valid @ModelAttribute("client") Client client,
                       BindingResult result) {
        if (result.hasErrors()) {
            return "client/newClient";
        }
        service.save(client);
        return "redirect:/clients";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            service.delete(id);
        } catch (DataIntegrityViolationException e) {
            redirect.addFlashAttribute("deleteError", true);
        }
        return "redirect:/clients";
    }
}
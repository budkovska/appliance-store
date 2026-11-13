package com.epam.rd.autocode.assessment.appliances.controller;

import com.epam.rd.autocode.assessment.appliances.model.OrderRow;
import com.epam.rd.autocode.assessment.appliances.model.Orders;
import com.epam.rd.autocode.assessment.appliances.service.ApplianceService;
import com.epam.rd.autocode.assessment.appliances.service.ClientService;
import com.epam.rd.autocode.assessment.appliances.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrdersController {

    private final OrderService orderService;
    private final ApplianceService applianceService;
    private final ClientService clientService;

    @GetMapping
    public String list(@PageableDefault(size = 10, sort = "id") Pageable pageable,
                       Authentication auth, Model model) {
        Page<Orders> page = orderService.findAllFor(auth.getName(), isEmployee(auth), pageable);
        model.addAttribute("orders", page.getContent());
        model.addAttribute("page", page);
        return "order/orders";
    }

    @GetMapping("/add")
    public String addForm(Authentication auth, Model model) {
        if (isEmployee(auth)) {
            model.addAttribute("clients", clientService.findAll());
        }
        return "order/newOrder";
    }

    @PostMapping("/add-order")
    public String create(@RequestParam(required = false) Long clientId, Authentication auth) {
        Orders order = orderService.create(auth.getName(), isEmployee(auth), clientId);
        return redirectToEdit(order.getId());
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Authentication auth, Model model) {
        Orders order = orderService.findById(id, auth.getName(), isEmployee(auth));
        List<OrderRow> rows = order.getOrderRowSet().stream()
                .sorted(Comparator.comparing(OrderRow::getId))
                .toList();
        model.addAttribute("order", order);
        model.addAttribute("rows", rows);
        return "order/editOrder";
    }

    @GetMapping("/{id}/choice-appliance")
    public String choiceAppliance(@PathVariable Long id, Authentication auth, Model model) {
        Orders order = orderService.findById(id, auth.getName(), isEmployee(auth));
        if (Boolean.TRUE.equals(order.getApproved())) {
            return redirectToEdit(id);
        }
        model.addAttribute("ordersId", id);
        model.addAttribute("appliances", applianceService.findAll());
        return "order/choiceAppliance";
    }

    @PostMapping("/{id}/add-into-order")
    public String addIntoOrder(@PathVariable Long id,
                               @RequestParam Long applianceId,
                               @RequestParam Long numbers,
                               Authentication auth,
                               RedirectAttributes redirect) {
        try {
            orderService.addRow(id, applianceId, numbers, auth.getName(), isEmployee(auth));
        } catch (IllegalArgumentException e) {
            redirect.addFlashAttribute("numberError", true);
        } catch (IllegalStateException e) {
            redirect.addFlashAttribute("lockedError", true);
        }
        return redirectToEdit(id);
    }

    @GetMapping("/{id}/rows/{rowId}/delete")
    public String removeRow(@PathVariable Long id, @PathVariable Long rowId,
                            Authentication auth, RedirectAttributes redirect) {
        try {
            orderService.removeRow(id, rowId, auth.getName(), isEmployee(auth));
        } catch (IllegalStateException e) {
            redirect.addFlashAttribute("lockedError", true);
        }
        return redirectToEdit(id);
    }

    @GetMapping("/{id}/delete")
    public String delete(@PathVariable Long id, Authentication auth) {
        orderService.delete(id, auth.getName(), isEmployee(auth));
        return "redirect:/orders";
    }

    @GetMapping("/{id}/approved")
    public String approve(@PathVariable Long id, Authentication auth) {
        orderService.approve(id, auth.getName());
        return "redirect:/orders";
    }

    @GetMapping("/{id}/unapproved")
    public String unapprove(@PathVariable Long id) {
        orderService.unapprove(id);
        return "redirect:/orders";
    }

    private String redirectToEdit(Long id) {
        return "redirect:/orders/" + id + "/edit";
    }

    private boolean isEmployee(Authentication auth) {
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_EMPLOYEE"));
    }
}
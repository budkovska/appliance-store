package com.epam.rd.autocode.assessment.appliances.controller;

import com.epam.rd.autocode.assessment.appliances.dto.OrderDto;
import com.epam.rd.autocode.assessment.appliances.dto.OrderListQuery;
import com.epam.rd.autocode.assessment.appliances.exception.InvalidQuantityException;
import com.epam.rd.autocode.assessment.appliances.exception.OrderLockedException;
import com.epam.rd.autocode.assessment.appliances.security.CurrentUser;
import com.epam.rd.autocode.assessment.appliances.service.ApplianceService;
import com.epam.rd.autocode.assessment.appliances.service.ClientService;
import com.epam.rd.autocode.assessment.appliances.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrdersController {

    private final OrderService orderService;
    private final ApplianceService applianceService;
    private final ClientService clientService;

    private static final Map<String, String> SORTABLE = Map.of(
            "id", "id",
            "client", "client.name",
            "employee", "employee.name",
            "approved", "approved");

    @GetMapping
    public String list(@ModelAttribute("q") OrderListQuery q, Model model) {
        Pageable pageable = q.resolve(SORTABLE, "id");
        Page<OrderDto> page = orderService.findAllForCurrentUser(q.getSearch(), q.approvedOrNull(), pageable);
        model.addAttribute("orders", page.getContent());
        model.addAttribute("page", page);
        return "order/orders";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        if (CurrentUser.isEmployee()) {
            model.addAttribute("clients", clientService.findAll());
        }
        return "order/newOrder";
    }

    @PostMapping("/add-order")
    public String create(@RequestParam(required = false) Long clientId) {
        OrderDto order = CurrentUser.isEmployee()
                ? orderService.createForClient(clientId)
                : orderService.createForCurrentClient();
        return redirectToEdit(order.getId());
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("order", orderService.findById(id));
        return "order/editOrder";
    }

    @GetMapping("/{id}/choice-appliance")
    public String choiceAppliance(@PathVariable Long id, Model model) {
        OrderDto order = orderService.findById(id);
        if (order.isApproved()) {
            return redirectToEdit(id);
        }
        model.addAttribute("ordersId", id);
        model.addAttribute("appliances", applianceService.findAll());
        return "order/choiceAppliance";
    }

    @PostMapping("/{id}/add-into-order")
    public String addIntoOrder(@PathVariable Long id,
                               @RequestParam Long applianceId,
                               @RequestParam Long numbers) {
        try {
            orderService.addRow(id, applianceId, numbers);
        } catch (InvalidQuantityException e) {
            return redirectToEdit(id) + "?numberError";
        } catch (OrderLockedException e) {
            return redirectToEdit(id) + "?lockedError";
        }
        return redirectToEdit(id);
    }

    @PostMapping("/{id}/rows/{rowId}/delete")
    public String removeRow(@PathVariable Long id, @PathVariable Long rowId) {
        try {
            orderService.removeRow(id, rowId);
        } catch (OrderLockedException e) {
            return redirectToEdit(id) + "?lockedError";
        }
        return redirectToEdit(id);
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        try {
            orderService.delete(id);
        } catch (OrderLockedException e) {
            return "redirect:/orders?lockedError";
        }
        return "redirect:/orders";
    }

    @PostMapping("/{id}/approved")
    public String approve(@PathVariable Long id) {
        orderService.approve(id);
        return "redirect:/orders";
    }

    @PostMapping("/{id}/unapproved")
    public String unapprove(@PathVariable Long id) {
        orderService.unapprove(id);
        return "redirect:/orders";
    }

    private String redirectToEdit(Long id) {
        return "redirect:/orders/" + id + "/edit";
    }
}
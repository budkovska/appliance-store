package com.epam.rd.autocode.assessment.appliances.security;

import com.epam.rd.autocode.assessment.appliances.repository.OrdersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("orderSecurity")
@RequiredArgsConstructor
public class OrderSecurity {

    private final OrdersRepository ordersRepository;

    public boolean isOwner(Long orderId, Authentication authentication) {
        if (orderId == null || authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        return ordersRepository.existsByIdAndClientEmailIgnoreCase(orderId, authentication.getName());
    }
}
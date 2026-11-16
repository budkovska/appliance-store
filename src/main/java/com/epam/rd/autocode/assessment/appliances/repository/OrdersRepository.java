package com.epam.rd.autocode.assessment.appliances.repository;

import com.epam.rd.autocode.assessment.appliances.model.Orders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdersRepository extends JpaRepository<Orders, Long> {
    Page<Orders> findByClientEmail(String email, Pageable pageable);
    boolean existsByIdAndClientEmailIgnoreCase(Long id, String email);
}

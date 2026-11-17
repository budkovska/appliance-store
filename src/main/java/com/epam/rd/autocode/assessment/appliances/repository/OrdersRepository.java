package com.epam.rd.autocode.assessment.appliances.repository;

import com.epam.rd.autocode.assessment.appliances.model.Orders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OrdersRepository
        extends JpaRepository<Orders, Long>, JpaSpecificationExecutor<Orders> {

    @Override
    @EntityGraph(attributePaths = {"client", "employee"})
    Page<Orders> findAll(Specification<Orders> spec, Pageable pageable);

    boolean existsByIdAndClientEmailIgnoreCase(Long id, String email);
}
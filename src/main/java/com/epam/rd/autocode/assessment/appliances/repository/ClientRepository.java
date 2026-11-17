package com.epam.rd.autocode.assessment.appliances.repository;

import com.epam.rd.autocode.assessment.appliances.model.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {
    Optional<Client> findFirstByEmail(String email);
    Optional<Client> findFirstByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);
    @Query("""
        select c from Client c
        where lower(c.name) like :pattern escape '!'
           or lower(c.email) like :pattern escape '!'
           or lower(c.card) like :pattern escape '!'
        """)
    Page<Client> search(@Param("pattern") String pattern, Pageable pageable);
}
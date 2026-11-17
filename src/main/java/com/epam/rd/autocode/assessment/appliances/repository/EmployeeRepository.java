package com.epam.rd.autocode.assessment.appliances.repository;

import com.epam.rd.autocode.assessment.appliances.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findFirstByEmail(String email);
    Optional<Employee> findFirstByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);
    @Query("""
        select e from Employee e
        where lower(e.name) like :pattern escape '!'
           or lower(e.email) like :pattern escape '!'
           or lower(e.department) like :pattern escape '!'
        """)
    Page<Employee> search(@Param("pattern") String pattern, Pageable pageable);
}

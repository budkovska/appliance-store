package com.epam.rd.autocode.assessment.appliances.repository.specification;

import com.epam.rd.autocode.assessment.appliances.model.Client;
import com.epam.rd.autocode.assessment.appliances.model.Employee;
import com.epam.rd.autocode.assessment.appliances.model.Orders;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class OrderSpecifications {

    private OrderSpecifications() {
    }

    public static Specification<Orders> filter(String clientEmail, String search, Boolean approved) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            Join<Orders, Client> client = root.join("client");

            if (clientEmail != null) {
                predicates.add(cb.equal(cb.lower(client.<String>get("email")),
                        clientEmail.toLowerCase(Locale.ROOT)));
            }
            if (approved != null) {
                Path<Boolean> path = root.get("approved");
                predicates.add(approved
                        ? cb.isTrue(path)
                        : cb.or(cb.isFalse(path), cb.isNull(path)));
            }
            if (StringUtils.hasText(search)) {
                String pattern = SearchUtils.likePattern(search);
                Join<Orders, Employee> employee = root.join("employee", JoinType.LEFT);

                List<Predicate> any = new ArrayList<>();
                any.add(cb.like(cb.lower(client.<String>get("name")), pattern, SearchUtils.ESCAPE));
                any.add(cb.like(cb.lower(employee.<String>get("name")), pattern, SearchUtils.ESCAPE));

                String trimmed = search.trim();
                if (trimmed.matches("\\d{1,18}")) {
                    any.add(cb.equal(root.get("id"), Long.parseLong(trimmed)));
                }
                predicates.add(cb.or(any.toArray(new Predicate[0])));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
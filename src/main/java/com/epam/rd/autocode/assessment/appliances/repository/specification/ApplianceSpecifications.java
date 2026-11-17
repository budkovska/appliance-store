package com.epam.rd.autocode.assessment.appliances.repository.specification;

import com.epam.rd.autocode.assessment.appliances.model.Appliance;
import com.epam.rd.autocode.assessment.appliances.model.Category;
import com.epam.rd.autocode.assessment.appliances.model.Manufacturer;
import com.epam.rd.autocode.assessment.appliances.model.PowerType;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public final class ApplianceSpecifications {

    private ApplianceSpecifications() {
    }

    public static Specification<Appliance> filter(String search, Category category,
                                                  PowerType powerType, Long manufacturerId) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(search)) {
                String pattern = SearchUtils.likePattern(search);
                Join<Appliance, Manufacturer> manufacturer = root.join("manufacturer");
                predicates.add(cb.or(
                        cb.like(cb.lower(root.<String>get("name")), pattern, SearchUtils.ESCAPE),
                        cb.like(cb.lower(root.<String>get("model")), pattern, SearchUtils.ESCAPE),
                        cb.like(cb.lower(manufacturer.<String>get("name")), pattern, SearchUtils.ESCAPE)));
            }
            if (category != null) {
                predicates.add(cb.equal(root.get("category"), category));
            }
            if (powerType != null) {
                predicates.add(cb.equal(root.get("powerType"), powerType));
            }
            if (manufacturerId != null) {
                predicates.add(cb.equal(root.get("manufacturer").get("id"), manufacturerId));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
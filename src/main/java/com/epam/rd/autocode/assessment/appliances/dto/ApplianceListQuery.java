package com.epam.rd.autocode.assessment.appliances.dto;

import com.epam.rd.autocode.assessment.appliances.model.Category;
import com.epam.rd.autocode.assessment.appliances.model.PowerType;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class ApplianceListQuery extends ListQuery {

    private Category category;
    private PowerType powerType;
    private Long manufacturerId;

    @Override
    protected void addFilterParams(Map<String, Object> params) {
        if (category != null) {
            params.put("category", category.name());
        }
        if (powerType != null) {
            params.put("powerType", powerType.name());
        }
        if (manufacturerId != null) {
            params.put("manufacturerId", manufacturerId);
        }
    }
}
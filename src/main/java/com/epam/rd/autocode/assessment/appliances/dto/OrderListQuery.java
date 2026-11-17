package com.epam.rd.autocode.assessment.appliances.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class OrderListQuery extends ListQuery {

    private String status = "";

    public OrderListQuery() {
        setDir("desc");
    }

    public Boolean approvedOrNull() {
        return switch (status == null ? "" : status) {
            case "approved" -> Boolean.TRUE;
            case "pending" -> Boolean.FALSE;
            default -> null;
        };
    }

    @Override
    protected void addFilterParams(Map<String, Object> params) {
        if ("approved".equals(status) || "pending".equals(status)) {
            params.put("status", status);
        }
    }
}
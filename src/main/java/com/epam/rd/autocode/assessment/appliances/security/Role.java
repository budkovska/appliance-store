package com.epam.rd.autocode.assessment.appliances.security;

public enum Role {
    EMPLOYEE,
    CLIENT;

    public String authority() {
        return "ROLE_" + name();
    }
}
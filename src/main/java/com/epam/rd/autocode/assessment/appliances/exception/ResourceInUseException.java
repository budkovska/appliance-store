package com.epam.rd.autocode.assessment.appliances.exception;

public class ResourceInUseException extends RuntimeException {
    public ResourceInUseException(String resource, Object id) {
        super(resource + " is in use and cannot be deleted: " + id);
    }
}
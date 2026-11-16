package com.epam.rd.autocode.assessment.appliances.exception;

public class InvalidQuantityException extends RuntimeException {
    public InvalidQuantityException(Long number) {
        super("Quantity must be positive: " + number);
    }
}
package com.epam.rd.autocode.assessment.appliances.exception;

public class OrderLockedException extends RuntimeException {
    public OrderLockedException(Long orderId) {
        super("Order is approved and cannot be changed: " + orderId);
    }
}
package com.epam.rd.autocode.assessment.appliances.exception;

public class SelfDeletionException extends RuntimeException {
    public SelfDeletionException() {
        super("Employee cannot delete own account");
    }
}
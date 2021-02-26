package com.libertex.demo.exception;

public class OperationIsForbiddenException extends RuntimeException {
    public OperationIsForbiddenException(String message) {
        super(message);
    }
}

package com.libertex.demo.exception;

import lombok.Getter;

@Getter
public class ValidationException extends Exception {

    public ValidationException(String message) {
        super(message);
    }
}

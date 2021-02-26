package com.libertex.demo.exception;

import lombok.Getter;

@Getter
public class RecordNotFoundException extends RuntimeException {

    private Object id;

    public RecordNotFoundException(Class<?> clazz, Object id) {
        super(String.format("Can't find entity %s with id = %s in db", clazz.getName(), id));
        this.id = id;
    }

    public RecordNotFoundException(String message) {
        super(message);
    }
}

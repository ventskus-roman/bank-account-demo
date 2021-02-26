package com.libertex.demo.controller;

import com.libertex.demo.dto.ErrorDto;
import com.libertex.demo.enums.ErrorCode;
import com.libertex.demo.exception.OperationIsForbiddenException;
import com.libertex.demo.exception.RecordNotFoundException;
import com.libertex.demo.exception.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

@ControllerAdvice(annotations = {RestController.class})
@Component
@Slf4j
public class BaseControllerAdvice {

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<?> handleThrowable(final Throwable ex) {
        log.error("Unexpected exception in controller", ex);
        var dto = ErrorDto.builder()
                .code(ErrorCode.UNKNOWN_ERROR)
                .message(ex.getMessage())
                .build();
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(dto);
    }

    @ExceptionHandler(RecordNotFoundException.class)
    public ResponseEntity<?> handleNotFoundExceptions(RecordNotFoundException exception) {
        var dto = ErrorDto.builder()
                .code(ErrorCode.RECORD_NOT_FOUND)
                .message(exception.getMessage())
                .build();
        return new ResponseEntity<>(dto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(OperationIsForbiddenException.class)
    public ResponseEntity<?> handleOperationForbiddenExceptions(OperationIsForbiddenException exception) {
        var dto = ErrorDto.builder()
                .code(ErrorCode.BALANCE_ERROR)
                .message(exception.getMessage())
                .build();
        return new ResponseEntity<>(dto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<?> handleValidationExceptions(ValidationException exception) {
        var dto = ErrorDto.builder()
                .code(ErrorCode.VALIDATION_ERROR)
                .message(exception.getMessage())
                .build();
        return new ResponseEntity<>(dto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentExceptionException(final IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .build();
    }
}

package com.dhanush.rentify_backend.exception;

import com.dhanush.rentify_backend.utils.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalException {

    private static final Logger log = LoggerFactory.getLogger(GlobalException.class);

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Void>> handleUserAlreadyExists(
            UserAlreadyExistsException ex) {

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiResponse<>(
                        HttpStatus.CONFLICT.value(),
                        ex.getMessage(),
                        null
                ));
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidCredentials(
            InvalidCredentialsException ex) {

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse<>(
                        HttpStatus.UNAUTHORIZED.value(),
                        ex.getMessage(),
                        null
                ));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFound(
            ResourceNotFoundException ex) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(
                        HttpStatus.NOT_FOUND.value(),
                        ex.getMessage(),
                        null
                ));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiResponse<Void>> handleResponseStatus(
            ResponseStatusException ex) {

        log.warn("ResponseStatusException: {}", ex.getReason(), ex);
        return ResponseEntity.status(ex.getStatusCode())
                .body(new ApiResponse<>(
                        ex.getStatusCode().value(),
                        ex.getReason(),
                        null
                ));
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationException(
            BindException ex) {

        Map<String, String> errors = new LinkedHashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.putIfAbsent(error.getField(), error.getDefaultMessage());
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(
                        HttpStatus.BAD_REQUEST.value(),
                        "Validation failed",
                        errors
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(
            Exception ex) {

        log.error("Unhandled exception", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "Something went wrong",
                        null
                ));
    }
}

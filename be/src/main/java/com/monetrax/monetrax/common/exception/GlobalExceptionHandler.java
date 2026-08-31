package com.monetrax.monetrax.common.exception;

import com.monetrax.monetrax.user.exception.EmailAlreadyExistsException;
import com.monetrax.monetrax.user.exception.NoSuchUserExistsException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // handler for when fetching user info if user doesnt exist this is thrown
    @ExceptionHandler(value = NoSuchUserExistsException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNoSuchUserExistsException(NoSuchUserExistsException err){
        return new ErrorResponse(HttpStatus.NOT_FOUND.value(), err.getMessage());
    }

    // exception for when we are creating user in the db, but email already exists
    @ExceptionHandler(value = EmailAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleEmailAlreadyExistsException(EmailAlreadyExistsException err){
        return new ErrorResponse(HttpStatus.NOT_FOUND.value(), err.getMessage());
    }

    // validator for dto's exception
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationError(MethodArgumentNotValidException ex) {
        Map<String, Object> errors = new LinkedHashMap<>();
        errors.put("status", HttpStatus.BAD_REQUEST.value());
        errors.put("errors", ex.getBindingResult().getFieldErrors()
                .stream()
                .map(err -> Map.of("field", err.getField(), "message", Objects.requireNonNullElse(err.getDefaultMessage(), "Validation failure!")))
                .toList());
        return ResponseEntity.badRequest().body(errors);
    }

    // validator for dto's exception
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, Object> errors = new LinkedHashMap<>();
        errors.put("status", HttpStatus.BAD_REQUEST.value());
        errors.put("errors", ex.getConstraintViolations()
                .stream()
                .map(v -> Map.of("field", v.getPropertyPath().toString(), "message", v.getMessage()))
                .toList());
        return ResponseEntity.badRequest().body(errors);
    }






}

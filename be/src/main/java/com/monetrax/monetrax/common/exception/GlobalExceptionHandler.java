package com.monetrax.monetrax.common.exception;

import com.monetrax.monetrax.accounts.exceptions.NoAccountDataToUpdate;
import com.monetrax.monetrax.accounts.exceptions.NoSuchAccountFound;
import com.monetrax.monetrax.auth.exceptions.JwtAuthenticationException;
import com.monetrax.monetrax.categories.exceptions.MissingFieldsForCategoryUpdate;
import com.monetrax.monetrax.categories.exceptions.NoSuchCategoryExistsException;
import com.monetrax.monetrax.user.exception.EmailAlreadyExistsException;
import com.monetrax.monetrax.user.exception.NoFieldToUpdateUserExistsException;
import com.monetrax.monetrax.user.exception.NoSuchUserExistsException;
import com.monetrax.monetrax.user.exception.PasswordMismatchException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    public static List<Map<String,Object>> addCustomErrorToErrorResponse(String err, String clueInfo){
        Map<String,Object> error = new HashMap<>();
        error.put("clue",clueInfo);
        error.put("message", err);
        List<Map<String,Object>> listToInsert=new ArrayList<>();
        listToInsert.add(error);
        return listToInsert;
    }

    // handler for when fetching user info if user doesn't exist this is thrown
    @ExceptionHandler(value = NoSuchUserExistsException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNoSuchUserExistsException(NoSuchUserExistsException err){
        return new ErrorResponse(HttpStatus.NOT_FOUND.value(), addCustomErrorToErrorResponse(err.getMessage(), "param:user_id"));
    }

    // exception for when we want to update user but we didn't provide any field to update
    @ExceptionHandler(value = NoFieldToUpdateUserExistsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleNoFieldToUpdateUserExistsException(NoFieldToUpdateUserExistsException err){
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), addCustomErrorToErrorResponse(err.getMessage(), "insertFieldInRequest"));
    }

    // exception for when we want to update user but we didn't provide any field to update
     @ExceptionHandler(value = PasswordMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handlePasswordMismatchException(PasswordMismatchException err){
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), addCustomErrorToErrorResponse(err.getMessage(), "incorrectPassword"));
    }

    // exception for when we are creating user in the db, but email already exists
    @ExceptionHandler(value = EmailAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleEmailAlreadyExistsException(EmailAlreadyExistsException err){
        return new ErrorResponse(HttpStatus.FORBIDDEN.value(), addCustomErrorToErrorResponse(err.getMessage(), "userEmail"));
    }

    // exception specific to categories api when we do not have any field to update and yet we call the endpoint
    @ExceptionHandler(value = MissingFieldsForCategoryUpdate.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMissingFieldsForCategoryUpdate(MissingFieldsForCategoryUpdate err){
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), addCustomErrorToErrorResponse(err.getMessage(), "insertFieldInRequest"));
    }

    // exception specific to categories api when either the user_id or category_id is wrong
    @ExceptionHandler(value = NoSuchCategoryExistsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleNoSuchCategoryExistsException(NoSuchCategoryExistsException err){
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), addCustomErrorToErrorResponse(err.getMessage(), "category_idORuser_id"));
    }

    // validator for dto's exception
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationError(MethodArgumentNotValidException ex) {
        List<Map<String, Object>> errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(err -> Map.<String, Object>of(
                        "clue", err.getField(),
                        "message", Objects.requireNonNullElse(err.getDefaultMessage(), "Validation failure!")))
                .toList();
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), errors);
    }

    // validator for dto's exception
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConstraintViolation(ConstraintViolationException ex) {
        List<Map<String, Object>> errors = ex.getConstraintViolations()
                .stream()
                .map(v -> Map.<String, Object>of(
                        "field", v.getPropertyPath().toString(),
                        "message", v.getMessage()))
                .toList();
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), errors);
    }


    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleTypeMismatchViolation(MethodArgumentTypeMismatchException ex){
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), addCustomErrorToErrorResponse(ex.getMessage(), "param:"+ex.getName()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleHttpMessageNotReadableException(HttpMessageNotReadableException ex){
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), addCustomErrorToErrorResponse(ex.getMessage(), "InvalidParamType"));
    }

    @ExceptionHandler(JwtAuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleJwtAuthenticationException(JwtAuthenticationException ex){
        return new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), addCustomErrorToErrorResponse(ex.getMessage(), "UnauthorizedOrInvalidBearer"));
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleAuthenticationException(AuthenticationException ex){
        return new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), addCustomErrorToErrorResponse(ex.getMessage(), "UnauthorizedOrInvalidBearer"));
    }


    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleBadCredentialsException(BadCredentialsException ex) {
        return new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                addCustomErrorToErrorResponse(
                        ex.getMessage(),
                        "BadCredentials"
                )
        );
    }

    @ExceptionHandler(DisabledException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleDisabledException(DisabledException ex) {
        return new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                addCustomErrorToErrorResponse(
                        ex.getMessage(),
                        "UnverifiedEmail"
                )
        );
    }

    @ExceptionHandler(LockedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleLockedException(LockedException ex) {
        return new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                addCustomErrorToErrorResponse(
                        ex.getMessage(),
                        "AccountLocked"
                )
        );
    }

    @ExceptionHandler(CredentialsExpiredException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleCredentialsExpiredException(CredentialsExpiredException ex) {
        return new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                addCustomErrorToErrorResponse(
                        ex.getMessage(),
                        "CredentialsExpired"
                )
        );
    }

    @ExceptionHandler(InsufficientAuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleInsufficientAuthenticationException(
            InsufficientAuthenticationException ex) {

        return new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                addCustomErrorToErrorResponse(
                        ex.getMessage(),
                        "InsufficientAuthentication"
                )
        );
    }


    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleAccessDeniedException(AccessDeniedException ex){
        return new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), addCustomErrorToErrorResponse(ex.getMessage(), "BadAuth"));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNoResourceFoundException(NoResourceFoundException ex){
        return new ErrorResponse(HttpStatus.NOT_FOUND.value(), addCustomErrorToErrorResponse(ex.getMessage(), "MissingRoute"));
    }

    @ExceptionHandler(NoAccountDataToUpdate.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNoAccountDataToUpdate(NoAccountDataToUpdate ex){
        return new ErrorResponse(HttpStatus.NOT_FOUND.value(), addCustomErrorToErrorResponse(ex.getMessage(), "MissingFields"));
    }

    @ExceptionHandler(NoSuchAccountFound.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNoSuchAccountFound(NoSuchAccountFound ex){
        return new ErrorResponse(HttpStatus.NOT_FOUND.value(), addCustomErrorToErrorResponse(ex.getMessage(), "InvalidAccountID"));
    }
}

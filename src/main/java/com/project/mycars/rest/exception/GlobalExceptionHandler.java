package com.project.mycars.rest.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrors> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(FieldError::getDefaultMessage)
                .orElse(messageSource.getMessage("fields.missing", null, Locale.getDefault()));

        ApiErrors apiErrors = new ApiErrors(message, HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(apiErrors, HttpStatus.BAD_REQUEST);
    }

    private String getErrorMessage(String validationCode) {
        if ("NotBlank".equals(validationCode) || "NotNull".equals(validationCode)) {
            return messageSource.getMessage("fields.missing", null, Locale.getDefault());
        } else {
            return messageSource.getMessage("fields.invalid", null, Locale.getDefault());
        }
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiErrors> handleResponseStatusException(ResponseStatusException ex) {
        return new ResponseEntity<>(new ApiErrors(ex.getReason(), ex.getStatusCode().value()), ex.getStatusCode());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrors> handleGlobalExceptions(Exception ex) {
        return new ResponseEntity<>(new ApiErrors(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Captura erro de formato do JSON
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleInvalidJsonFormat(HttpMessageNotReadableException ex) {
        if (ex.getCause() instanceof InvalidFormatException) {
            String message = messageSource.getMessage("fields.invalid", null, Locale.getDefault());
            ApiErrors apiErrors = new ApiErrors(message, HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(apiErrors, HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.badRequest().body(Map.of("message", "Invalid request format."));
    }


}

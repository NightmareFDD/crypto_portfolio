package com.engeto.crypto_portfolio.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CryptoNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(CryptoNotFoundException ex, HttpServletRequest request) {
        log.warn("Not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ApiError(
                        HttpStatus.NOT_FOUND.value(),
                        ex.getMessage(),
                        "CryptoNotFoundException",
                        request.getRequestURI(),
                        LocalDateTime.now()
                )
        );
    }

    @ExceptionHandler(DuplicateCryptoException.class)
    public ResponseEntity<ApiError> handleDuplicate(DuplicateCryptoException ex, HttpServletRequest request) {
        log.warn("Duplicate entry: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ApiError(
                        HttpStatus.BAD_REQUEST.value(),
                        ex.getMessage(),
                        "DuplicateCryptoException",
                        request.getRequestURI(),
                        LocalDateTime.now()
                )
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<String> messages = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .toList();

        String combinedMessage = String.join("; ", messages);
        log.warn("Validation error: {}", combinedMessage);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ApiError(
                        HttpStatus.BAD_REQUEST.value(),
                        combinedMessage,
                        "ValidationException",
                        request.getRequestURI(),
                        LocalDateTime.now()
                )
        );
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleOtherExceptions(Exception ex, HttpServletRequest request) {
        log.error("Unexpected error", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ApiError(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "An unexpected error occurred: " + ex.getMessage(),
                        "Exception",
                        request.getRequestURI(),
                        LocalDateTime.now()
                )
        );
    }
}

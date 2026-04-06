package com.datn.datn_be.exception;

import com.datn.datn_be.dto.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;
import java.util.Map;

/**
 * Global Exception Handler for the application
 * Handles all exceptions thrown by controllers and returns appropriate API responses
 */
@RestControllerAdvice
public class GeneralExceptionAdvisor {

    private static final Logger logger = LoggerFactory.getLogger(GeneralExceptionAdvisor.class);

    /**
     * Handle ClientSideException
     * Thrown when client makes invalid request
     */
    @ExceptionHandler(ClientSideException.class)
    public ResponseEntity<ApiResponse<?>> handleClientSideException(
            ClientSideException ex,
            WebRequest request) {

        logger.warn("ClientSideException: {} - {}", ex.getCode(), ex.getMessage());

        HttpStatus status = HttpStatus.valueOf(ex.getCode());

        ApiResponse<?> response = new ApiResponse<>(
                ex.getCode(),
                ex.getMessage(),
                ex.getData()
        );

        return new ResponseEntity<>(response, status);
    }

    /**
     * Handle validation errors from @Valid annotation
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationException(
            MethodArgumentNotValidException ex,
            WebRequest request) {

        logger.warn("Validation error: {}", ex.getMessage());

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ApiResponse<?> response = new ApiResponse<>(
                400,
                "Validation failed",
                errors
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle 404 Not Found exception
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleNotFoundException(
            NoHandlerFoundException ex,
            WebRequest request) {

        logger.warn("Resource not found: {}", ex.getRequestURL());

        ApiResponse<?> response = new ApiResponse<>(
                404,
                "Resource not found: " + ex.getRequestURL(),
                null
        );

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    /**
     * Handle IllegalArgumentException
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<?>> handleIllegalArgumentException(
            IllegalArgumentException ex,
            WebRequest request) {

        logger.warn("IllegalArgumentException: {}", ex.getMessage());

        ApiResponse<?> response = new ApiResponse<>(
                400,
                "Invalid argument: " + ex.getMessage(),
                null
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle IllegalStateException
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<?>> handleIllegalStateException(
            IllegalStateException ex,
            WebRequest request) {

        logger.warn("IllegalStateException: {}", ex.getMessage());

        ApiResponse<?> response = new ApiResponse<>(
                400,
                "Invalid state: " + ex.getMessage(),
                null
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle NullPointerException
     */
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ApiResponse<?>> handleNullPointerException(
            NullPointerException ex,
            WebRequest request) {

        logger.error("NullPointerException occurred", ex);

        ApiResponse<?> response = new ApiResponse<>(
                500,
                "Internal server error: Null pointer exception",
                null
        );

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handle generic Exception
     * This is the fallback handler for any exception not handled above
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleGlobalException(
            Exception ex,
            WebRequest request) {

        logger.error("Unhandled exception occurred", ex);

        ApiResponse<?> response = new ApiResponse<>(
                500,
                "Internal server error: " + ex.getMessage(),
                null
        );

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handle NumberFormatException
     */
    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<ApiResponse<?>> handleNumberFormatException(
            NumberFormatException ex,
            WebRequest request) {

        logger.warn("NumberFormatException: {}", ex.getMessage());

        ApiResponse<?> response = new ApiResponse<>(
                400,
                "Invalid number format: " + ex.getMessage(),
                null
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle ClassCastException
     */
    @ExceptionHandler(ClassCastException.class)
    public ResponseEntity<ApiResponse<?>> handleClassCastException(
            ClassCastException ex,
            WebRequest request) {

        logger.error("ClassCastException occurred", ex);

        ApiResponse<?> response = new ApiResponse<>(
                500,
                "Internal server error: Type mismatch",
                null
        );

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handle ArrayIndexOutOfBoundsException
     */
    @ExceptionHandler(ArrayIndexOutOfBoundsException.class)
    public ResponseEntity<ApiResponse<?>> handleArrayIndexOutOfBoundsException(
            ArrayIndexOutOfBoundsException ex,
            WebRequest request) {

        logger.error("ArrayIndexOutOfBoundsException occurred", ex);

        ApiResponse<?> response = new ApiResponse<>(
                500,
                "Internal server error: Array index out of bounds",
                null
        );

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handle RuntimeException
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<?>> handleRuntimeException(
            RuntimeException ex,
            WebRequest request) {

        logger.error("RuntimeException occurred", ex);

        ApiResponse<?> response = new ApiResponse<>(
                500,
                "Internal server error: " + ex.getMessage(),
                null
        );

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}


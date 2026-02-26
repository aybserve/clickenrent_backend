package org.clickenrent.rentalservice.exception;

import io.sentry.Sentry;
import io.sentry.SentryLevel;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Global exception handler for all REST controllers.
 * Provides consistent error responses across the application.
 * Integrates with Sentry for error tracking and monitoring.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * Capture exception to Sentry with request context.
     */
    private void captureExceptionToSentry(Exception ex, WebRequest request, HttpStatus status) {
        try {
            Sentry.withScope(scope -> {
                scope.setTag("http_status", String.valueOf(status.value()));
                scope.setTag("request_path", request.getDescription(false).replace("uri=", ""));
                scope.setLevel(status.is5xxServerError() ? SentryLevel.ERROR : SentryLevel.WARNING);
                Sentry.captureException(ex);
            });
        } catch (Exception sentryEx) {
            log.warn("Failed to capture exception to Sentry: {}", sentryEx.getMessage());
        }
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request) {
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateResourceException(
            DuplicateResourceException ex, WebRequest request) {
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CONFLICT.value())
                .error(HttpStatus.CONFLICT.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(
            UnauthorizedException ex, WebRequest request) {
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(
            AccessDeniedException ex, WebRequest request) {
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.FORBIDDEN.value())
                .error(HttpStatus.FORBIDDEN.getReasonPhrase())
                .message("You don't have permission to access this resource")
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex, WebRequest request) {
        
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message("Validation failed")
                .path(request.getDescription(false).replace("uri=", ""))
                .details(errors)
                .build();
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(
            ConstraintViolationException ex, WebRequest request) {
        
        List<String> errors = ex.getConstraintViolations()
                .stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.toList());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message("Validation failed")
                .path(request.getDescription(false).replace("uri=", ""))
                .details(errors)
                .build();
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(
            DataIntegrityViolationException ex, WebRequest request) {
        
        String message = "Database constraint violation";
        if (ex.getMessage().contains("unique") || ex.getMessage().contains("duplicate")) {
            message = "Resource already exists with the provided unique field(s)";
        }
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CONFLICT.value())
                .error(HttpStatus.CONFLICT.getReasonPhrase())
                .message(message)
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(PhotoValidationException.class)
    public ResponseEntity<ErrorResponse> handlePhotoValidationException(
            PhotoValidationException ex, WebRequest request) {
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PhotoAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handlePhotoAlreadyExistsException(
            PhotoAlreadyExistsException ex, WebRequest request) {
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CONFLICT.value())
                .error(HttpStatus.CONFLICT.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(
            IllegalStateException ex, WebRequest request) {
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle SQL Grammar exceptions (e.g., missing columns, invalid SQL syntax).
     * Provides specific error messages for PostGIS-related issues.
     */
    @ExceptionHandler(org.hibernate.exception.SQLGrammarException.class)
    public ResponseEntity<ErrorResponse> handleSQLGrammarException(
            org.hibernate.exception.SQLGrammarException ex, WebRequest request) {
        
        String message = "Database query error";
        String details = null;
        
        // Check for PostGIS-related errors
        if (ex.getMessage().contains("geom") || ex.getMessage().contains("ST_")) {
            message = "Spatial database feature not configured properly. Please contact support.";
            details = "PostGIS geometry column or functions may be missing";
        } else if (ex.getMessage().contains("column") && ex.getMessage().contains("does not exist")) {
            message = "Database schema mismatch detected";
            details = "Required database columns are missing";
        }
        
        // Log the actual error for debugging
        log.error("SQL Grammar Exception: {}", ex.getMessage(), ex);
        captureExceptionToSentry(ex, request, HttpStatus.INTERNAL_SERVER_ERROR);
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Database Error")
                .message(message)
                .path(request.getDescription(false).replace("uri=", ""))
                .details(details != null ? List.of(details) : null)
                .build();
        
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handle invalid data access resource usage (e.g., missing tables or columns).
     * This catches PostgreSQL errors wrapped by Spring's exception translation.
     */
    @ExceptionHandler(org.springframework.dao.InvalidDataAccessResourceUsageException.class)
    public ResponseEntity<ErrorResponse> handleInvalidDataAccessResourceUsageException(
            org.springframework.dao.InvalidDataAccessResourceUsageException ex, WebRequest request) {
        
        String message = "Database resource error";
        String details = null;
        
        if (ex.getMessage() != null) {
            if (ex.getMessage().contains("column") && ex.getMessage().contains("does not exist")) {
                message = "Database schema is missing required columns. Please run database migrations.";
                details = "Required database column is missing";
            } else if (ex.getMessage().contains("relation") && ex.getMessage().contains("does not exist")) {
                message = "Database table is missing. Please verify database setup.";
                details = "Required database table is missing";
            } else if (ex.getMessage().contains("geom") || ex.getMessage().contains("ST_")) {
                message = "PostGIS spatial feature not configured properly. Please contact support.";
                details = "PostGIS geometry column or functions may be missing";
            } else if (ex.getMessage().contains("extension") || ex.getMessage().contains("postgis")) {
                message = "PostGIS extension is not installed or configured";
                details = "PostGIS extension required for spatial queries";
            }
        }
        
        log.error("Invalid Data Access Resource Usage: {}", ex.getMessage(), ex);
        captureExceptionToSentry(ex, request, HttpStatus.INTERNAL_SERVER_ERROR);
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Database Configuration Error")
                .message(message)
                .path(request.getDescription(false).replace("uri=", ""))
                .details(details != null ? List.of(details) : null)
                .build();
        
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex, WebRequest request) {
        
        // Capture to Sentry
        captureExceptionToSentry(ex, request, HttpStatus.INTERNAL_SERVER_ERROR);
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message("An unexpected error occurred")
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}







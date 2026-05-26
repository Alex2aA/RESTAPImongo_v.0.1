package com.config;

import com.mongodb.MongoCommandException;
import com.mongodb.MongoSecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MongoCommandException.class)
    public ResponseEntity<Map<String, Object>> handleMongoCommandException(MongoCommandException e) {
        log.error("MongoCommandException code={} msg={}", e.getCode(), e.getMessage());
        if (e.getCode() == 13 || e.getCode() == 18) {
            return forbidden("Insufficient MongoDB privileges for your role");
        }
        return error("Database error: " + e.getMessage());
    }

    @ExceptionHandler(MongoSecurityException.class)
    public ResponseEntity<Map<String, Object>> handleMongoSecurityException(MongoSecurityException e) {
        log.error("MongoSecurityException msg={}", e.getMessage());
        return forbidden("Insufficient MongoDB privileges for your role");
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Map<String, Object>> handleDataAccessException(DataAccessException e) {
        log.error("DataAccessException type={} msg={}", e.getClass().getSimpleName(), e.getMessage());
        Throwable cause = findMongoCause(e);
        if (cause instanceof MongoCommandException) {
            MongoCommandException mce = (MongoCommandException) cause;
            if (mce.getCode() == 13 || mce.getCode() == 18) {
                return forbidden("Insufficient MongoDB privileges for your role");
            }
        }
        if (cause instanceof MongoSecurityException) {
            return forbidden("Insufficient MongoDB privileges for your role");
        }
        return error("Database error: " + e.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(AccessDeniedException e) {
        log.error("AccessDeniedException msg={}", e.getMessage());
        return forbidden("Access denied");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(Exception e) {
        log.error("Unhandled exception: type={} msg={}", e.getClass().getName(), e.getMessage(), e);
        return error("Internal server error: " + e.getClass().getSimpleName() + " - " + e.getMessage());
    }

    private Throwable findMongoCause(Throwable e) {
        Throwable cause = e;
        while (cause != null) {
            if (cause instanceof MongoCommandException || cause instanceof MongoSecurityException) {
                return cause;
            }
            cause = cause.getCause();
        }
        return e;
    }

    private ResponseEntity<Map<String, Object>> forbidden(String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", 403);
        body.put("error", "Forbidden");
        body.put("message", message);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    private ResponseEntity<Map<String, Object>> error(String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", 500);
        body.put("error", "Internal Server Error");
        body.put("message", message);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

}

package org.example.islab.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandlerController {
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<String> authException(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body("Authentication failed at controller advice: " + ex.getLocalizedMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> notAnOwner(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body("Access control check failed: " + ex.getLocalizedMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> generalException(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Something went wrong on server side: " + ex.getLocalizedMessage());
    }
}

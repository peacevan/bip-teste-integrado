package com.example.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler para a API REST
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNoResourceFound(NoResourceFoundException ex) {
        Map<String, Object> error = createErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "Recurso não encontrado",
                ex.getMessage() 
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(UnsupportedOperationException.class)
    public ResponseEntity<Map<String, Object>> handleUnsupportedOperation(UnsupportedOperationException ex) {
        Map<String, Object> error = createErrorResponse(
                HttpStatus.NOT_IMPLEMENTED.value(),
                "Operação não implementada",
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        Map<String, Object> error = createErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Argumentos inválidos",
                ex.getMessage()
        );
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        Map<String, Object> error = createErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Erro interno do servidor",
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        Map<String, Object> error = createErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Erro inesperado",
                "Entre em contato com o suporte"
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    private Map<String, Object> createErrorResponse(int status, String error, String message) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", status);
        errorResponse.put("error", error);
        errorResponse.put("message", message);
        return errorResponse;
    }
}
package com.example.securityjwt.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Objects;

public class CustomExceptionHandler {
    @ExceptionHandler({ MethodArgumentNotValidException.class })
    public ResponseEntity<?> handleException(Exception e) {
        if(e instanceof MethodArgumentNotValidException){
            return requiredFieldMissing((MethodArgumentNotValidException) e);
        }
        return ResponseEntity.badRequest().build();
    }

    private ResponseEntity<?> requiredFieldMissing(MethodArgumentNotValidException e) {
        var field = Objects.requireNonNull(e.getFieldError()).getField();
        return ResponseEntity.badRequest().body("Propriedade obrigatória '" + field + "' ausente ou em branco no objeto enviado");
    }
}

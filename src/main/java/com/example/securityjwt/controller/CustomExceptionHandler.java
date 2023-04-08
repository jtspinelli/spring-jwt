package com.example.securityjwt.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Objects;

public class CustomExceptionHandler {
    @ExceptionHandler({ MethodArgumentNotValidException.class })
    public ResponseEntity<?> handleException(Exception e) {
        if(passwordAbsent(e)) return requiredFieldMissing("password");
        if(usernameAbsent(e)) return requiredFieldMissing("username");

        return ResponseEntity.badRequest().build();
    }

    private ResponseEntity<?> requiredFieldMissing(String field) {
        return ResponseEntity.badRequest().body("Propriedade obrigatória '" + field + "' ausente ou em branco no objeto en viado");
    }

    private boolean passwordAbsent(Exception e){
        return e instanceof MethodArgumentNotValidException
                && Objects.requireNonNull(((MethodArgumentNotValidException) e).getFieldError()).getField().equals("password");
    }

    private boolean usernameAbsent(Exception e){
        return e instanceof MethodArgumentNotValidException
                && Objects.requireNonNull(((MethodArgumentNotValidException) e).getFieldError()).getField().equals("username");
    }
}

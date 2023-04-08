package com.example.securityjwt.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
public class UsuarioDto {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    private Boolean ativo;
    @NotNull
    private Long perfilId;
}

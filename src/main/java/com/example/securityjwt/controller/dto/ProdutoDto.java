package com.example.securityjwt.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
public class ProdutoDto {
    @NotBlank
    private String nome;
    @NotBlank
    private String descricao;
    @NotNull
    private Double valor;
}

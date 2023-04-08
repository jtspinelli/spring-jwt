package com.example.securityjwt.controller;

import com.example.securityjwt.controller.dto.ProdutoDto;
import com.example.securityjwt.model.Produto;
import com.example.securityjwt.service.ProdutoService;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/produto")
public class ProdutoController extends CustomExceptionHandler {
    private final ProdutoService service;

    public ProdutoController(ProdutoService service) {
        this.service = service;
    }

    @GetMapping("/todos")
    public List<Produto> listarProdutos(){
        return service.listarProdutos();
    }

    @PostMapping
    public Produto salvarProduto(@RequestBody @Valid ProdutoDto produtoDto){
        var produto = new Produto();
        produto.setNome(produtoDto.getNome());
        produto.setDescricao(produtoDto.getDescricao());
        produto.setValor(produtoDto.getValor());

        return service.salvarProduto(produto);
    }
}

package com.example.securityjwt.controller;

import com.example.securityjwt.controller.dto.LoginDto;
import com.example.securityjwt.controller.dto.TokenDto;
import com.example.securityjwt.controller.dto.UsuarioDto;
import com.example.securityjwt.model.Usuario;
import com.example.securityjwt.repository.PerfilRepository;
import com.example.securityjwt.repository.UsuarioRepository;
import com.example.securityjwt.service.TokenService;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/auth")
public class AutenticadorController extends CustomExceptionHandler {
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UsuarioRepository repository;
    private final PerfilRepository perfilRepository;
    private final PasswordEncoder encoder;

    public AutenticadorController(AuthenticationManager authenticationManager, TokenService tokenService, UsuarioRepository repository, PerfilRepository perfilRepository, PasswordEncoder encoder) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.repository = repository;
        this.perfilRepository = perfilRepository;
        this.encoder = encoder;
    }

    @PostMapping
    public ResponseEntity<?> autenticar(@RequestBody @Valid LoginDto loginDto){
        var login = loginDto.converter();
        try {
            var authentication = authenticationManager.authenticate(login);
            var token = tokenService.gerarToken(authentication);
            return ResponseEntity.ok(new TokenDto(token, "Bearer"));
        } catch (AuthenticationException e) {
            return ResponseEntity.badRequest().body("Usuário e/ou senha incorretos");
        }
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<?> cadastrar(@RequestBody @Validated UsuarioDto usuarioDto) {
        var perfil = this.perfilRepository.findById(usuarioDto.getPerfilId()).orElse(null);
        if(usuarioDto.getPerfilId() == 1 || perfil == null)  return ResponseEntity.badRequest().build();

        var usuario = Usuario.builder()
                .username(usuarioDto.getUsername())
                .password(encoder.encode(usuarioDto.getPassword()))
                .ativo(true)
                .perfis(List.of(perfil))
                .build();

        try {
            repository.save(usuario);
        } catch (Exception e){
            if(e instanceof DataIntegrityViolationException) {
                var usernameUniqueViolation = ((ConstraintViolationException) e.getCause()).getConstraintName().contains("USUARIO(USERNAME NULLS FIRST)");
                if(usernameUniqueViolation) return ResponseEntity.badRequest().body("Nome de usuário não disponível");
            }

            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.created(URI.create("/auth/cadastrar")).build();
    }
}



















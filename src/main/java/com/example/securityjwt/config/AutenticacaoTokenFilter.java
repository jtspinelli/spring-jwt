package com.example.securityjwt.config;

import com.example.securityjwt.service.TokenService;
import com.example.securityjwt.service.UsuarioService;
import io.jsonwebtoken.Jwts;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AutenticacaoTokenFilter extends OncePerRequestFilter {
    private final TokenService tokenService;
    private final UsuarioService usuarioService;

    public AutenticacaoTokenFilter(TokenService tokenService, UsuarioService usuarioService){
        this.tokenService = tokenService;
        this.usuarioService = usuarioService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getRequestURI().startsWith("/auth");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var token = recuperarToken(request);
        var valido = tokenService.isTokenValid(token);
        if(valido) autenticarCliente(token);

        filterChain.doFilter(request, response);
    }

    private void autenticarCliente(String token) {
        var user = Jwts.parser().setSigningKey(this.tokenService.getSecret()).parseClaimsJws(token).getBody().getSubject();

        if (user != null) {
            SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                    user,
                    null,
                        this.usuarioService.getAuthoritiesFromUser(this.tokenService.getIdUsuario(token))
                    ));
        }
    }

    private String recuperarToken(HttpServletRequest request) {
        var token = request.getHeader("Authorization");
        if(token.isBlank() || !token.startsWith("Bearer")) return null;

        return token.substring(7);
    }
}

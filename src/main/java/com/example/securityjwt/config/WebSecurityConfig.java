package com.example.securityjwt.config;

import com.example.securityjwt.model.Perfil;
import com.example.securityjwt.model.Usuario;
import com.example.securityjwt.repository.PerfilRepository;
import com.example.securityjwt.repository.UsuarioRepository;
import com.example.securityjwt.service.AutenticacaoService;
import com.example.securityjwt.service.TokenService;
import com.example.securityjwt.service.UsuarioService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private final TokenService tokenService;
    private final UsuarioRepository usuarioRepository;
    private final PerfilRepository perfilRepository;
    private final AutenticacaoService autenticacaoService;
    private final UsuarioService usuarioService;

    public WebSecurityConfig(TokenService tokenService, UsuarioRepository usuarioRepository, PerfilRepository perfilRepository, AutenticacaoService autenticacaoService, UsuarioService usuarioService){
        this.tokenService = tokenService;
        this.usuarioRepository = usuarioRepository;
        this.perfilRepository = perfilRepository;
        this.autenticacaoService = autenticacaoService;
        this.usuarioService = usuarioService;
    }

    @Override
    @Bean
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder encoder() { return new BCryptPasswordEncoder(); }

    // alterando o serviço de autenticação para usar o banco de dados que criamos
    // usa o Usuario
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(autenticacaoService).passwordEncoder(encoder());
        var adminPerfil = new Perfil();
        var userPerfil = new Perfil();
        adminPerfil.setNome("ROLE_ADMIN");
        userPerfil.setNome("ROLE_USER");
        this.perfilRepository.save(adminPerfil);
        this.perfilRepository.save(userPerfil);

        var user = new Usuario();
        user.setAtivo(true);
        user.setName("admin");
        user.setUsername("admin");
        user.setPassword(encoder().encode("admin"));
        user.setPerfis(List.of(this.perfilRepository.findById(1L).get()));
        this.usuarioRepository.save(user);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/auth", "/auth/cadastrar").permitAll() // permite o acesso ao endpoint de autenticação e de cadastro de usuário
                .antMatchers(HttpMethod.GET, "/produto/todos").permitAll()
                .antMatchers("/hello/admin").hasAuthority("ROLE_ADMIN")
                .antMatchers(HttpMethod.POST, "/produto").hasAuthority("ROLE_ADMIN")
            .and()
            .authorizeRequests()
                .anyRequest().authenticated()
            .and().csrf().disable() // desabilita o csrf (necessário para o uso do token)
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // aceita apenas chamadas com o token
            .and()
            .addFilterBefore( // adicionar o filtro do token JWT que criamos
                    new AutenticacaoTokenFilter(tokenService, usuarioService),
                    UsernamePasswordAuthenticationFilter.class
            );
    }

    // remove a configuração padrão do WebSecurity
    @Override
    public void configure(WebSecurity web) throws Exception { }
}

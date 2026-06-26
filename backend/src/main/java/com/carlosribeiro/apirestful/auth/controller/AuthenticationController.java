package com.carlosribeiro.apirestful.auth.controller;

import com.carlosribeiro.apirestful.auth.model.Usuario;
import com.carlosribeiro.apirestful.auth.repository.UsuarioRepository;
import com.carlosribeiro.apirestful.auth.service.JwtService;
import com.carlosribeiro.apirestful.auth.util.TokenResponse;
import com.carlosribeiro.apirestful.auth.util.UsuarioLogin;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// @CrossOrigin(origins = "http://localhost:5173")
@AllArgsConstructor
@RestController
@RequestMapping("autenticacao")   // http://localhost:8080/autenticacao
public class AuthenticationController {

    // AuthenticationManager é uma interface responsável por cuidar da autenticação dos usuários.
    // Ela possui o método authenticate que deve ser chamado sempre que um usuário precisa ser
    // autenticado.

    // O Spring security vem com uma implementação desta interface e esta implementação
    // chama o método authenticate() de um AuthenticationProvider (no nosso caso,
    // DaoAuthenticationProvider - veja o método authenticationProvider() em SecurityConfig).
    // O método authenticate() do AuthenticationProvider é responsável por realizar a autenticação.
    // A implementação de AuthenticationProvider que estamos utilizando (DaoAuthenticationProvider),
    // precisa de dois objetos para autenticar um usuário: userDetailsService e passwordEncoder.

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;

    @PostMapping("login")  // http://localhost:8080/autenticacao/login
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody UsuarioLogin usuarioLogin,
                                               HttpServletResponse response) {

        // O método authenticate() de AuthenticationManager vai chamar o método authenticate() de um
        // AuthenticationProvider que irá chamar o método loadUserByUsename() de UsuarioService (que
        // implementa a interface UserDetailsService).

        // Abaixo estamos utilizando a classe UsernamePasswordAuthenticationToken para passar para
        // o método authenticate() do authentication manager a conta e a senha do usuário. No nosso
        // caso, estamos utilizando como conta o email dos usuários.

        // Para esta classe (UsernamePasswordAuthenticationToken) existe um outro construtor que recebe,
        // além do identificador do usuário e da senha, os perfis do usuário. Esse outro construtor
        // deve ser utilizado em usuários autenticados.

        // Esse método lança a exceção AuthenticationException caso o usuário não possa ser autenticado.
        // Nesse caso, retornará para o cliente o erro 401 - UNAUTHORIZED.

        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(usuarioLogin.getEmail(), usuarioLogin.getSenha()));

        // O método finByEmail() lança a exceção NoSuchElementException mas ela nunca acontecerá pois
        // já teria dado erro acima. Aqui a gente usa o email para recuperar o objeto usuario (completo)

        Usuario usuario = usuarioRepository.findByEmail(usuarioLogin.getEmail()).orElseThrow();

        // E na linha abaixo o objeto usuário é passado para o método que gera o token.
        // Ao passar o objeto usuario para o método generateAccessToken() isso vai permitir que ao ser
        // gerado o token o id do usuário, seu nome, e Role sejam passados para o token como Claims.
        // Assim, quando o usuário acessar o servidor novament (no próximo acesso) não precisaremos
        // acessar o banco de dados para recuperar estas informações.

        String accessToken = jwtService.generateAccessToken(usuario);

        // Note que o método de login retorna um ResponseEntity de TokenResponse que contém o token,
        // o id do usuário, seu nome e Role.

        return new ResponseEntity<>(new TokenResponse(
            accessToken, usuario.getId(), usuario.getNome(), usuario.getRole().name()), HttpStatus.OK);
    }
}

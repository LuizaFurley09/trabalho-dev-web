package com.carlosribeiro.apirestful.auth.controller;

import com.carlosribeiro.apirestful.auth.model.Usuario;
import com.carlosribeiro.apirestful.auth.repository.UsuarioRepository;
import com.carlosribeiro.apirestful.auth.service.JwtService;
import com.carlosribeiro.apirestful.auth.util.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes do AuthenticationController.
 *
 * AuthenticationManager, JwtService e UsuarioRepository são mockados via @MockitoBean,
 * já que o foco é testar apenas o comportamento do controller (e não a lógica real
 * de autenticação/geração de token).
 *
 * addFilters = false desabilita a cadeia de filtros de segurança (JwtAuthenticationFilter).
 */

@WebMvcTest(AuthenticationController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario("Carlos", "carlos@email.com", "senhaCriptografada", Role.ADMIN);
        usuario.setId(1L);
    }

    @Test
    void login_comCredenciaisValidas() throws Exception {
        //deve retornar token
        String json = """
            {
                "email": "carlos@email.com",
                "senha": "123456"
            }
            """;

        when(usuarioRepository.findByEmail("carlos@email.com"))
                .thenReturn(Optional.of(usuario));
        when(jwtService.generateAccessToken(usuario)).thenReturn("token-jwt-fake");

        mockMvc.perform(post("/autenticacao/login")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token-jwt-fake"))
                .andExpect(jsonPath("$.idUsuario").value(1))
                .andExpect(jsonPath("$.nome").value("Carlos"))
                .andExpect(jsonPath("$.role").value("ADMIN"));

        verify(authenticationManager).authenticate(any());
        verify(jwtService).generateAccessToken(usuario);
    }

    @Test
    void login_comCredenciaisInvalidas() throws Exception {
        // Deve propagar bad credentials exception

        /*
         O controller não captura BadCredentialsException de propósito: é o
         ExceptionTranslationFilter do Spring Security (configurado em SecurityConfig)
         quem converte essa exceção em HTTP 401 na aplicação real.

         Como este teste usa @AutoConfigureMockMvc(addFilters = false), os filtros de
         segurança não rodam, então a tradução para 401 não acontece aqui — e não deveria,
         pois essa responsabilidade não é do controller. Por isso validamos que a exceção
         é de fato propagada (não é engolida) e que o fluxo de geração de token não continua.
         */

        String json = """
            {
                "email": "carlos@email.com",
                "senha": "senhaErrada"
            }
            """;

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Credenciais inválidas"));

        Exception exception = assertThrows(Exception.class, () ->
                mockMvc.perform(post("/autenticacao/login")
                        .contentType("application/json")
                        .content(json)));

        // A causa raiz da ServletException deve ser a BadCredentialsException lançada
        assertInstanceOf(BadCredentialsException.class, exception.getCause());

        verify(jwtService, never()).generateAccessToken(any());
    }

    @Test
    void login_comEmailInvalido() throws Exception {
        // Deve retornar 400

        String json = """
            {
                "email": "email-invalido",
                "senha": "123456"
            }
            """;

        mockMvc.perform(post("/autenticacao/login")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isBadRequest());

        verify(authenticationManager, never()).authenticate(any());
    }

    @Test
    void login_comCamposEmBranco() throws Exception {
        // Deve retornar 400
        String json = """
            {
                "email": "",
                "senha": ""
            }
            """;

        mockMvc.perform(post("/autenticacao/login")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isBadRequest());

        verify(authenticationManager, never()).authenticate(any());
    }

    @Test
    void login_quandoUsuarioNaoEncontradoAposAutenticar() throws Exception {
        /*
           Caso teórico: autenticação passa, mas o usuário não é encontrado no
           findByEmail (orElseThrow lança NoSuchElementException).
           Hoje não há handler para essa exceção em GlobalExceptionHandler,
           então a requisição deve propagar o erro (500), evidenciando a lacuna.
        */

        String json = """
        {
            "email": "fantasma@email.com",
            "senha": "123456"
        }
        """;

        when(usuarioRepository.findByEmail("fantasma@email.com"))
                .thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () ->
                mockMvc.perform(post("/autenticacao/login")
                        .contentType("application/json")
                        .content(json)));

        assertInstanceOf(java.util.NoSuchElementException.class, exception.getCause());

        verify(jwtService, never()).generateAccessToken(any());
    }
}
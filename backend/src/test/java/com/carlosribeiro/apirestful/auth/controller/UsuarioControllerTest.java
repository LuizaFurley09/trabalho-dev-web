package com.carlosribeiro.apirestful.auth.controller;

import com.carlosribeiro.apirestful.auth.model.Usuario;
import com.carlosribeiro.apirestful.auth.service.JwtService;
import com.carlosribeiro.apirestful.auth.service.UsuarioService;
import com.carlosribeiro.apirestful.auth.util.InfoUsuario;
import com.carlosribeiro.apirestful.auth.util.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes do UsuarioController.
 *
 * addFilters = false desabilita a cadeia de filtros de segurança (JwtAuthenticationFilter),
 * mantendo o teste focado no comportamento do controller.
 */

@WebMvcTest(UsuarioController.class)
@AutoConfigureMockMvc(addFilters = false)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioService usuarioService;

    @MockitoBean
    JwtService jwtService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario("Carlos", "carlos@email.com", "senhaCriptografada", Role.USER);
        usuario.setId(1L);
    }

    @Test
    void recuperarUsuarios_deveRetornarListaDeUsuarios() throws Exception {
        when(usuarioService.recuperarUsuarios()).thenReturn(List.of(usuario));

        mockMvc.perform(get("/usuarios"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0].nome").value("Carlos"))
            .andExpect(jsonPath("$[0].email").value("carlos@email.com"));

        verify(usuarioService).recuperarUsuarios();
    }

    @Test
    void cadastrarUsuario_comDadosValidos_deveRetornarInfoUsuario() throws Exception {
        String json = """
            {
                "nome": "Carlos",
                "email": "carlos@email.com",
                "senha": "123456"
            }
            """;

        when(usuarioService.cadastrarUsuario(any()))
            .thenReturn(new InfoUsuario(true, false, "Usuário cadastrado com sucesso!"));

        mockMvc.perform(post("/usuarios")
                .contentType("application/json")
                .content(json))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.valido").value(true))
            .andExpect(jsonPath("$.duplicado").value(false))
            .andExpect(jsonPath("$.mensagem").value("Usuário cadastrado com sucesso!"));

        verify(usuarioService).cadastrarUsuario(any());
    }

    @Test
    void cadastrarUsuario_comEmailJaCadastrado_deveRetornarDuplicado() throws Exception {
        String json = """
            {
                "nome": "Carlos",
                "email": "carlos@email.com",
                "senha": "123456"
            }
            """;

        when(usuarioService.cadastrarUsuario(any()))
            .thenReturn(new InfoUsuario(false, true, "Email já cadastrado!"));

        mockMvc.perform(post("/usuarios")
                .contentType("application/json")
                .content(json))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.valido").value(false))
            .andExpect(jsonPath("$.duplicado").value(true));
    }

    @Test
    void cadastrarUsuario_comEmailInvalido_deveRetornar400() throws Exception {
        String json = """
            {
                "nome": "Carlos",
                "email": "email-invalido",
                "senha": "123456"
            }
            """;

        mockMvc.perform(post("/usuarios")
                .contentType("application/json")
                .content(json))
            .andExpect(status().isBadRequest());

        verify(usuarioService, never()).cadastrarUsuario(any());
    }

    @Test
    void cadastrarUsuario_comCamposObrigatoriosFaltando_deveRetornar400() throws Exception {
        String json = "{}";

        mockMvc.perform(post("/usuarios")
                .contentType("application/json")
                .content(json))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.map.nome").exists())
            .andExpect(jsonPath("$.map.email").exists())
            .andExpect(jsonPath("$.map.senha").exists());

        verify(usuarioService, never()).cadastrarUsuario(any());
    }
}

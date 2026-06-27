package com.carlosribeiro.apirestful.auth.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test") // É necessário um application-test.properties
public class UsuarioSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    // Requisição de Cadastrar Usuários: POST /usuarios sem autenticação
    // Resultado esperado: 200 Ok (deve permitir)
    @Test
    void devePermitirCadastrarUsuario() throws Exception {

        // Cenário com dados genéricos de um novo usuário
        String jsonUsuarioSemMapper = """
        {
            "nome": "Claudio",
            "email": "claudiops@email.com",
            "senha": "senha#Segura123"
        }
        """;

        mockMvc.perform(MockMvcRequestBuilders.post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonUsuarioSemMapper))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    // Requisição de Recuperar Usuários: GET /usuarios/** sem autenticação
    // Resultado esperado: 200 Ok (deve permitir)
    @Test
    void devePermitirRecuperarUsuarios() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/usuarios")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}

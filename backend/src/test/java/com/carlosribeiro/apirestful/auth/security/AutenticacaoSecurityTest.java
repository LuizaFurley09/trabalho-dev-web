package com.carlosribeiro.apirestful.auth.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AutenticacaoSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    // Requisição de Fazer Login: POST /autenticacao/login sem autenticação
    // Resultado esperado: 200 Ok (deve permitir)
    @Test
    void devePermitirFazerLogin() throws Exception {

        String jsonLoginSemMapper = """
        {
            "email": "admin@mail.com",
            "senha": "desweb"
        }
        """;

        mockMvc.perform(MockMvcRequestBuilders.post("/autenticacao/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonLoginSemMapper))
                .andExpect(MockMvcResultMatchers.status().isOk());
        // O status esperado depende se o usuário existe no banco de teste H2 (200 OK)
        // ou se vai dar erro de credenciais (401/400). O importante é NÃO dar 403 Forbidden da segurança.
    }
}
package com.carlosribeiro.apirestful.auth.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import java.time.LocalDate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test") // É necessário um application-test.properties
public class ProdutoSecurityTest {

    @Autowired
    private MockMvc mockMvc;


    // Requisição de Recuperar Produtos: GET /produtos/** sem autenticação
    // Resultado esperado: 200 Ok (deve permitir)
    @Test
    void devePermitirRecuperarProdutos() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/produtos")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    // Requisição de Cadastrar Produto: POST /produtos sem autenticação
    // Resultado esperado: 401 Unathorized (não deve permitir)
    @Test
    void deveImpedirCadastrarProdutoSemAutenticacao() throws Exception {

        String jsonProdutoSemMapper = """
        {
            "id": null,
            "imagem": "uva.png",
            "nome": "FrutaTeste",
            "descricao": "Descrição Teste",
            "disponivel": true,
            "qtdEstoque": 10,
            "preco": 1000.00,
            "dataCadastro": "%s",
            "categoria": {
                "id": 1,
                "nome": "FrutaFake"
            }
        }
        """.formatted(LocalDate.now().toString());

        mockMvc.perform(MockMvcRequestBuilders.post("/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonProdutoSemMapper))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    // Requisição de Cadastrar Produto: POST /produtos com autenticação
    // Resultado esperado: 200 Ok (deve permitir)
    @Test
    void devePermitirCadastrarProdutoParaUsuarioAutenticado() throws Exception {

        String jsonProdutoSemMapper = """
        {
            "id": null,
            "imagem": "uva.png",
            "nome": "FrutaTeste",
            "descricao": "Descrição Teste",
            "disponivel": true,
            "qtdEstoque": 10,
            "preco": 1000.00,
            "dataCadastro": "%s",
            "categoria": {
                "id": 1,
                "nome": "FrutaFake"
            }
        }
        """.formatted(LocalDate.now().toString());

        mockMvc.perform(MockMvcRequestBuilders.post("/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonProdutoSemMapper)
                        // Spring Security simula um usuário autenticado genérico
                        .with(user("user")))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    // Requisição de Alterar Produto: PUT /produtos/** sem autenticação
    // Resultado esperado: 401 Unathorized (não deve permitir)
    @Test
    void deveImpedirAlterarProdutoSemAutenticacao() throws Exception {

        String jsonProdutoSemMapper = """
        {
            "id": 1,
            "imagem": "uva.png",
            "nome": "FrutaTeste",
            "descricao": "Descrição Teste",
            "disponivel": true,
            "qtdEstoque": 10,
            "preco": 1000.00,
            "dataCadastro": "%s",
            "categoria": {
                "id": 1,
                "nome": "fruta"
            }
        }
        """.formatted(LocalDate.now().toString());

        mockMvc.perform(MockMvcRequestBuilders.put("/produtos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonProdutoSemMapper))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    // Requisição de Alterar Produto: PUT /produtos/** autenticado como USER
    // Resultado esperado: 403 Forbidden (deve proibir)
    @Test
    void deveBloquearAlterarProdutoParaPerfilUser() throws Exception {

        String jsonProdutoSemMapper = """
        {
            "id": 1,
            "imagem": "uva.png",
            "nome": "FrutaTeste",
            "descricao": "Descrição Teste",
            "disponivel": true,
            "qtdEstoque": 10,
            "preco": 1000.00,
            "dataCadastro": "%s",
            "categoria": {
                "id": 1,
                "nome": "fruta"
            }
        }
        """.formatted(LocalDate.now().toString());

        mockMvc.perform(MockMvcRequestBuilders.put("/produtos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonProdutoSemMapper)
                        // Spring Security simula um usuário autenticado como USER
                        .with(user("user").roles("USER")))
                        // .with(jwt().authorities(new SimpleGrantedAuthority("USER"))))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    // Requisição de Alterar Produto: PUT /produtos/** autenticado como ADMIN
    // Resultado esperado: 200 Ok (deve permitir)
    @Test
    void devePermitirAlterarProdutoParaPerfilAdmin() throws Exception {

        String jsonProdutoSemMapper = """
        {
            "id": 1,
            "imagem": "uva.png",
            "nome": "FrutaTeste",
            "descricao": "Descrição Teste",
            "disponivel": true,
            "qtdEstoque": 10,
            "preco": 1000.00,
            "dataCadastro": "%s",
            "categoria": {
                "id": 1,
                "nome": "fruta"
            }
        }
        """.formatted(LocalDate.now().toString());

        mockMvc.perform(MockMvcRequestBuilders.put("/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonProdutoSemMapper)
                        // Spring Security simula um usuário autenticado como ADMIN
                        .with(user("admin").roles("ADMIN")))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    // Requisição de Deletar Produto: DELETE /produtos/** sem autenticação
    // Resultado esperado: 401 Unathorized (não deve permitir)
    @Test
    void deveImpedirDeletarProdutoSemAutenticacao() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/produtos/1"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    // Requisição de Deletar Produto: DELETE /produtos/** autenticado como USER
    // Resultado esperado: 403 Forbidden (deve proibir)
    @Test
    void deveBloquearDeletarProdutoParaPerfilUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/produtos/1")
                        // Spring Security simula um usuário autenticado como USER
                        .with(user("user").roles("USER")))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    // Requisição de Deletar Produto: DELETE /produtos/** autenticado como ADMIN
    // Resultado esperado: 200 Ok (deve permitir)
    @Test
    @WithMockUser(roles = "ADMIN") // Spring Security simula um usuário autenticado como ADMIN
    void devePermitirDeletarProdutoParaPerfilAdmin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/produtos/1")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
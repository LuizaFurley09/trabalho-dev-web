package com.carlosribeiro.apirestful.controller;

import com.carlosribeiro.apirestful.auth.service.JwtService;
import com.carlosribeiro.apirestful.dto.CategoriaResumo;
import com.carlosribeiro.apirestful.dto.ProdutoResponse;
import com.carlosribeiro.apirestful.exception.EntidadeNaoEncontradaException;
import com.carlosribeiro.apirestful.service.ProdutoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes do ProdutoController.
 *
 * addFilters = false desabilita a cadeia de filtros de segurança para que os testes
 * fiquem focados apenas no comportamento do controller (roteamento, validação de
 * @RequestBody, serialização da resposta e tratamento de exceções via
 * GlobalExceptionHandler, que é carregado automaticamente pelo @WebMvcTest).
 */

@WebMvcTest(ProdutoController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProdutoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProdutoService produtoService;

    @MockitoBean
    private JwtService jwtService;

    private ProdutoResponse produtoResponse;

    @BeforeEach
    void setUp() {
        CategoriaResumo categoriaResumo = new CategoriaResumo(1L, "Informática");
        produtoResponse = new ProdutoResponse(
            1L,
            "imagem.png",
            "Notebook",
            "Notebook gamer",
            true,
            10,
            new BigDecimal("3500.00"),
            LocalDate.now(),
            categoriaResumo
        );
    }

    @Test
    void recuperarProdutos_deveRetornarListaDeProdutos() throws Exception {
        when(produtoService.recuperarProdutos()).thenReturn(List.of(produtoResponse));

        mockMvc.perform(get("/produtos"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0].nome").value("Notebook"))
            .andExpect(jsonPath("$[0].categoria.nome").value("Informática"));

        verify(produtoService).recuperarProdutos();
    }

    @Test
    void recuperarProdutoPorId_quandoExiste_deveRetornarProduto() throws Exception {
        when(produtoService.recuperarProdutoPorId(1L)).thenReturn(produtoResponse);

        mockMvc.perform(get("/produtos/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.nome").value("Notebook"));

        verify(produtoService).recuperarProdutoPorId(1L);
    }

    @Test
    void recuperarProdutoPorId_quandoNaoExiste_deveRetornar404() throws Exception {
        when(produtoService.recuperarProdutoPorId(99L))
            .thenThrow(new EntidadeNaoEncontradaException("Produto com id = 99 não encontrado."));

        mockMvc.perform(get("/produtos/99"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.errorCode").value(404))
            .andExpect(jsonPath("$.message").value("Produto com id = 99 não encontrado."));
    }

    @Test
    void cadastrarProduto_comDadosValidos_deveRetornar200ESalvar() throws Exception {
        String json = """
            {
                "imagem": "imagem.png",
                "nome": "Notebook",
                "descricao": "Notebook gamer",
                "disponivel": true,
                "qtdEstoque": 10,
                "preco": 3500.00,
                "dataCadastro": "2024-01-01",
                "categoria": { "id": 1, "nome": "Informática" }
            }
            """;

        when(produtoService.cadastrarProduto(any())).thenReturn(produtoResponse);

        mockMvc.perform(post("/produtos")
                .contentType("application/json")
                .content(json))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nome").value("Notebook"));

        verify(produtoService).cadastrarProduto(any());
    }

    @Test
    void cadastrarProduto_comIdInformado_deveRetornar400() throws Exception {
        // Id deve ser nulo no cadastro (grupo OnCreate) -> erro de validação
        String json = """
            {
                "id": 1,
                "imagem": "imagem.png",
                "nome": "Notebook",
                "descricao": "Notebook gamer",
                "disponivel": true,
                "qtdEstoque": 10,
                "preco": 3500.00,
                "dataCadastro": "2024-01-01",
                "categoria": { "id": 1, "nome": "Informática" }
            }
            """;

        mockMvc.perform(post("/produtos")
                .contentType("application/json")
                .content(json))
            .andExpect(status().isBadRequest());

        verify(produtoService, never()).cadastrarProduto(any());
    }

    @Test
    void cadastrarProduto_comCamposObrigatoriosFaltando_deveRetornar400ComMensagens() throws Exception {
        String json = """
            {
                "disponivel": true
            }
            """;

        mockMvc.perform(post("/produtos")
                .contentType("application/json")
                .content(json))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.map.nome").exists())
            .andExpect(jsonPath("$.map.imagem").exists());

        verify(produtoService, never()).cadastrarProduto(any());
    }

    @Test
    void alterarProduto_comDadosValidos_deveRetornar200() throws Exception {
        String json = """
            {
                "id": 1,
                "imagem": "imagem.png",
                "nome": "Notebook Atualizado",
                "descricao": "Notebook gamer atualizado",
                "disponivel": true,
                "qtdEstoque": 5,
                "preco": 4000.00,
                "dataCadastro": "2024-01-01",
                "categoria": { "id": 1, "nome": "Informática" }
            }
            """;

        when(produtoService.alterarProduto(any())).thenReturn(produtoResponse);

        mockMvc.perform(put("/produtos")
                .contentType("application/json")
                .content(json))
            .andExpect(status().isOk());

        verify(produtoService).alterarProduto(any());
    }

    @Test
    void alterarProduto_semId_deveRetornar400() throws Exception {
        // Id deve ser informado na alteração (grupo OnUpdate) -> erro de validação
        String json = """
            {
                "imagem": "imagem.png",
                "nome": "Notebook",
                "descricao": "Notebook gamer",
                "disponivel": true,
                "qtdEstoque": 10,
                "preco": 3500.00,
                "dataCadastro": "2024-01-01",
                "categoria": { "id": 1, "nome": "Informática" }
            }
            """;

        mockMvc.perform(put("/produtos")
                .contentType("application/json")
                .content(json))
            .andExpect(status().isBadRequest());

        verify(produtoService, never()).alterarProduto(any());
    }

    @Test
    void removerProdutoPorId_deveRetornar200EChamarServico() throws Exception {
        mockMvc.perform(delete("/produtos/1"))
            .andExpect(status().isOk());

        verify(produtoService).removerProdutoPorId(1L);
    }

    @Test
    void removerProdutoPorId_quandoNaoExiste_deveRetornar404() throws Exception {
        doThrow(new EntidadeNaoEncontradaException("Produto com id = 99 não encontrado."))
            .when(produtoService).removerProdutoPorId(99L);

        mockMvc.perform(delete("/produtos/99"))
            .andExpect(status().isNotFound());
    }

    @Test
    void recuperarProdutosComPaginacao_deveRetornarResultadoPaginado() throws Exception {
        Page<ProdutoResponse> page = new PageImpl<>(
            List.of(produtoResponse), PageRequest.of(0, 5), 1);

        when(produtoService.recuperarProdutosComPaginacao(any(), eq("")))
            .thenReturn(page);

        mockMvc.perform(get("/produtos/paginacao"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalDeItens").value(1))
            .andExpect(jsonPath("$.totalDePaginas").value(1))
            .andExpect(jsonPath("$.paginaCorrente").value(0))
            .andExpect(jsonPath("$.itens[0].nome").value("Notebook"));
    }

    @Test
    void recuperarProdutosComPaginacao_comParametros_deveUsarValoresInformados() throws Exception {
        Page<ProdutoResponse> page = new PageImpl<>(
            List.of(produtoResponse), PageRequest.of(1, 3), 4);

        when(produtoService.recuperarProdutosComPaginacao(any(), eq("Notebook")))
            .thenReturn(page);

        mockMvc.perform(get("/produtos/paginacao")
                .param("pagina", "1")
                .param("tamanho", "3")
                .param("nome", "Notebook"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.paginaCorrente").value(1));

        verify(produtoService).recuperarProdutosComPaginacao(any(), eq("Notebook"));
    }
}

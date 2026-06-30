package com.carlosribeiro.apirestful.controller;

import com.carlosribeiro.apirestful.auth.service.JwtService;
import com.carlosribeiro.apirestful.dto.CategoriaResumo;
import com.carlosribeiro.apirestful.dto.ProdutoResponse;
import com.carlosribeiro.apirestful.exception.EntidadeNaoEncontradaException;
import com.carlosribeiro.apirestful.service.ProdutoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
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
    void recuperarProdutos() throws Exception {
        //deve retornar lista de produtos
        when(produtoService.recuperarProdutos()).thenReturn(List.of(produtoResponse));

        mockMvc.perform(get("/produtos"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0].nome").value("Notebook"))
            .andExpect(jsonPath("$[0].categoria.nome").value("Informática"));

        verify(produtoService).recuperarProdutos();
    }

    @Test
    void recuperarProdutoPorId_quandoExiste() throws Exception {
        //deve retornar produto
        when(produtoService.recuperarProdutoPorId(1L)).thenReturn(produtoResponse);

        mockMvc.perform(get("/produtos/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.nome").value("Notebook"));

        verify(produtoService).recuperarProdutoPorId(1L);
    }

    @Test
    void recuperarProdutoPorId_quandoNaoExiste() throws Exception {
        //deve retornar 404
        when(produtoService.recuperarProdutoPorId(99L))
            .thenThrow(new EntidadeNaoEncontradaException("Produto com id = 99 não encontrado."));

        mockMvc.perform(get("/produtos/99"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.errorCode").value(404))
            .andExpect(jsonPath("$.message").value("Produto com id = 99 não encontrado."));
    }

    @Test
    void cadastrarProduto_comDadosValidos() throws Exception {
        //deve retornar 200 e salvar
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
    void cadastrarProduto_comIdInformado() throws Exception {
        // deve retornar 400
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
    void cadastrarProduto_comCamposObrigatoriosFaltando() throws Exception {
        // deve retornar 400 com mensagens
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
    void alterarProduto_comDadosValidos() throws Exception {
        //deve retornar 200
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
    void alterarProduto_semId() throws Exception {
        // deve retornar 400
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
    void removerProdutoPorId() throws Exception {
        // deve retornar 200 e chamar serviço
        mockMvc.perform(delete("/produtos/1"))
            .andExpect(status().isOk());

        verify(produtoService).removerProdutoPorId(1L);
    }

    @Test
    void removerProdutoPorId_quandoNaoExiste() throws Exception {
        // deve retornar 404
        doThrow(new EntidadeNaoEncontradaException("Produto com id = 99 não encontrado."))
            .when(produtoService).removerProdutoPorId(99L);

        mockMvc.perform(delete("/produtos/99"))
            .andExpect(status().isNotFound());
    }

    @Test
    void recuperarProdutosComPaginacao() throws Exception {
        // deve retornar resultado paginado
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
    void recuperarProdutosComPaginacao_comParametros() throws Exception {
        // deve usar valores informados
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
    @Test
    void cadastrarProduto_comPrecoInvalido() throws Exception {
        // Preço abaixo de 0.1 deve gerar 400 com mensagem específica
        String json = """
        {
            "imagem": "imagem.png",
            "nome": "Notebook",
            "descricao": "Notebook gamer",
            "disponivel": true,
            "qtdEstoque": 10,
            "preco": 0.05,
            "dataCadastro": "2024-01-01",
            "categoria": { "id": 1, "nome": "Informática" }
        }
        """;

        mockMvc.perform(post("/produtos")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.map.preco").value("O 'Preço' deve ser maior ou igual a 0.1."));

        verify(produtoService, never()).cadastrarProduto(any());
    }

    @Test
    void cadastrarProduto_comQtdEstoqueNegativa() throws Exception {
        // Quantidade negativa deve gerar 400 com mensagem específica
        String json = """
        {
            "imagem": "imagem.png",
            "nome": "Notebook",
            "descricao": "Notebook gamer",
            "disponivel": true,
            "qtdEstoque": -1,
            "preco": 3500.00,
            "dataCadastro": "2024-01-01",
            "categoria": { "id": 1, "nome": "Informática" }
        }
        """;

        mockMvc.perform(post("/produtos")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.map.qtdEstoque").value("A 'Quantidade em estoque' deve ser maior ou igual a 0."));

        verify(produtoService, never()).cadastrarProduto(any());
    }

    @Test
    void cadastrarProduto_semCategoria() throws Exception {
        // Categoria nula deve gerar 400
        String json = """
        {
            "imagem": "imagem.png",
            "nome": "Notebook",
            "descricao": "Notebook gamer",
            "disponivel": true,
            "qtdEstoque": 10,
            "preco": 3500.00,
            "dataCadastro": "2024-01-01"
        }
        """;

        mockMvc.perform(post("/produtos")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.map.categoriaResumo").value("A 'Categoria' deve ser informada."));

        verify(produtoService, never()).cadastrarProduto(any());
    }

    @Test
    void cadastrarProduto_comJsonMalformado() throws Exception {
        // JSON com sintaxe inválida não deve cair em MethodArgumentNotValidException
        // e não deve chamar o service
        String jsonInvalido = """
        {
            "nome": "Notebook",
            "preco": 3500.00,
        """; // JSON incompleto/quebrado de propósito

        mockMvc.perform(post("/produtos")
                        .contentType("application/json")
                        .content(jsonInvalido))
                .andExpect(status().isBadRequest());

        verify(produtoService, never()).cadastrarProduto(any());
    }

    @Test
    void cadastrarProduto_comContentTypeInvalido() throws Exception {
        // Content-Type não suportado deve retornar 415
        String json = """
        {
            "nome": "Notebook"
        }
        """;

        mockMvc.perform(post("/produtos")
                        .contentType("text/plain")
                        .content(json))
                .andExpect(status().isUnsupportedMediaType());

        verify(produtoService, never()).cadastrarProduto(any());
    }

    @Test
    void recuperarProdutoPorId_comIdEmFormatoInvalido() throws Exception {
        // Id não numérico no path deve retornar 400, não 500
        mockMvc.perform(get("/produtos/abc"))
                .andExpect(status().isBadRequest());

        verify(produtoService, never()).recuperarProdutoPorId(anyLong());
    }

    @Test
    void recuperarProdutosComPaginacao_comPaginaForaDoIntervalo() throws Exception {
        // Página inexistente deve retornar lista vazia, não erro
        Page<ProdutoResponse> paginaVazia = new PageImpl<>(
                List.of(), PageRequest.of(99, 5), 1);

        when(produtoService.recuperarProdutosComPaginacao(any(), eq("")))
                .thenReturn(paginaVazia);

        mockMvc.perform(get("/produtos/paginacao").param("pagina", "99"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itens").isArray())
                .andExpect(jsonPath("$.itens").isEmpty());
    }
}

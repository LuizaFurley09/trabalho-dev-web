package com.carlosribeiro.apirestful.controller;

import com.carlosribeiro.apirestful.auth.service.JwtService;
import com.carlosribeiro.apirestful.dto.CategoriaDto;
import com.carlosribeiro.apirestful.dto.ProdutoResponse;
import com.carlosribeiro.apirestful.model.Categoria;
import com.carlosribeiro.apirestful.model.Produto;
import com.carlosribeiro.apirestful.service.CategoriaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes do CategoriaController.
 *
 * @WebMvcTest carrega apenas a camada web (controllers, conversores, exception handlers),
 * sem subir o contexto completo da aplicação.
 *
 * addFilters = false desabilita os filtros de segurança (ex: JwtAuthenticationFilter) para
 * que o teste foque exclusivamente no comportamento do controller, sem se preocupar com
 * autenticação/autorização (isso deve ser testado separadamente, se necessário).
 */

@WebMvcTest(CategoriaController.class)
@AutoConfigureMockMvc(addFilters = false)
class CategoriaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoriaService categoriaService;

    @MockitoBean
    private JwtService jwtService;

    private Categoria categoria;
    private Produto produto;

    @BeforeEach
    void setUp() {
        categoria = new Categoria("Informática");
        produto = new Produto(
            "imagem.png",
            "Notebook",
            "Notebook gamer",
            true,
            10,
            new BigDecimal("3500.00"),
            LocalDate.now(),
            categoria
        );
        categoria.getProdutos().add(produto);
    }

    @Test
    void recuperarCategoriasComProdutosV1_deveRetornarListaDeCategorias() throws Exception {
        when(categoriaService.recuperarCategoriasComProdutosV1())
                .thenReturn(List.of(categoria));

        mockMvc.perform(get("/categorias/v1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].nome").value("Informática"));

        verify(categoriaService).recuperarCategoriasComProdutosV1();
    }

    @Test
    void recuperarCategoriasComProdutosV1_quandoNaoHaCategorias_deveRetornarListaVazia() throws Exception {
        when(categoriaService.recuperarCategoriasComProdutosV1())
            .thenReturn(List.of());

        mockMvc.perform(get("/categorias/v1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void recuperarCategoriasComProdutosV2_deveRetornarListaDeCategoriaDto() throws Exception {
        ProdutoResponse produtoResponse = new ProdutoResponse(
            1L, "imagem.png", "Notebook", "Notebook gamer", true, 10,
            new BigDecimal("3500.00"), LocalDate.now(), null
        );
        CategoriaDto categoriaDto = new CategoriaDto(1L, "Informática", List.of(produtoResponse));

        when(categoriaService.recuperarCategoriasComProdutosV2())
            .thenReturn(List.of(categoriaDto));

        mockMvc.perform(get("/categorias/v2"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].nome").value("Informática"))
            .andExpect(jsonPath("$[0].produtos[0].nome").value("Notebook"));

        verify(categoriaService).recuperarCategoriasComProdutosV2();
    }
}

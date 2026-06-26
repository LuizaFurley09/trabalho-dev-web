package com.carlosribeiro.apirestful.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.carlosribeiro.apirestful.dto.CategoriaDto;
import com.carlosribeiro.apirestful.dto.CategoriaResumo;
import com.carlosribeiro.apirestful.dto.ProdutoResponse;
import com.carlosribeiro.apirestful.mapper.CategoriaMapper;
import com.carlosribeiro.apirestful.model.Categoria;
import com.carlosribeiro.apirestful.model.Produto;
import com.carlosribeiro.apirestful.repository.CategoriaRepository;

// mvnw.cmd test para rodar os testes unitários

@ExtendWith(MockitoExtension.class)
class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private CategoriaMapper categoriaMapper;

    @InjectMocks
    private CategoriaService categoriaService;

    // função auxiliar para criar um Produto com os campos mínimos

    private Produto criarProduto(Long id, String nome, BigDecimal preco, Categoria categoria) {
        Produto p = new Produto(
            nome.toLowerCase() + ".png",
            nome,
            "Descrição do " + nome,
            true,
            100,
            preco,
            LocalDate.now(),
            categoria
        );
        p.setId(id);
        return p;
    }

    // função auxiliar para criar um ProdutoResponse correspondente

    private ProdutoResponse criarProdutoResponse(Long id, String nome, BigDecimal preco, CategoriaResumo catResumo) {
        return new ProdutoResponse(
            id,
            nome.toLowerCase() + ".png",
            nome,
            "Descrição do " + nome,
            true,
            100,
            preco,
            LocalDate.now(),
            catResumo
        );
    }

    // recuperar categorias com produtos (retorno das entidades)

    @Test
    void recuperarCategoriasComProdutosV1_deveRetornarListaDeCategorias() {
        Categoria fruta = new Categoria("fruta");
        fruta.setId(1L);
        Categoria legume = new Categoria("legume");
        legume.setId(2L);

        Produto abacate = criarProduto(1L, "Abacate", BigDecimal.valueOf(2.45), fruta);
        Produto abobrinha = criarProduto(2L, "Abobrinha", BigDecimal.valueOf(1.10), legume);
        fruta.setProdutos(Arrays.asList(abacate));
        legume.setProdutos(Arrays.asList(abobrinha));

        List<Categoria> categorias = Arrays.asList(fruta, legume);
        when(categoriaRepository.recuperarCategoriasComProdutos()).thenReturn(categorias);

        List<Categoria> resultado = categoriaService.recuperarCategoriasComProdutosV1();

        assertEquals(2, resultado.size());
        assertEquals("fruta", resultado.get(0).getNome());
        assertEquals(1, resultado.get(0).getProdutos().size());
        verify(categoriaRepository).recuperarCategoriasComProdutos();
    }

    // recuperar categorias com produtos (retorno dos DTOs)

    @Test
    void recuperarCategoriasComProdutosV2_deveRetornarListaDeCategoriaDto() {
        Categoria fruta = new Categoria("fruta");
        fruta.setId(1L);
        Categoria legume = new Categoria("legume");
        legume.setId(2L);

        Produto abacate = criarProduto(1L, "Abacate", BigDecimal.valueOf(2.45), fruta);
        Produto abobrinha = criarProduto(2L, "Abobrinha", BigDecimal.valueOf(1.10), legume);
        fruta.setProdutos(Arrays.asList(abacate));
        legume.setProdutos(Arrays.asList(abobrinha));

        List<Categoria> categorias = Arrays.asList(fruta, legume);

        CategoriaResumo catResumo1 = new CategoriaResumo(1L, "fruta");
        CategoriaResumo catResumo2 = new CategoriaResumo(2L, "legume");
        ProdutoResponse prodResponse1 = criarProdutoResponse(1L, "Abacate", BigDecimal.valueOf(2.45), catResumo1);
        ProdutoResponse prodResponse2 = criarProdutoResponse(2L, "Abobrinha", BigDecimal.valueOf(1.10), catResumo2);

        CategoriaDto dto1 = new CategoriaDto(1L, "fruta", Arrays.asList(prodResponse1));
        CategoriaDto dto2 = new CategoriaDto(2L, "legume", Arrays.asList(prodResponse2));
        List<CategoriaDto> dtosEsperados = Arrays.asList(dto1, dto2);

        when(categoriaRepository.recuperarCategoriasComProdutos()).thenReturn(categorias);
        when(categoriaMapper.toCategoriasDto(categorias)).thenReturn(dtosEsperados);

        List<CategoriaDto> resultado = categoriaService.recuperarCategoriasComProdutosV2();

        assertEquals(2, resultado.size());
        assertEquals("fruta", resultado.get(0).nome());
        assertEquals(1, resultado.get(0).produtosDto().size());
        assertEquals("Abacate", resultado.get(0).produtosDto().get(0).nome());

        verify(categoriaRepository).recuperarCategoriasComProdutos();
        verify(categoriaMapper).toCategoriasDto(categorias);
    }
}
package com.carlosribeiro.apirestful.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.anyLong;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.carlosribeiro.apirestful.dto.CategoriaResumo;
import com.carlosribeiro.apirestful.dto.ProdutoRequest;
import com.carlosribeiro.apirestful.dto.ProdutoResponse;
import com.carlosribeiro.apirestful.exception.EntidadeNaoEncontradaException;
import com.carlosribeiro.apirestful.mapper.ProdutoMapper;
import com.carlosribeiro.apirestful.model.Categoria;
import com.carlosribeiro.apirestful.model.Produto;
import com.carlosribeiro.apirestful.repository.ProdutoRepository;

// mvnw.cmd test para rodar os testes unitários

@ExtendWith(MockitoExtension.class)
class ProdutoServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private ProdutoMapper produtoMapper;

    @InjectMocks
    private ProdutoService produtoService;

    // função auxiliar para criar entidade Produto (usada nos mocks do repositório)

    private Produto criarProduto(Long id, String nome, BigDecimal preco) {
        Produto p = new Produto(
            nome.toLowerCase() + ".png",
            nome,
            "Descrição do " + nome,
            true,
            100,
            preco,
            LocalDate.now(),
            new Categoria("Teste")
        );
        p.setId(id);
        return p;
    }

    // função auxiliar para criar ProdutoResponse com todos os campos

    private ProdutoResponse criarResponse(Long id, String nome, BigDecimal preco) {
        return new ProdutoResponse(
            id,
            nome.toLowerCase() + ".png",
            nome,
            "Descrição do " + nome,
            true,
            100,
            preco,
            LocalDate.now(),
            new CategoriaResumo(1L, "Teste")
        );
    }

    // função auxiliar para criar ProdutoRequest com todos os campos

    private ProdutoRequest criarRequest(Long id, String nome, BigDecimal preco) {
        return new ProdutoRequest(
            id,
            nome.toLowerCase() + ".png",
            nome,
            "Descrição do " + nome,
            true,
            100,
            preco,
            LocalDate.now(),
            new CategoriaResumo(1L, "Teste")
        );
    }

    // recuperar todos os produtos com DTOs

    @Test
    void recuperarProdutos_deveRetornarListaDeProdutoResponse() {
        Produto abacate = criarProduto(1L, "Abacate", BigDecimal.valueOf(2.45));
        Produto agriao = criarProduto(5L, "Agrião", BigDecimal.valueOf(2.50));
        List<Produto> produtos = Arrays.asList(abacate, agriao);

        ProdutoResponse response1 = criarResponse(1L, "Abacate", BigDecimal.valueOf(2.45));
        ProdutoResponse response2 = criarResponse(5L, "Agrião", BigDecimal.valueOf(2.50));

        when(produtoRepository.recuperarProduos()).thenReturn(produtos);
        when(produtoMapper.toProdutosResponse(produtos)).thenReturn(Arrays.asList(response1, response2));

        List<ProdutoResponse> resultado = produtoService.recuperarProdutos();

        assertEquals(2, resultado.size());
        assertEquals("Abacate", resultado.get(0).nome());
        verify(produtoRepository).recuperarProduos();
        verify(produtoMapper).toProdutosResponse(produtos);
    }

    // retornar o produto por ID quando ele existe

    @Test
    void recuperarProdutoPorId_quandoExiste_deveRetornarProdutoResponse() {
        Produto abobora = criarProduto(3L, "Abóbora", BigDecimal.valueOf(4.70));
        ProdutoResponse response = criarResponse(3L, "Abóbora", BigDecimal.valueOf(4.70));

        when(produtoRepository.findById(3L)).thenReturn(Optional.of(abobora));
        when(produtoMapper.toProdutoResponse(abobora)).thenReturn(response);

        ProdutoResponse resultado = produtoService.recuperarProdutoPorId(3L);

        assertEquals("Abóbora", resultado.nome());
        verify(produtoRepository).findById(3L);
    }

    // lançar exceção quando o produto não existe

    @Test
    void recuperarProdutoPorId_quandoNaoExiste_deveLancarExcecao() {
        when(produtoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntidadeNaoEncontradaException.class,
            () -> produtoService.recuperarProdutoPorId(99L));
    }

    // cadastrar produto a partir do request

    @Test
    void cadastrarProduto_deveRetornarProdutoResponse() {
        ProdutoRequest request = criarRequest(null, "Banana", BigDecimal.valueOf(3.50));
        Produto produto = criarProduto(null, "Banana", BigDecimal.valueOf(3.50));
        Produto produtoSalvo = criarProduto(6L, "Banana", BigDecimal.valueOf(3.50));
        ProdutoResponse response = criarResponse(6L, "Banana", BigDecimal.valueOf(3.50));

        when(produtoMapper.toProduto(request)).thenReturn(produto);
        when(produtoRepository.save(produto)).thenReturn(produtoSalvo);
        when(produtoMapper.toProdutoResponse(produtoSalvo)).thenReturn(response);

        ProdutoResponse resultado = produtoService.cadastrarProduto(request);

        assertEquals(6L, resultado.id());
        assertEquals("Banana", resultado.nome());
    }

    // alterar produto existente

    @Test
    void alterarProduto_deveRetornarProdutoResponse() {
        ProdutoRequest request = criarRequest(1L, "Abacate Orgânico", BigDecimal.valueOf(3.00));
        Produto produto = criarProduto(1L, "Abacate Orgânico", BigDecimal.valueOf(3.00));
        Produto produtoSalvo = criarProduto(1L, "Abacate Orgânico", BigDecimal.valueOf(3.00));
        ProdutoResponse response = criarResponse(1L, "Abacate Orgânico", BigDecimal.valueOf(3.00));

        when(produtoMapper.toProduto(request)).thenReturn(produto);
        when(produtoRepository.save(produto)).thenReturn(produtoSalvo);
        when(produtoMapper.toProdutoResponse(produtoSalvo)).thenReturn(response);

        ProdutoResponse resultado = produtoService.alterarProduto(request);

        assertEquals("Abacate Orgânico", resultado.nome());
    }

    // remover produto existente

    @Test
    void removerProdutoPorId_quandoExiste_deveRemover() {
        Produto acelga = criarProduto(4L, "Acelga", BigDecimal.valueOf(4.99));
        ProdutoResponse response = criarResponse(4L, "Acelga", BigDecimal.valueOf(4.99));

        when(produtoRepository.findById(4L)).thenReturn(Optional.of(acelga));
        when(produtoMapper.toProdutoResponse(acelga)).thenReturn(response);

        assertDoesNotThrow(() -> produtoService.removerProdutoPorId(4L));

        verify(produtoRepository).deleteById(4L);
    }

    // lançar exceção ao tentar remover produto inexistente

    @Test
    void removerProdutoPorId_quandoNaoExiste_deveLancarExcecao() {
        when(produtoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntidadeNaoEncontradaException.class,
            () -> produtoService.removerProdutoPorId(99L));

        verify(produtoRepository, never()).deleteById(anyLong());
    }

    // recuperar produtos com paginação e filtro

    @Test
    void recuperarProdutosComPaginacao_deveRetornarPageDeProdutoResponse() {
        PageRequest pageRequest = PageRequest.of(0, 3);
        String nome = "Ab";
        Produto abacate = criarProduto(1L, "Abacate", BigDecimal.valueOf(2.45));
        Produto abobora = criarProduto(3L, "Abóbora", BigDecimal.valueOf(4.70));
        List<Produto> lista = Arrays.asList(abacate, abobora);
        Page<Produto> page = new PageImpl<>(lista, pageRequest, lista.size());

        ProdutoResponse response1 = criarResponse(1L, "Abacate", BigDecimal.valueOf(2.45));
        ProdutoResponse response2 = criarResponse(3L, "Abóbora", BigDecimal.valueOf(4.70));

        when(produtoRepository.recuperarProdutosComPaginacao(pageRequest, "%" + nome + "%"))
            .thenReturn(page);
        when(produtoMapper.toProdutoResponse(abacate)).thenReturn(response1);
        when(produtoMapper.toProdutoResponse(abobora)).thenReturn(response2);

        Page<ProdutoResponse> resultado = produtoService.recuperarProdutosComPaginacao(pageRequest, nome);

        assertEquals(2, resultado.getTotalElements());
        assertEquals("Abacate", resultado.getContent().get(0).nome());
    }
}
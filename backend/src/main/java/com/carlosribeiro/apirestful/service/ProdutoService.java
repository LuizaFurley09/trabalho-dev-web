package com.carlosribeiro.apirestful.service;

import com.carlosribeiro.apirestful.dto.ProdutoRequest;
import com.carlosribeiro.apirestful.dto.ProdutoResponse;
import com.carlosribeiro.apirestful.exception.EntidadeNaoEncontradaException;
import com.carlosribeiro.apirestful.mapper.ProdutoMapper;
import com.carlosribeiro.apirestful.model.Produto;
import com.carlosribeiro.apirestful.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProdutoService {
    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private ProdutoMapper produtoMapper;

    public List<ProdutoResponse> recuperarProdutos() {
        List<Produto> produtos = produtoRepository.recuperarProduos();
        return produtoMapper.toProdutosResponse(produtos);
    }

    public ProdutoResponse recuperarProdutoPorId(long id) {
        Produto produto = produtoRepository.findById(id)
            .orElseThrow(() -> new EntidadeNaoEncontradaException(
                "Produto com id = " + id + " não encontrado."));
        return produtoMapper.toProdutoResponse(produto);
    }

    public ProdutoResponse cadastrarProduto(ProdutoRequest produtoRequest) {
        Produto produto = produtoMapper.toProduto(produtoRequest);
        produto = produtoRepository.save(produto);
        return produtoMapper.toProdutoResponse(produto);
    }

    public ProdutoResponse alterarProduto(ProdutoRequest produtoRequest) {
        Produto produto = produtoMapper.toProduto(produtoRequest);
        produto = produtoRepository.save(produto);
        return produtoMapper.toProdutoResponse(produto);
    }

    public void removerProdutoPorId(long id) {
        recuperarProdutoPorId(id);
        produtoRepository.deleteById(id);
    }

    public Page<ProdutoResponse> recuperarProdutosComPaginacao(PageRequest pageRequest, String nome) {
        Page<Produto> page = produtoRepository.recuperarProdutosComPaginacao(pageRequest, "%" + nome + "%");
        return page.map((produto) -> produtoMapper.toProdutoResponse(produto));
    }
}

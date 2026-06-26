package com.carlosribeiro.apirestful.service;

import com.carlosribeiro.apirestful.exception.EntidadeNaoEncontradaException;
import com.carlosribeiro.apirestful.model.Produto;
import com.carlosribeiro.apirestful.repository.ProdutoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProdutoService {
    @Autowired
    private ProdutoRepository produtoRepository;

    public List<Produto> recuperarProduos() {
        return produtoRepository.recuperarProduos();
    }

    public Produto recuperarProdutoPorId(long id) {
        return produtoRepository.findById(id)
            .orElseThrow(() -> new EntidadeNaoEncontradaException(
                "Produto com id = " + id + " não encontrado."));
    }

    public Produto cadastrarProduo(Produto produto) {
        return produtoRepository.save(produto);
    }

    public Produto alterarProduo(Produto produto) {
        return produtoRepository.save(produto);
    }

    public void removerProduoPorId(long id) {
        recuperarProdutoPorId(id);
        produtoRepository.deleteById(id);
    }
}

package com.carlosribeiro.apirestful.service;

import com.carlosribeiro.apirestful.dto.CategoriaDto;
import com.carlosribeiro.apirestful.mapper.CategoriaMapper;
import com.carlosribeiro.apirestful.model.Categoria;
import com.carlosribeiro.apirestful.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private CategoriaMapper categoriaMapper;

    public List<Categoria> recuperarCategoriasComProdutosV1() {
        return categoriaRepository.recuperarCategoriasComProdutos();
    }

    public List<CategoriaDto> recuperarCategoriasComProdutosV2() {
        List<Categoria> categorias = categoriaRepository.recuperarCategoriasComProdutos();
        return categoriaMapper.toCategoriasDto(categorias);
    }
}

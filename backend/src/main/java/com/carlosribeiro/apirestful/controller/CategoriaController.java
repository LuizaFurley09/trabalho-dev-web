package com.carlosribeiro.apirestful.controller;

import com.carlosribeiro.apirestful.dto.CategoriaDto;
import com.carlosribeiro.apirestful.model.Categoria;
import com.carlosribeiro.apirestful.service.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin("http://localhost:5173")
@RequestMapping("categorias")
@RestController
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    @GetMapping("v1")
    public List<Categoria> recuperarCategoriasComProdutosV1() {
        List<Categoria> categorias = categoriaService.recuperarCategoriasComProdutosV1();
        categorias.forEach((categoria) -> {
            System.out.println("Categoria: " + categoria.getNome());
            categoria.getProdutos().forEach((produto) -> {
                System.out.println("    Produto: " + produto.getNome());
            });
        });
        return categorias;
    }

    @GetMapping("v2")
    public List<CategoriaDto> recuperarCategoriasComProdutosV2() {
        return categoriaService.recuperarCategoriasComProdutosV2();
    }

}

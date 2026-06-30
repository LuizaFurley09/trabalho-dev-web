package com.carlosribeiro.apirestful.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.carlosribeiro.apirestful.model.Categoria;


public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    @Query("select c from Categoria c " + "left join fetch c.produtos " + "order by c.id")
    List<Categoria> recuperarCategoriasComProdutos();
}

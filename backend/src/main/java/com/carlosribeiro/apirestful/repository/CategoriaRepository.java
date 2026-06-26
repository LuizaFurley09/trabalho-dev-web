package com.carlosribeiro.apirestful.repository;

import com.carlosribeiro.apirestful.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    @Query("select c from Categoria c " +
        "left outer join c.produtos " +
        "order by c.id")
    List<Categoria> recuperarCategoriasComProdutos();
}

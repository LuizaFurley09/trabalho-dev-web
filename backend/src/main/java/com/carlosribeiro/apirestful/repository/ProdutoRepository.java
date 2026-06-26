package com.carlosribeiro.apirestful.repository;

import com.carlosribeiro.apirestful.model.Produto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    @Query("select p from Produto p left join fetch p.categoria order by p.id")
    List<Produto> recuperarProduos();

    @Query(
        value = "select p from Produto p left join fetch p.categoria where p.nome like :nome order by p.id",
        countQuery = "select count(p) from Produto p where p.nome like :nome "
    )
    Page<Produto> recuperarProdutosComPaginacao(PageRequest pageRequest, @Param("nome") String nome);
}

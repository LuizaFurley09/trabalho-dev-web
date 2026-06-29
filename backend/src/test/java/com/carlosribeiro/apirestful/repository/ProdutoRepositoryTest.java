package com.carlosribeiro.apirestful.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import com.carlosribeiro.apirestful.config.SecurityConfigTest;
import com.carlosribeiro.apirestful.model.Categoria;
import com.carlosribeiro.apirestful.model.Produto;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@DataJpaTest
@ActiveProfiles("test")
@Import(SecurityConfigTest.class)
class ProdutoRepositoryTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Test
    void deveSalvarProduto() {
        Categoria fruta = new Categoria("Fruta Geral");
        entityManager.persist(fruta);

        Produto banana = new Produto(
                "banana.jpg",
                "Banana",
                "Banana prata",
                true,
                100,
                new BigDecimal("1.50"),
                LocalDate.now(),
                fruta
        );

        Produto salvo = produtoRepository.save(banana);

        assertThat(salvo.getId()).isPositive();
        assertThat(salvo.getNome()).isEqualTo("Banana");
    }

    @Test
    void deveRecuperarTodosProdutosComCategoria() {
        Categoria fruta = new Categoria("FrutaTeste");
        Categoria verdura = new Categoria("VerduraTeste");
        entityManager.persist(fruta);
        entityManager.persist(verdura);

        Produto p1 = new Produto("alface.jpg", "AlfaceTeste", "desc",
                true, 50, new BigDecimal("2.99"), LocalDate.now(), verdura);
        Produto p2 = new Produto("banana.jpg", "BananaTeste", "desc",
                true, 100, new BigDecimal("1.50"), LocalDate.now(), fruta);
 
        entityManager.persist(p1);
        entityManager.persist(p2);
        entityManager.flush();
 

        List<Produto> todos = produtoRepository.recuperarProduos();

        List<Produto> doTeste = todos.stream()
                .filter(p -> p.getNome().endsWith("Teste"))
                .toList();

        assertThat(doTeste).hasSize(2);
        assertThat(doTeste)
                .extracting(Produto::getNome)
                .containsExactlyInAnyOrder("AlfaceTeste", "BananaTeste");

        assertThat(doTeste.get(0).getCategoria()).isNotNull();
    }

    @Test
    void deveRecuperarProdutosComPaginacao() {
        Categoria cat = new Categoria("CategoriaTeste_Paginacao");
        entityManager.persist(cat);

        entityManager.persist(new Produto("alface.jpg", "PagTeste_Alface", "desc",
                true, 10, new BigDecimal("3.00"), LocalDate.now(), cat));
        entityManager.persist(new Produto("couve.jpg", "PagTeste_Couve", "desc",
                true, 20, new BigDecimal("4.00"), LocalDate.now(), cat));
        entityManager.persist(new Produto("agriao.jpg", "PagTeste_Agriao", "desc",
                true, 30, new BigDecimal("2.00"), LocalDate.now(), cat));
        entityManager.flush();

        PageRequest pageRequest = PageRequest.of(0, 2);
        Page<Produto> pagina =
                produtoRepository.recuperarProdutosComPaginacao(pageRequest, "%PagTeste%");

        assertThat(pagina.getTotalElements()).isEqualTo(3);
        assertThat(pagina.getContent()).hasSize(2);
    }

    @Test
    void deveFiltrarProdutosPorNome() {
        Categoria cat = new Categoria("CategoriaVerduraTeste");
        entityManager.persist(cat);

        entityManager.persist(new Produto("alface.jpg", "FiltroAlface", "desc",
                true, 50, new BigDecimal("2.99"), LocalDate.now(), cat));
        entityManager.persist(new Produto("acelga.jpg", "FiltroAcelga", "desc",
                true, 40, new BigDecimal("3.50"), LocalDate.now(), cat));
        entityManager.persist(new Produto("couve.jpg", "FiltroCouve", "desc",
                true, 100, new BigDecimal("1.50"), LocalDate.now(), cat));
        entityManager.flush();
 

        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Produto> pagina =
                produtoRepository.recuperarProdutosComPaginacao(pageRequest, "%FiltroA%");

        assertThat(pagina.getTotalElements()).isEqualTo(2);
        assertThat(pagina.getContent())
                .extracting(Produto::getNome)
                .containsExactlyInAnyOrder("FiltroAlface", "FiltroAcelga");
    }

    @Test
    void deveExcluirProduto() {
        Categoria verdura = new Categoria("VerduraGeral");
        entityManager.persist(verdura);

        Produto cenoura = new Produto("cenoura.jpg","Cenoura","laranja",
                true, 60, new BigDecimal("1.00"), LocalDate.now(), verdura);
        entityManager.persist(cenoura);
        entityManager.flush();

        Long id = cenoura.getId();

        produtoRepository.deleteById(id);
        entityManager.flush();

        Produto encontrado = entityManager.find(Produto.class, id);

        assertThat(encontrado).isNull();
    }

    @Test
    void deveAtualizarProduto() {
        Categoria verdura = new Categoria("VerduraGeral");
        entityManager.persist(verdura);

        Produto cebola = new Produto("cebola.jpg", "Cebola", "roxa", true, 200,
                new BigDecimal("0.50"), LocalDate.now(), verdura);

        entityManager.persist(cebola);
        entityManager.flush();

        cebola.setPreco(new BigDecimal("0.75"));
        cebola.setQtdEstoque(150);

        produtoRepository.save(cebola);
        entityManager.flush();

        Produto recuperado = entityManager.find(Produto.class, cebola.getId());

        assertThat(recuperado.getPreco()).isEqualByComparingTo(new BigDecimal("0.75"));
        assertThat(recuperado.getQtdEstoque()).isEqualTo(150);
    }
}
package com.carlosribeiro.apirestful.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import com.carlosribeiro.apirestful.config.SecurityConfigTest;
import com.carlosribeiro.apirestful.model.Categoria;
import com.carlosribeiro.apirestful.model.Produto;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@DataJpaTest
@ActiveProfiles("test")
@Import(SecurityConfigTest.class)
class CategoriaRepositoryTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Test
    void deveSalvarCategoria() {
        Categoria frutas = new Categoria("FrutaTeste");
        Categoria salva = categoriaRepository.save(frutas);

        assertThat(salva.getId()).isPositive();
        assertThat(salva.getNome()).isEqualTo("FrutaTeste");
    }

    @Test
    void deveRecuperarCategoriasComProdutos() {
        Categoria verduras = new Categoria("VerduraTeste");
        entityManager.persist(verduras);

        Produto alface = new Produto(
                "alface.jpg",
                "Alface Crespa",
                "Alface orgânica",
                true,
                30,
                new BigDecimal("3.90"),
                LocalDate.now(),
                verduras
        );

        Produto couve = new Produto(
                "couve.jpg",
                "Couve Manteiga",
                "Couve fresca",
                true,
                20,
                new BigDecimal("2.50"),
                LocalDate.now(),
                verduras
        );

        entityManager.persist(alface);
        entityManager.persist(couve);
        entityManager.flush();
        entityManager.clear();

        List<Categoria> categorias = categoriaRepository.recuperarCategoriasComProdutos();
        
        Categoria recuperada = categorias.stream()
                .filter(c -> c.getNome().equals("VerduraTeste"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Categoria não encontrada"));

        assertThat(recuperada.getProdutos()).hasSize(2);
        assertThat(recuperada.getProdutos()).extracting(Produto::getNome)
                .containsExactlyInAnyOrder("Alface Crespa", "Couve Manteiga");
    }

    @Test
    void deveExcluirCategoria() {
        Categoria legumes = new Categoria("LegumeTeste");

        entityManager.persist(legumes);
        entityManager.flush();

        Long id = legumes.getId();

        categoriaRepository.deleteById(id);
        entityManager.flush();

        Categoria encontrada = entityManager.find(Categoria.class, id);

        assertThat(encontrada).isNull();
    }

    @Test
    void deveAtualizarCategoria() {
        Categoria frutas = new Categoria("FrutaTeste");

        entityManager.persist(frutas);
        entityManager.flush();

        frutas.setNome("FrutasTropicais");

        categoriaRepository.save(frutas);
        entityManager.flush();

        Categoria recuperada = entityManager.find(Categoria.class, frutas.getId());

        assertThat(recuperada.getNome()).isEqualTo("FrutasTropicais");
    }
}
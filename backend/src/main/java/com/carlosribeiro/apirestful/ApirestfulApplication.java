package com.carlosribeiro.apirestful;

import com.carlosribeiro.apirestful.model.Categoria;
import com.carlosribeiro.apirestful.model.Produto;
import com.carlosribeiro.apirestful.repository.CategoriaRepository;
import com.carlosribeiro.apirestful.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.math.BigDecimal;
import java.time.LocalDate;

@SpringBootApplication
public class ApirestfulApplication implements CommandLineRunner {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    public static void main(String[] args) {
        SpringApplication.run(ApirestfulApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        Categoria fruta = new Categoria("fruta");
        categoriaRepository.save(fruta);

        Categoria legume = new Categoria("legume");
        categoriaRepository.save(legume);

        Categoria verdura = new Categoria("verdura");
        categoriaRepository.save(verdura);

        Produto produto = new Produto(
            "abacate.png",
            "Abacate",
            "1 unidade aprox. 750g",
            true,
            100,
            BigDecimal.valueOf(2.45),
            LocalDate.of(2025, 4, 26),
            fruta);
        produtoRepository.save(produto);

        produto = new Produto(
            "abobrinha.png",
            "Abobrinha",
            "1 unidade aprox. 250g",
            false,
            500,
            BigDecimal.valueOf(1.1),
            LocalDate.of(2025, 5, 22),
            legume);
        produtoRepository.save(produto);

        produto = new Produto(
            "abobora.png",
            "Abóbora",
            "1 unidade aprox. 1,9kg",
            true,
            400,
            BigDecimal.valueOf(4.7),
            LocalDate.of(2025, 3, 24),
            legume);
        produtoRepository.save(produto);

        produto = new Produto(
            "acelga.png",
            "Acelga",
            "1 maço de aprox. 400g",
            true,
            120,
            BigDecimal.valueOf(4.99),
            LocalDate.of(2025, 3, 12),
            verdura);
        produtoRepository.save(produto);

        produto = new Produto(
            "agriao.png",
            "Agrião",
            "1 maço de aprox. 200g",
            true,
            340,
            BigDecimal.valueOf(2.5),
            LocalDate.of(2025, 5, 17),
            verdura);
        produtoRepository.save(produto);
    }
}

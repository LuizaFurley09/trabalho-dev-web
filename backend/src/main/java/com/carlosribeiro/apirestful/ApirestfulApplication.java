package com.carlosribeiro.apirestful;

import com.carlosribeiro.apirestful.auth.model.Usuario;
import com.carlosribeiro.apirestful.auth.repository.UsuarioRepository;
import com.carlosribeiro.apirestful.auth.util.Role;
import com.carlosribeiro.apirestful.model.Categoria;
import com.carlosribeiro.apirestful.model.Produto;
import com.carlosribeiro.apirestful.repository.CategoriaRepository;
import com.carlosribeiro.apirestful.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;

@SpringBootApplication
public class ApirestfulApplication implements CommandLineRunner {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public static void main(String[] args) {
        SpringApplication.run(ApirestfulApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        Usuario admin = new Usuario(
            "Admin",
            "admin@mail.com",
            passwordEncoder.encode("desweb"),
            Role.ADMIN);
        usuarioRepository.save(admin);

        Usuario user = new Usuario(
            "User",
            "user@mail.com",
            passwordEncoder.encode("desweb"),
            Role.USER);
        usuarioRepository.save(user);
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

        produto = new Produto(
            "alface.png",
            "Alface",
            "1 maço de aprox. 200g",
            true,
            220,
            BigDecimal.valueOf(4.99),
            LocalDate.of(2023, 5, 14),
            verdura);
        produtoRepository.save(produto);

        produto = new Produto(
            "banana.png",
            "Banana",
            "1 unidade aprox. 165g",
            true,
            350,
            BigDecimal.valueOf(1.05),
            LocalDate.of(2023, 2, 22),
            fruta);
        produtoRepository.save(produto);

        produto = new Produto(
            "berinjela.png",
            "Berinjela",
            "1 unidade aprox. 370g",
            true,
            720,
            BigDecimal.valueOf(1.85),
            LocalDate.of(2023, 2, 23),
            legume);
        produtoRepository.save(produto);

        produto = new Produto(
            "brocolis.png",
            "Brócolis",
            "1 unidade aprox. 300g",
            true,
            600,
            BigDecimal.valueOf(5.39),
            LocalDate.of(2023, 3, 28),
            verdura);
        produtoRepository.save(produto);

        produto = new Produto(
            "cebola.png",
            "Cebola",
            "1 unidade aprox. 200g",
            true,
            95,
            BigDecimal.valueOf(0.56),
            LocalDate.of(2023, 4, 30),
            legume);
        produtoRepository.save(produto);

        produto = new Produto(
            "cenoura.png",
            "Cenoura",
            "1 unidade aprox. 180g",
            true,
            350,
            BigDecimal.valueOf(1.01),
            LocalDate.of(2023, 5, 29),
            legume);
        produtoRepository.save(produto);

        produto = new Produto(
            "cereja.png",
            "Cereja",
            "1 unidade aprox. 250g",
            true,
            240,
            BigDecimal.valueOf(11.23),
            LocalDate.of(2023, 5, 11),
            fruta);
        produtoRepository.save(produto);
    }
}

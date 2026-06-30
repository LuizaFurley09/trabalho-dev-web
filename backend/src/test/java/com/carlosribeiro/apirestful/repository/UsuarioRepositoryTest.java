package com.carlosribeiro.apirestful.repository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import com.carlosribeiro.apirestful.auth.model.Usuario;
import com.carlosribeiro.apirestful.auth.repository.UsuarioRepository;
import com.carlosribeiro.apirestful.auth.util.Role;
import com.carlosribeiro.apirestful.config.SecurityConfigTest;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@DataJpaTest
@ActiveProfiles("test")
@Import(SecurityConfigTest.class)
class UsuarioRepositoryTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    void deveSalvarUsuario() {
        Usuario usuario = new Usuario("Maria", "maria@mail.com", "senha123", Role.USER);

        Usuario salvo = usuarioRepository.save(usuario);

        assertThat(salvo.getId()).isPositive();
        assertThat(salvo.getNome()).isEqualTo("Maria");
        assertThat(salvo.getEmail()).isEqualTo("maria@mail.com");
        assertThat(salvo.getRole()).isEqualTo(Role.USER);
    }

    @Test
    void deveBuscarUsuarioPorEmail() {
        Usuario usuario = new Usuario("João", "joao@mail.com", "senha123", Role.USER);
        entityManager.persist(usuario);
        entityManager.flush();

        Optional<Usuario> encontrado = usuarioRepository.findByEmail("joao@mail.com");

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getNome()).isEqualTo("João");
    }

    @Test
    void deveRetornarVazioQuandoEmailNaoExiste() {
        Optional<Usuario> encontrado = usuarioRepository.findByEmail("naoexiste@mail.com");

        assertThat(encontrado).isEmpty();
    }

    @Test
    void deveRejeitarEmailDuplicado() {
        Usuario u1 = new Usuario("Ana", "ana@mail.com", "senha123", Role.USER);
        Usuario u2 = new Usuario("Ana2", "ana@mail.com", "senha456", Role.ADMIN);

        entityManager.persist(u1);
        entityManager.flush();

        assertThatThrownBy(() -> {
            entityManager.persist(u2);
            entityManager.flush();
        }).isInstanceOf(Exception.class);
    }

    @Test
    void deveExcluirUsuario() {
        Usuario usuario = new Usuario("Carlos", "carlos@mail.com", "senha123", Role.USER);
        entityManager.persist(usuario);
        entityManager.flush();

        Long id = usuario.getId();

        usuarioRepository.deleteById(id);
        entityManager.flush();

        Usuario encontrado = entityManager.find(Usuario.class, id);

        assertThat(encontrado).isNull();
    }

    @Test
    void deveAtualizarUsuario() {

        Usuario usuario = new Usuario("Carlos", "carlos@mail.com", "senha123", Role.USER);
        entityManager.persist(usuario);
        entityManager.flush();

        usuario.setNome("Carlos Atualizado");
        usuarioRepository.save(usuario);
        entityManager.flush();

        Usuario atualizado = entityManager.find(Usuario.class, usuario.getId());
        assertThat(atualizado.getNome()).isEqualTo("Carlos Atualizado");
    }
}
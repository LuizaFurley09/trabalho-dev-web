package com.carlosribeiro.apirestful.auth.service;

import com.carlosribeiro.apirestful.auth.model.Usuario;
import com.carlosribeiro.apirestful.auth.repository.UsuarioRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@AllArgsConstructor
@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        // O método loadUserByUsername() deve propagar a exceção UsernameNotFoundException
        Usuario usuario = usuarioRepository
            .findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        // A classe User é uma classe do Spring que implementa UserDetails.
        // UserDetails é uma representação para usuário. Possui tudo que um usuário precisa ter.
        // Aqui não precisamos das permissões daí estarmos retornando uma lista vazia.

        return new User(usuario.getEmail(), usuario.getSenha(), Collections.emptyList());
    }
}

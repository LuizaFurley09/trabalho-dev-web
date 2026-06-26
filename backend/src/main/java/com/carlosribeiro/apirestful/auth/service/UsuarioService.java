package com.carlosribeiro.apirestful.auth.service;

import com.carlosribeiro.apirestful.auth.dto.UsuarioCreate;
import com.carlosribeiro.apirestful.auth.model.Usuario;
import com.carlosribeiro.apirestful.auth.repository.UsuarioRepository;
import com.carlosribeiro.apirestful.auth.util.InfoUsuario;
import com.carlosribeiro.apirestful.auth.util.Role;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public InfoUsuario cadastrarUsuario(UsuarioCreate usuarioCreate) {
        Usuario usuarioCadastrado = usuarioRepository
            .findByEmail(usuarioCreate.getEmail())
            .orElse(null);
        if (usuarioCadastrado == null) {
            Usuario usuario = new Usuario(
                usuarioCreate.getNome(),
                usuarioCreate.getEmail(),
                passwordEncoder.encode(usuarioCreate.getSenha()),
                Role.USER);
            usuarioRepository.save(usuario);
            return new InfoUsuario(true, false, "Usuário cadastrado com sucesso!");
        }
        else {
            return new InfoUsuario(false, true, "Email já cadastrado!");
        }
    }

    public List<Usuario> recuperarUsuarios() {
        return usuarioRepository.findAll();
    }
}

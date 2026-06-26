package com.carlosribeiro.apirestful.auth.controller;

import com.carlosribeiro.apirestful.auth.dto.UsuarioCreate;
import com.carlosribeiro.apirestful.auth.model.Usuario;
import com.carlosribeiro.apirestful.auth.service.UsuarioService;
import com.carlosribeiro.apirestful.auth.util.InfoUsuario;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("usuarios")   // htttp://localhost:8080/usuarios
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping
    public List<Usuario> recuperaUsuarios() {
        return usuarioService.recuperarUsuarios();
    }

    @PostMapping
    public InfoUsuario cadastrarUsuario(@RequestBody @Valid UsuarioCreate usuarioCreate) {
        return usuarioService.cadastrarUsuario(usuarioCreate);
    }
}
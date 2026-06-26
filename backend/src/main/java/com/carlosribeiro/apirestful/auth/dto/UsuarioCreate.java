package com.carlosribeiro.apirestful.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class UsuarioCreate {
    @NotEmpty(message = "O 'Nome' deve ser informado.")
    private String nome;

    @NotEmpty(message = "O 'Email' deve ser informado.")
    @Email(message = "Email inválido.")
    private String email;

    @NotEmpty(message = "A 'Senha' deve ser informada.")
    private String senha;
}

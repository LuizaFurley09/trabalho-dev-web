package com.carlosribeiro.apirestful.auth.util;

public record TokenResponse(String token, long idUsuario, String nome, String role) {
}


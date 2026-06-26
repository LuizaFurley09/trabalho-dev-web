package com.carlosribeiro.apirestful.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record CategoriaDto(Long id,
                           String nome,
                           @JsonProperty("produtos")
                           List<ProdutoResponse> produtosDto) {
}

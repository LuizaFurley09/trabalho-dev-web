package com.carlosribeiro.apirestful.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ProdutoResponse(
    Long id,
    String imagem,
    String nome,
    String descricao,
    boolean disponivel,
    int qtdEstoque,
    BigDecimal preco,
    LocalDate dataCadastro,
    @JsonProperty("categoria")
    CategoriaResumo categoriaResumo) {
}
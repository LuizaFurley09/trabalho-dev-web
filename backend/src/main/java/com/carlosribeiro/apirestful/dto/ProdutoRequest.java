package com.carlosribeiro.apirestful.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ProdutoRequest(
    @Null(groups = OnCreate.class, message = "O 'Id' deve ser nulo no cadastro.")
    @NotNull(groups = OnUpdate.class, message = "O 'Id' deve ser informado na alteração.")
    Long id,

    @NotEmpty(message = "A 'Imagem' deve ser informada.")
    String imagem,

    @NotEmpty(message = "O 'Nome' deve ser informado.")
    String nome,

    @NotEmpty(message = "A 'Descrição' deve ser informada.")
    String descricao,

    boolean disponivel,

    @NotNull(message = "A 'Quantidade em estoque' deve ser informada.")
    @Min(value = 0, message = "A 'Quantidade em estoque' deve ser maior ou igual a 0.")
    Integer qtdEstoque,

    @NotNull(message = "O 'Preço' deve ser informado.")
    @DecimalMin(inclusive = true, value = "0.1", message = "O 'Preço' deve ser maior ou igual a 0.1.")
    BigDecimal preco,

    @NotNull(message = "A 'Data de Cadastro' deve ser informada.")
    LocalDate dataCadastro,

    @JsonProperty("categoria")
    @NotNull(message = "A 'Categoria' deve ser informada.")
    CategoriaResumo categoriaResumo
) {
    // Oncreate e OnUpdate são duas interfaces marcadoras (marker interface) para grupos de
    // validação do Bean Validation.
    // Em ProdutoRequest a interface OnCreate (que não tem métodos de propósito) apenas serve
    // como “rótulo” para dizer quais regras devem rodar no cenário de criação.
    // No campo id de ProdutoRequest existem regras por grupo
    // @Null(groups = OnCreate.class) no POST
    // @NotNull(groups = OnUpdate.class) no PUT
    // No controller os grupos de validação são ativados com @Validated:
    // POST ativa OnCreate em ProdutoController
    // PUT ativa OnUpdate em ProdutoController

    public interface OnCreate {}
    public interface OnUpdate {}
}
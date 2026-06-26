package com.carlosribeiro.apirestful.mapper;

import com.carlosribeiro.apirestful.dto.ProdutoRequest;
import com.carlosribeiro.apirestful.dto.ProdutoResponse;
import com.carlosribeiro.apirestful.model.Produto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProdutoMapper {

    List<ProdutoResponse> toProdutosResponse(List<Produto> produtos);

    @Mapping(source = "categoria", target = "categoriaResumo")
    ProdutoResponse toProdutoResponse(Produto produto);

    @Mapping(source = "categoriaResumo", target = "categoria")
    Produto toProduto(ProdutoRequest produtoRequest);
}

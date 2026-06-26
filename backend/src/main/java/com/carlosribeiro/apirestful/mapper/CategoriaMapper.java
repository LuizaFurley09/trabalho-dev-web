package com.carlosribeiro.apirestful.mapper;

import com.carlosribeiro.apirestful.dto.CategoriaDto;
import com.carlosribeiro.apirestful.dto.CategoriaResumo;
import com.carlosribeiro.apirestful.model.Categoria;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = ProdutoMapper.class)
public interface CategoriaMapper {

    List<CategoriaDto> toCategoriasDto(List<Categoria> categorias);

    @Mapping(source = "produtos", target = "produtosDto")
    CategoriaDto toCategoriaDto(Categoria categoria);

    // CategoriaResumo toCategoriaResumo(Categoria categoria);
}

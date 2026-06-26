package com.carlosribeiro.apirestful.repository;

import com.carlosribeiro.apirestful.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
}

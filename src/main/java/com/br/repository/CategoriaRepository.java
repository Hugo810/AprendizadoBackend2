package com.br.repository;

import com.br.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    // O método save() vem de JpaRepository
    
    Optional<Categoria> findByNome(String nome);
    List<Categoria> findByAtivoTrue();
    List<Categoria> findByAtivoTrueOrderByNomeAsc();
    List<Categoria> findByTipoSistemaFalseAndAtivoTrue();
    List<Categoria> findByTipoSistemaTrue();
}
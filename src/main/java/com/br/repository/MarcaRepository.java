package com.br.repository;

import com.br.model.Marca;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MarcaRepository extends JpaRepository<Marca, Long> {
    
    Optional<Marca> findByNome(String nome);
    
    List<Marca> findByAtivoTrue();
    
    List<Marca> findByAtivoTrueOrderByNomeAsc();
    
    List<Marca> findByTipoSistemaFalseAndAtivoTrue();
    
    List<Marca> findByTipoSistemaTrue();
    
    boolean existsByNome(String nome);
}
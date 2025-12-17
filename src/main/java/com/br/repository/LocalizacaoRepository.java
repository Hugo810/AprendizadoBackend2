package com.br.repository;

import com.br.model.Localizacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocalizacaoRepository extends JpaRepository<Localizacao, Long> {
    
    Optional<Localizacao> findByNome(String nome);
    
    List<Localizacao> findByAtivoTrue();
    
    List<Localizacao> findByAtivoTrueOrderByNomeAsc();
    
    List<Localizacao> findByTipoSistemaFalseAndAtivoTrue();
    
    List<Localizacao> findByTipoSistemaTrue();
    
    boolean existsByNome(String nome);
}
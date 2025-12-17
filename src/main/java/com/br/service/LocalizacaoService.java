package com.br.service;

import com.br.model.Localizacao;
import java.util.List;
import java.util.Optional;

public interface LocalizacaoService {
    
    // CRUD básico
    Localizacao save(Localizacao localizacao);
    Optional<Localizacao> findById(Long id);
    List<Localizacao> findAll();
    List<Localizacao> findActive();
    Localizacao update(Long id, Localizacao localizacaoDetails);
    void delete(Long id);
    void softDelete(Long id);
    
    // Consultas específicas
    Optional<Localizacao> findByNome(String nome);
    List<Localizacao> findCustom(); // Localizações personalizadas ativas
    List<Localizacao> findSystem(); // Localizações do sistema
    
    // Validações
    boolean existsByNome(String nome);
}
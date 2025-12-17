package com.br.service;

import com.br.model.Marca;
import java.util.List;
import java.util.Optional;

public interface MarcaService {
    
    // CRUD básico
    Marca save(Marca marca);
    Optional<Marca> findById(Long id);
    List<Marca> findAll();
    List<Marca> findActive();
    Marca update(Long id, Marca marcaDetails);
    void delete(Long id);
    void softDelete(Long id);
    
    // Consultas específicas
    Optional<Marca> findByNome(String nome);
    List<Marca> findCustom(); // Marcas personalizadas ativas
    List<Marca> findSystem(); // Marcas do sistema
    
    // Validações
    boolean existsByNome(String nome);
}
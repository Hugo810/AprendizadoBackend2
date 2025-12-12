package com.br.service;

import com.br.model.Categoria;
import com.br.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service  // ← ESSA ANOTAÇÃO É ESSENCIAL!
public class CategoriaService {
    
    @Autowired
    private CategoriaRepository categoriaRepository;
    
    public List<Categoria> findAll() {
        return categoriaRepository.findAll();
    }
    
    public List<Categoria> findActive() {
        return categoriaRepository.findByAtivoTrueOrderByNomeAsc();
    }
    
    public List<Categoria> findCustom() {
        return categoriaRepository.findByTipoSistemaFalseAndAtivoTrue();
    }
    
    public List<Categoria> findSystem() {
        return categoriaRepository.findByTipoSistemaTrue();
    }
    
    public Optional<Categoria> findById(Long id) {
        return categoriaRepository.findById(id);
    }
    
    public Categoria save(Categoria categoria) {
        return categoriaRepository.save(categoria);
    }
    
    public void delete(Long id) {
        categoriaRepository.deleteById(id);
    }
    
    public Categoria update(Long id, Categoria categoriaDetails) {
        return categoriaRepository.findById(id)
            .map(categoria -> {
                // Não permite alterar categorias do sistema
                if (!categoria.getTipoSistema()) {
                    categoria.setNome(categoriaDetails.getNome());
                    categoria.setDescricao(categoriaDetails.getDescricao());
                    categoria.setAtivo(categoriaDetails.getAtivo());
                }
                return categoriaRepository.save(categoria);
            })
            .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));
    }
    
    // Soft delete para personalizadas, não permite excluir do sistema
    public void softDelete(Long id) {
        categoriaRepository.findById(id).ifPresent(categoria -> {
            if (!categoria.getTipoSistema()) {
                categoria.setAtivo(false);
                categoriaRepository.save(categoria);
            }
        });
    }
}
package com.br.service;

import com.br.model.Marca;
import com.br.repository.MarcaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class MarcaServiceImpl implements MarcaService {
    
    @Autowired
    private MarcaRepository marcaRepository;
    
    @Override
    @Transactional
    public Marca save(Marca marca) {
        // Verificar se já existe marca com mesmo nome
        if (marca.getId() == null && marcaRepository.existsByNome(marca.getNome())) {
            throw new IllegalArgumentException("Marca com nome '" + marca.getNome() + "' já existe");
        }
        return marcaRepository.save(marca);
    }
    
    @Override
    public Optional<Marca> findById(Long id) {
        return marcaRepository.findById(id);
    }
    
    @Override
    public List<Marca> findAll() {
        return marcaRepository.findAll();
    }
    
    @Override
    public List<Marca> findActive() {
        return marcaRepository.findByAtivoTrueOrderByNomeAsc();
    }
    
    @Override
    @Transactional
    public Marca update(Long id, Marca marcaDetails) {
        return marcaRepository.findById(id)
            .map(marca -> {
                // Não permite alterar marcas do sistema
                if (!marca.getTipoSistema()) {
                    marca.setNome(marcaDetails.getNome());
                    marca.setDescricao(marcaDetails.getDescricao());
                    marca.setAtivo(marcaDetails.getAtivo());
                }
                return marcaRepository.save(marca);
            })
            .orElseThrow(() -> new RuntimeException("Marca não encontrada"));
    }
    
    @Override
    @Transactional
    public void delete(Long id) {
        marcaRepository.findById(id).ifPresent(marca -> {
            if (!marca.getTipoSistema()) {
                marcaRepository.delete(marca);
            }
        });
    }
    
    @Override
    @Transactional
    public void softDelete(Long id) {
        marcaRepository.findById(id).ifPresent(marca -> {
            if (!marca.getTipoSistema()) {
                marca.setAtivo(false);
                marcaRepository.save(marca);
            }
        });
    }
    
    @Override
    public Optional<Marca> findByNome(String nome) {
        return marcaRepository.findByNome(nome);
    }
    
    @Override
    public List<Marca> findCustom() {
        return marcaRepository.findByTipoSistemaFalseAndAtivoTrue();
    }
    
    @Override
    public List<Marca> findSystem() {
        return marcaRepository.findByTipoSistemaTrue();
    }
    
    @Override
    public boolean existsByNome(String nome) {
        return marcaRepository.existsByNome(nome);
    }
}
package com.br.service;

import com.br.model.Localizacao;
import com.br.repository.LocalizacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class LocalizacaoServiceImpl implements LocalizacaoService {
    
    @Autowired
    private LocalizacaoRepository localizacaoRepository;
    
    @Override
    @Transactional
    public Localizacao save(Localizacao localizacao) {
        // Verificar se já existe localização com mesmo nome
        if (localizacao.getId() == null && localizacaoRepository.existsByNome(localizacao.getNome())) {
            throw new IllegalArgumentException("Localização com nome '" + localizacao.getNome() + "' já existe");
        }
        return localizacaoRepository.save(localizacao);
    }
    
    @Override
    public Optional<Localizacao> findById(Long id) {
        return localizacaoRepository.findById(id);
    }
    
    @Override
    public List<Localizacao> findAll() {
        return localizacaoRepository.findAll();
    }
    
    @Override
    public List<Localizacao> findActive() {
        return localizacaoRepository.findByAtivoTrueOrderByNomeAsc();
    }
    
    @Override
    @Transactional
    public Localizacao update(Long id, Localizacao localizacaoDetails) {
        return localizacaoRepository.findById(id)
            .map(localizacao -> {
                // Não permite alterar localizações do sistema
                if (!localizacao.getTipoSistema()) {
                    localizacao.setNome(localizacaoDetails.getNome());
                    localizacao.setDescricao(localizacaoDetails.getDescricao());
                    localizacao.setAtivo(localizacaoDetails.getAtivo());
                }
                return localizacaoRepository.save(localizacao);
            })
            .orElseThrow(() -> new RuntimeException("Localização não encontrada"));
    }
    
    @Override
    @Transactional
    public void delete(Long id) {
        localizacaoRepository.findById(id).ifPresent(localizacao -> {
            if (!localizacao.getTipoSistema()) {
                localizacaoRepository.delete(localizacao);
            }
        });
    }
    
    @Override
    @Transactional
    public void softDelete(Long id) {
        localizacaoRepository.findById(id).ifPresent(localizacao -> {
            if (!localizacao.getTipoSistema()) {
                localizacao.setAtivo(false);
                localizacaoRepository.save(localizacao);
            }
        });
    }
    
    @Override
    public Optional<Localizacao> findByNome(String nome) {
        return localizacaoRepository.findByNome(nome);
    }
    
    @Override
    public List<Localizacao> findCustom() {
        return localizacaoRepository.findByTipoSistemaFalseAndAtivoTrue();
    }
    
    @Override
    public List<Localizacao> findSystem() {
        return localizacaoRepository.findByTipoSistemaTrue();
    }
    
    @Override
    public boolean existsByNome(String nome) {
        return localizacaoRepository.existsByNome(nome);
    }
}
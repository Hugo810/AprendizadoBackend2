package com.br.controller;

import com.br.model.Localizacao;
import com.br.service.LocalizacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/localizacoes")
@CrossOrigin(origins = "http://localhost:4200")
public class LocalizacaoController {
    
    @Autowired
    private LocalizacaoService localizacaoService;
    
    // GET - Listar todas as localizações
    @GetMapping
    public ResponseEntity<List<Localizacao>> listarTodas() {
        List<Localizacao> localizacoes = localizacaoService.findAll();
        return ResponseEntity.ok(localizacoes);
    }
    
    // GET - Listar localizações ativas
    @GetMapping("/ativas")
    public ResponseEntity<List<Localizacao>> listarAtivas() {
        List<Localizacao> localizacoes = localizacaoService.findActive();
        return ResponseEntity.ok(localizacoes);
    }
    
    // GET - Listar localizações personalizadas ativas
    @GetMapping("/personalizadas")
    public ResponseEntity<List<Localizacao>> listarPersonalizadas() {
        List<Localizacao> localizacoes = localizacaoService.findCustom();
        return ResponseEntity.ok(localizacoes);
    }
    
    // GET - Buscar por ID
    @GetMapping("/{id}")
    public ResponseEntity<Localizacao> buscarPorId(@PathVariable Long id) {
        Optional<Localizacao> localizacaoOpt = localizacaoService.findById(id);
        return localizacaoOpt.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // GET - Buscar por nome
    @GetMapping("/nome/{nome}")
    public ResponseEntity<Localizacao> buscarPorNome(@PathVariable String nome) {
        Optional<Localizacao> localizacaoOpt = localizacaoService.findByNome(nome);
        return localizacaoOpt.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // POST - Criar nova localização
    @PostMapping
    public ResponseEntity<?> criarLocalizacao(@RequestBody Localizacao localizacao) {
        try {
            Localizacao novaLocalizacao = localizacaoService.save(localizacao);
            return ResponseEntity.status(HttpStatus.CREATED).body(novaLocalizacao);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("erro", "Erro ao criar localização"));
        }
    }
    
    // PUT - Atualizar localização
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarLocalizacao(@PathVariable Long id, @RequestBody Localizacao localizacaoDetails) {
        try {
            Localizacao localizacaoAtualizada = localizacaoService.update(id, localizacaoDetails);
            return ResponseEntity.ok(localizacaoAtualizada);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("erro", "Erro ao atualizar localização"));
        }
    }
    
    // DELETE - Excluir localização (físico)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirLocalizacao(@PathVariable Long id) {
        try {
            localizacaoService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // DELETE - Desativar localização (soft delete)
    @DeleteMapping("/{id}/desativar")
    public ResponseEntity<Void> desativarLocalizacao(@PathVariable Long id) {
        try {
            localizacaoService.softDelete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // POST - Ativar localização
    @PostMapping("/{id}/ativar")
    public ResponseEntity<Localizacao> ativarLocalizacao(@PathVariable Long id) {
        return localizacaoService.findById(id)
                .map(localizacao -> {
                    localizacao.setAtivo(true);
                    Localizacao localizacaoAtivada = localizacaoService.save(localizacao);
                    return ResponseEntity.ok(localizacaoAtivada);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
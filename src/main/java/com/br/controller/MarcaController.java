package com.br.controller;

import com.br.model.Marca;
import com.br.service.MarcaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/marcas")
@CrossOrigin(origins = "http://localhost:4200")
public class MarcaController {
    
    @Autowired
    private MarcaService marcaService;
    
    // GET - Listar todas as marcas
    @GetMapping
    public ResponseEntity<List<Marca>> listarTodas() {
        List<Marca> marcas = marcaService.findAll();
        return ResponseEntity.ok(marcas);
    }
    
    // GET - Listar marcas ativas
    @GetMapping("/ativas")
    public ResponseEntity<List<Marca>> listarAtivas() {
        List<Marca> marcas = marcaService.findActive();
        return ResponseEntity.ok(marcas);
    }
    
    // GET - Listar marcas personalizadas ativas
    @GetMapping("/personalizadas")
    public ResponseEntity<List<Marca>> listarPersonalizadas() {
        List<Marca> marcas = marcaService.findCustom();
        return ResponseEntity.ok(marcas);
    }
    
    // GET - Buscar por ID
    @GetMapping("/{id}")
    public ResponseEntity<Marca> buscarPorId(@PathVariable Long id) {
        Optional<Marca> marcaOpt = marcaService.findById(id);
        return marcaOpt.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // GET - Buscar por nome
    @GetMapping("/nome/{nome}")
    public ResponseEntity<Marca> buscarPorNome(@PathVariable String nome) {
        Optional<Marca> marcaOpt = marcaService.findByNome(nome);
        return marcaOpt.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // POST - Criar nova marca
    @PostMapping
    public ResponseEntity<?> criarMarca(@RequestBody Marca marca) {
        try {
            Marca novaMarca = marcaService.save(marca);
            return ResponseEntity.status(HttpStatus.CREATED).body(novaMarca);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("erro", "Erro ao criar marca"));
        }
    }
    
    // PUT - Atualizar marca
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarMarca(@PathVariable Long id, @RequestBody Marca marcaDetails) {
        try {
            Marca marcaAtualizada = marcaService.update(id, marcaDetails);
            return ResponseEntity.ok(marcaAtualizada);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("erro", "Erro ao atualizar marca"));
        }
    }
    
    // DELETE - Excluir marca (físico)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirMarca(@PathVariable Long id) {
        try {
            marcaService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // DELETE - Desativar marca (soft delete)
    @DeleteMapping("/{id}/desativar")
    public ResponseEntity<Void> desativarMarca(@PathVariable Long id) {
        try {
            marcaService.softDelete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // POST - Ativar marca
    @PostMapping("/{id}/ativar")
    public ResponseEntity<Marca> ativarMarca(@PathVariable Long id) {
        return marcaService.findById(id)
                .map(marca -> {
                    marca.setAtivo(true);
                    Marca marcaAtivada = marcaService.save(marca);
                    return ResponseEntity.ok(marcaAtivada);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
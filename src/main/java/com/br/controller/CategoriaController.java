package com.br.controller;

import com.br.model.Categoria;
import com.br.service.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/categorias")
@CrossOrigin(origins = "http://localhost:4200")
public class CategoriaController {
    
    @Autowired
    private CategoriaService categoriaService;
    
    @GetMapping
    public List<Categoria> getAllActive() {
        return categoriaService.findActive();
    }
    
    @GetMapping("/custom")
    public List<Categoria> getCustom() {
        return categoriaService.findCustom();
    }
    
    @GetMapping("/system")
    public List<Categoria> getSystem() {
        return categoriaService.findSystem();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Categoria> getById(@PathVariable Long id) {
        return categoriaService.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public Categoria create(@RequestBody Categoria categoria) {
        categoria.setTipoSistema(false); // Sempre personalizada via API
        return categoriaService.save(categoria);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Categoria> update(@PathVariable Long id, 
                                           @RequestBody Categoria categoria) {
        try {
            Categoria updated = categoriaService.update(id, categoria);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        categoriaService.softDelete(id);
        return ResponseEntity.ok().build();
    }
}
// UsuarioController.java
package com.br.controller;

import com.br.model.Usuario;
import com.br.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "http://localhost:4200")
public class UsuarioController {
    
    @Autowired
    private UsuarioService usuarioService;
    
    // ============ CRUD BÁSICO ============
    
    @PostMapping
    public ResponseEntity<?> criarUsuario(@RequestBody Map<String, Object> requestBody) {
        try {
            System.out.println("=== CRIANDO USUÁRIO ===");
            for (Map.Entry<String, Object> entry : requestBody.entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue() + 
                                 " (tipo: " + (entry.getValue() != null ? entry.getValue().getClass().getSimpleName() : "null") + ")");
            }
            
            // Extrai campos obrigatórios
            String nome = (String) requestBody.get("nome");
            String email = (String) requestBody.get("email");
            
            if (nome == null || nome.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("erro", "Nome é obrigatório"));
            }
            
            if (email == null || email.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("erro", "Email é obrigatório"));
            }
            
            // Cria usuário
            Usuario usuario = new Usuario();
            usuario.setNome(nome);
            usuario.setEmail(email);
            
            // Campos opcionais
            if (requestBody.containsKey("matricula")) {
                usuario.setMatricula((String) requestBody.get("matricula"));
            }
            
            if (requestBody.containsKey("departamento")) {
                usuario.setDepartamento((String) requestBody.get("departamento"));
            }
            
            if (requestBody.containsKey("cargo")) {
                usuario.setCargo((String) requestBody.get("cargo"));
            }
            
            if (requestBody.containsKey("telefone")) {
                usuario.setTelefone((String) requestBody.get("telefone"));
            }
            
            // Ativo (default true)
            Object ativoObj = requestBody.get("ativo");
            if (ativoObj instanceof Boolean) {
                usuario.setAtivo((Boolean) ativoObj);
            } else {
                usuario.setAtivo(true);
            }
            
            // Valida e salva
            usuarioService.validarUsuario(usuario);
            Usuario novoUsuario = usuarioService.salvarUsuario(usuario);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(novoUsuario);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("erro", "Erro interno ao criar usuário"));
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarUsuario(
            @PathVariable Long id, 
            @RequestBody Map<String, Object> requestBody) {
        try {
            System.out.println("=== ATUALIZANDO USUÁRIO " + id + " ===");
            System.out.println("Dados: " + requestBody);
            
            // Busca usuário existente
            Usuario usuarioExistente = usuarioService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
            
            // Atualiza campos permitidos
            if (requestBody.containsKey("nome")) {
                usuarioExistente.setNome((String) requestBody.get("nome"));
            }
            
            if (requestBody.containsKey("email")) {
                String novoEmail = (String) requestBody.get("email");
                // Verifica se email foi alterado
                if (!usuarioExistente.getEmail().equals(novoEmail)) {
                    // Valida duplicidade
                    if (usuarioService.existeEmail(novoEmail)) {
                        return ResponseEntity.badRequest()
                            .body(Map.of("erro", "Email já existe: " + novoEmail));
                    }
                    usuarioExistente.setEmail(novoEmail);
                }
            }
            
            if (requestBody.containsKey("matricula")) {
                String novaMatricula = (String) requestBody.get("matricula");
                // Verifica se matrícula foi alterada
                if (novaMatricula != null && 
                    !novaMatricula.equals(usuarioExistente.getMatricula())) {
                    // Valida duplicidade
                    if (usuarioService.existeMatricula(novaMatricula)) {
                        return ResponseEntity.badRequest()
                            .body(Map.of("erro", "Matrícula já existe: " + novaMatricula));
                    }
                    usuarioExistente.setMatricula(novaMatricula);
                }
            }
            
            if (requestBody.containsKey("departamento")) {
                usuarioExistente.setDepartamento((String) requestBody.get("departamento"));
            }
            
            if (requestBody.containsKey("cargo")) {
                usuarioExistente.setCargo((String) requestBody.get("cargo"));
            }
            
            if (requestBody.containsKey("telefone")) {
                usuarioExistente.setTelefone((String) requestBody.get("telefone"));
            }
            
            if (requestBody.containsKey("ativo")) {
                Object ativoObj = requestBody.get("ativo");
                if (ativoObj instanceof Boolean) {
                    usuarioExistente.setAtivo((Boolean) ativoObj);
                }
            }
            
            // Atualiza usuário
            Usuario usuarioAtualizado = usuarioService.atualizarUsuario(id, usuarioExistente);
            return ResponseEntity.ok(usuarioAtualizado);
            
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("não encontrado")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("erro", "Erro interno ao atualizar usuário"));
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable Long id) {
        Optional<Usuario> usuarioOpt = usuarioService.buscarPorId(id);
        return usuarioOpt.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/email/{email}")
    public ResponseEntity<Usuario> buscarPorEmail(@PathVariable String email) {
        Optional<Usuario> usuarioOpt = usuarioService.buscarPorEmail(email);
        return usuarioOpt.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/matricula/{matricula}")
    public ResponseEntity<Usuario> buscarPorMatricula(@PathVariable String matricula) {
        Optional<Usuario> usuarioOpt = usuarioService.buscarPorMatricula(matricula);
        return usuarioOpt.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping
    public ResponseEntity<List<Usuario>> listarTodos() {
        List<Usuario> usuarios = usuarioService.listarTodos();
        return ResponseEntity.ok(usuarios);
    }
    
    @GetMapping("/ativos")
    public ResponseEntity<List<Usuario>> listarAtivos() {
        List<Usuario> usuarios = usuarioService.listarAtivos();
        return ResponseEntity.ok(usuarios);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desativarUsuario(@PathVariable Long id) {
        try {
            usuarioService.desativarUsuario(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PatchMapping("/{id}/reativar")
    public ResponseEntity<Void> reativarUsuario(@PathVariable Long id) {
        try {
            usuarioService.reativarUsuario(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // ============ CONSULTAS POR ATRIBUTOS ============
    
    @GetMapping("/departamento/{departamento}")
    public ResponseEntity<List<Usuario>> buscarPorDepartamento(@PathVariable String departamento) {
        try {
            List<Usuario> usuarios = usuarioService.buscarPorDepartamento(departamento);
            return ResponseEntity.ok(usuarios);
        } catch (Exception e) {
            return ResponseEntity.ok(List.of());
        }
    }
    
    @GetMapping("/cargo/{cargo}")
    public ResponseEntity<List<Usuario>> buscarPorCargo(@PathVariable String cargo) {
        try {
            List<Usuario> usuarios = usuarioService.buscarPorCargo(cargo);
            return ResponseEntity.ok(usuarios);
        } catch (Exception e) {
            return ResponseEntity.ok(List.of());
        }
    }
    
    // ============ BUSCA AVANÇADA ============
    
    @GetMapping("/buscar")
    public ResponseEntity<List<Usuario>> buscarPorTermo(@RequestParam String termo) {
        try {
            List<Usuario> usuarios = usuarioService.buscarPorTermo(termo);
            return ResponseEntity.ok(usuarios);
        } catch (Exception e) {
            return ResponseEntity.ok(List.of());
        }
    }
    
    // ============ VALIDAÇÕES ============
    
    @GetMapping("/verificar/email/{email}")
    public ResponseEntity<Boolean> verificarEmailExistente(@PathVariable String email) {
        boolean existe = usuarioService.existeEmail(email);
        return ResponseEntity.ok(existe);
    }
    
    @GetMapping("/verificar/matricula/{matricula}")
    public ResponseEntity<Boolean> verificarMatriculaExistente(@PathVariable String matricula) {
        boolean existe = usuarioService.existeMatricula(matricula);
        return ResponseEntity.ok(existe);
    }
    
    // ============ RELATÓRIOS ============
    
    @GetMapping("/relatorios/contagem")
    public ResponseEntity<Map<String, Integer>> obterContagemUsuarios() {
        try {
            Map<String, Integer> contagem = usuarioService.obterContagemTotalUsuarios();
            return ResponseEntity.ok(contagem);
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("total", 0));
        }
    }
    
    @GetMapping("/relatorios/departamento")
    public ResponseEntity<Map<String, Long>> obterContagemPorDepartamento() {
        try {
            Map<String, Long> contagem = usuarioService.contarPorDepartamento();
            return ResponseEntity.ok(contagem);
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of());
        }
    }
    
    // ============ MÉTODOS AUXILIARES (opcionais, seguindo o padrão Produto) ============
    
    private Long converterParaLong(Object obj, String campo) {
        if (obj == null) return null;
        
        try {
            if (obj instanceof Integer) {
                return ((Integer) obj).longValue();
            } else if (obj instanceof Long) {
                return (Long) obj;
            } else if (obj instanceof String) {
                return Long.parseLong((String) obj);
            } else if (obj instanceof Double) {
                return ((Double) obj).longValue();
            }
        } catch (NumberFormatException e) {
            System.out.println("Erro ao converter " + campo + ": " + obj + " (" + obj.getClass().getSimpleName() + ")");
        }
        return null;
    }
}
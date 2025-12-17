// UsuarioServiceImpl.java
package com.br.service;

import com.br.model.Usuario;
import com.br.repository.*;
import com.br.*;
import com.br.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UsuarioServiceImpl implements UsuarioService {
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    // ============ CRUD BÁSICO ============
    
    @Override
    @Transactional
    public Usuario salvarUsuario(Usuario usuario) {
        validarUsuario(usuario);
        
        // Garantir que novo usuário seja ativo por padrão
        if (usuario.getAtivo() == null) {
            usuario.setAtivo(true);
        }
        
        return usuarioRepository.save(usuario);
    }
    
    @Override
    @Transactional
    public Usuario atualizarUsuario(Long id, Usuario usuarioAtualizado) {
        Usuario usuarioExistente = usuarioRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado com ID: " + id));
        
        // Validar se email foi alterado e já existe em outro usuário
        if (!usuarioExistente.getEmail().equals(usuarioAtualizado.getEmail()) &&
            usuarioRepository.existsByEmail(usuarioAtualizado.getEmail())) {
            throw new IllegalArgumentException("Email já existe: " + usuarioAtualizado.getEmail());
        }
        
        // Validar se matrícula foi alterada e já existe
        if (usuarioAtualizado.getMatricula() != null &&
            !usuarioAtualizado.getMatricula().equals(usuarioExistente.getMatricula()) &&
            usuarioRepository.existsByMatricula(usuarioAtualizado.getMatricula())) {
            throw new IllegalArgumentException("Matrícula já existe: " + usuarioAtualizado.getMatricula());
        }
        
        // Atualizar campos permitidos
        usuarioExistente.setNome(usuarioAtualizado.getNome());
        usuarioExistente.setEmail(usuarioAtualizado.getEmail());
        usuarioExistente.setMatricula(usuarioAtualizado.getMatricula());
        usuarioExistente.setDepartamento(usuarioAtualizado.getDepartamento());
        usuarioExistente.setCargo(usuarioAtualizado.getCargo());
        usuarioExistente.setTelefone(usuarioAtualizado.getTelefone());
        
        return usuarioRepository.save(usuarioExistente);
    }
    
    @Override
    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }
    
    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }
    
    @Override
    public Optional<Usuario> buscarPorMatricula(String matricula) {
        return usuarioRepository.findByMatricula(matricula);
    }
    
    @Override
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }
    
    @Override
    public List<Usuario> listarAtivos() {
        return usuarioRepository.findByAtivoTrue();
    }
    
    @Override
    @Transactional
    public void desativarUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado com ID: " + id));
        
        usuario.setAtivo(false);
        usuarioRepository.save(usuario);
    }
    
    @Override
    @Transactional
    public void reativarUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado com ID: " + id));
        
        usuario.setAtivo(true);
        usuarioRepository.save(usuario);
    }
    
    // ============ CONSULTAS POR ATRIBUTOS ============
    
    @Override
    public List<Usuario> buscarPorDepartamento(String departamento) {
        return usuarioRepository.findByDepartamento(departamento);
    }
    
    @Override
    public List<Usuario> buscarPorCargo(String cargo) {
        // Implementação simples
        return usuarioRepository.findAll().stream()
            .filter(u -> u.getCargo() != null && u.getCargo().equalsIgnoreCase(cargo))
            .collect(Collectors.toList());
    }
    
    // ============ BUSCA AVANÇADA ============
    
    @Override
    public List<Usuario> buscarPorTermo(String termo) {
        if (termo == null || termo.trim().isEmpty()) {
            return listarAtivos();
        }
        
        String termoBusca = termo.trim().toLowerCase();
        return usuarioRepository.findAll().stream()
            .filter(u -> u.getAtivo())
            .filter(u -> 
                (u.getNome() != null && u.getNome().toLowerCase().contains(termoBusca)) ||
                (u.getEmail() != null && u.getEmail().toLowerCase().contains(termoBusca)) ||
                (u.getMatricula() != null && u.getMatricula().toLowerCase().contains(termoBusca))
            )
            .collect(Collectors.toList());
    }
    
    // ============ VALIDAÇÕES ============
    
    @Override
    public void validarUsuario(Usuario usuario) {
        if (usuario.getNome() == null || usuario.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do usuário é obrigatório");
        }
        
        if (usuario.getEmail() == null || usuario.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email do usuário é obrigatório");
        }
        
        // Validar formato de email básico
        if (!usuario.getEmail().contains("@")) {
            throw new IllegalArgumentException("Email inválido");
        }
        
        // Validar duplicidade (apenas para novos usuários)
        if (usuario.getId() == null) {
            if (usuarioRepository.existsByEmail(usuario.getEmail())) {
                throw new IllegalArgumentException("Email já existe: " + usuario.getEmail());
            }
            
            if (usuario.getMatricula() != null && 
                !usuario.getMatricula().isEmpty() &&
                usuarioRepository.existsByMatricula(usuario.getMatricula())) {
                throw new IllegalArgumentException("Matrícula já existe: " + usuario.getMatricula());
            }
        }
    }
    
    @Override
    public boolean existeEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }
    
    @Override
    public boolean existeMatricula(String matricula) {
        return usuarioRepository.existsByMatricula(matricula);
    }
    
    // ============ RELATÓRIOS E ESTATÍSTICAS ============
    
    @Override
    public Map<String, Long> contarPorDepartamento() {
        // ⭐⭐ USANDO O MÉTODO DO REPOSITORY QUE RETORNA List<Object[]> ⭐⭐
        List<Object[]> resultados = usuarioRepository.contarAtivosPorDepartamento();
        
        Map<String, Long> contagem = new HashMap<>();
        
        for (Object[] resultado : resultados) {
            String departamento = (String) resultado[0];
            Long quantidade = (Long) resultado[1];
            
            if (departamento == null) {
                departamento = "Sem Departamento";
            }
            
            contagem.put(departamento, quantidade);
        }
        
        return contagem;
    }
    
    @Override
    public Map<String, Integer> obterContagemTotalUsuarios() {
        List<Usuario> todosUsuarios = listarTodos();
        
        int totalUsuarios = todosUsuarios.size();
        int totalAtivos = (int) todosUsuarios.stream().filter(Usuario::getAtivo).count();
        int totalInativos = totalUsuarios - totalAtivos;
        
        Map<String, Integer> contagem = new HashMap<>();
        contagem.put("totalUsuarios", totalUsuarios);
        contagem.put("totalAtivos", totalAtivos);
        contagem.put("totalInativos", totalInativos);
        
        return contagem;
    }
}
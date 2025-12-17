package com.br.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.br.model.Usuario;

public interface UsuarioService {
	
    // ============ CRUD BÁSICO ============

	
	Usuario salvarUsuario(Usuario usuario);
	
	Usuario atualizarUsuario(Long id,Usuario usuario);
	
	Optional<Usuario>buscarPorId(Long id);
	
	Optional<Usuario>buscarPorEmail(String email);
	
	Optional<Usuario>buscarPorMatricula(String matricula);
	
	//Optional<Usuario>contarAtivosPorDepartamento(String departamento);

	
	List<Usuario>listarTodos();
	
	List<Usuario>listarAtivos();
	
	void desativarUsuario(Long id);
	
	void reativarUsuario(Long id);
    // ============ CONSULTAS POR ATRIBUTOS ============

	List<Usuario>buscarPorDepartamento(String despartamento);
	
	List<Usuario>buscarPorCargo(String cargo);
	
	// ============ BUSCA AVANÇADA ============
	
    // Busca geral (nome, email ou matrícula)
	List<Usuario>buscarPorTermo(String termo);
	
    // ============ VALIDAÇÕES ============

	void validarUsuario(Usuario usuario);
	
	boolean existeEmail(String email);
	
	boolean existeMatricula(String matricula);
	
    // ============ RELATÓRIOS E ESTATÍSTICAS ============
	
	Map<String, Long> contarPorDepartamento();
	Map<String, Integer>obterContagemTotalUsuarios();
	
	
}

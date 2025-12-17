package com.br.repository;

import com.br.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    // ⭐⭐ MÉTODOS BÁSICOS ⭐⭐
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByMatricula(String matricula);
    List<Usuario> findByAtivoTrue();
    List<Usuario> findByAtivoFalse();
    List<Usuario> findByDepartamento(String departamento);  // ⭐ CORRIGIDO: "departamento"
    Optional<Usuario> findByEmailAndAtivoTrue(String email);
    
    // ⭐⭐ MÉTODOS COM @Query ⭐⭐
    @Query("SELECT u FROM Usuario u WHERE u.departamento = :departamento AND u.ativo = true")
    List<Usuario> findByDepartamentoAndAtivoTrue(@Param("departamento") String departamento);
    
    @Query(value = "SELECT * FROM usuarios WHERE LOWER(nome) LIKE LOWER(CONCAT('%', :nome, '%'))", 
           nativeQuery = true)
    List<Usuario> buscarPorNomeSimilar(@Param("nome") String nome);
    
    // ⭐⭐ MÉTODOS DE BUSCA (Spring cria automaticamente) ⭐⭐
    List<Usuario> findByNomeContainingIgnoreCase(String nome);
    List<Usuario> findByEmailContainingIgnoreCase(String email);
    List<Usuario> findByMatriculaContainingIgnoreCase(String matricula);
    
    // ⭐⭐ RELATÓRIOS ⭐⭐
    @Query("SELECT u.departamento, COUNT(u) FROM Usuario u WHERE u.ativo = true GROUP BY u.departamento")
    List<Object[]> contarAtivosPorDepartamento();
    
    // ⭐⭐ VERIFICAÇÕES ⭐⭐
    boolean existsByEmail(String email);
    boolean existsByMatricula(String matricula);
    long countByAtivoTrue();
}
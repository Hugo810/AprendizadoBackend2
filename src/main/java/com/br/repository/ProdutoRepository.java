package com.br.repository;

import com.br.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    
    // ============ BUSCAS BÁSICAS ============
    Optional<Produto> findByCodigo(String codigo);
    Optional<Produto> findByNumeroSerie(String numeroSerie);
    List<Produto> findByAtivoTrue();
    List<Produto> findByNomeContainingIgnoreCase(String nome);
    List<Produto> findByCategoria(String categoria);
    List<Produto> findByMarca(String marca);
    List<Produto> findByLocalizacao(String localizacao);
    List<Produto> findByEstadoConservacao(String estadoConservacao);
    
    // ============ VERIFICAÇÕES ============
    boolean existsByCodigo(String codigo);
    boolean existsByNumeroSerie(String numeroSerie);
    
    // ============ CONSULTAS SIMPLES ============
    @Query("SELECT p FROM Produto p WHERE p.quantidadeDisponivel < :quantidadeMinima AND p.ativo = true")
    List<Produto> findProdutosComBaixaDisponibilidade(@Param("quantidadeMinima") Integer quantidadeMinima);
    
    @Query("SELECT p FROM Produto p WHERE p.quantidadeDisponivel = 0 AND p.ativo = true")
    List<Produto> findProdutosSemDisponibilidade();
    
    @Query("SELECT p FROM Produto p WHERE p.quantidadeDisponivel > 0 AND p.ativo = true ORDER BY p.nome")
    List<Produto> findProdutosDisponiveisParaEmprestimo();
    
    // ============ BUSCA GERAL ============
    @Query("SELECT p FROM Produto p WHERE " +
           "(LOWER(p.nome) LIKE LOWER(CONCAT('%', :termo, '%')) OR " +
           "LOWER(p.codigo) LIKE LOWER(CONCAT('%', :termo, '%')) OR " +
           "LOWER(p.numeroSerie) LIKE LOWER(CONCAT('%', :termo, '%'))) AND " +
           "p.ativo = true")
    List<Produto> buscarPorTermoGeral(@Param("termo") String termo);
    
    // ============ FILTROS MÚLTIPLOS ============
    @Query("SELECT p FROM Produto p WHERE " +
           "(:categoria IS NULL OR p.categoria = :categoria) AND " +
           "(:marca IS NULL OR p.marca = :marca) AND " +
           "(:estadoConservacao IS NULL OR p.estadoConservacao = :estadoConservacao) AND " +
           "p.ativo = true")
    List<Produto> findProdutosPorFiltros(
            @Param("categoria") String categoria,
            @Param("marca") String marca,
            @Param("estadoConservacao") String estadoConservacao);
    
    // ============ ESTATÍSTICAS ============
    @Query("SELECT p.categoria, COUNT(p) FROM Produto p WHERE p.ativo = true GROUP BY p.categoria")
    List<Object[]> contarProdutosPorCategoria();
}
package com.br.service;

import com.br.model.Produto;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ProdutoService {

	// ============ CRUD BÁSICO ============

	// Salvar novo produto
	Produto salvarProduto(Produto produto);

	// Atualizar produto existente
	Produto atualizarProduto(Long id, Produto produto);

	// Buscar por ID
	Optional<Produto> buscarPorId(Long id);

	// Buscar por código patrimonial/SN
	Optional<Produto> buscarPorCodigo(String codigo);

	// Buscar por número de série
	Optional<Produto> buscarPorNumeroSerie(String numeroSerie);

	// Listar todos os produtos
	List<Produto> listarTodos();

	// Listar produtos ativos
	List<Produto> listarAtivos();

	// Desativar produto (exclusão lógica)
	void desativarProduto(Long id);

	// ============ CONTROLE DE DISPONIBILIDADE ============

	// Registrar empréstimo (diminui quantidade disponível)
	Produto registrarEmprestimo(Long id, Integer quantidade);

	// Registrar devolução (aumenta quantidade disponível)
	Produto registrarDevolucao(Long id, Integer quantidade);

	// Verificar disponibilidade para empréstimo
	boolean verificarDisponibilidade(Long id, Integer quantidadeRequerida);

	// Listar produtos disponíveis para empréstimo
	List<Produto> listarDisponiveisParaEmprestimo();

	// Listar produtos sem disponibilidade (todos emprestados)
	List<Produto> listarSemDisponibilidade();

	// ============ CONSULTAS POR ATRIBUTOS ============

	// Buscar por categoria
	List<Produto> buscarPorCategoria(String categoria);

	// Buscar por marca
	List<Produto> buscarPorMarca(String marca);

	// Buscar por modelo
	List<Produto> buscarPorModelo(String modelo);

	// Buscar por localização
	List<Produto> buscarPorLocalizacao(String localizacao);

	// Buscar por estado de conservação
	List<Produto> buscarPorEstadoConservacao(String estadoConservacao);

	// ============ BUSCA AVANÇADA ============

	// Busca geral (nome, código ou número de série)
	List<Produto> buscarPorTermo(String termo);

	// Buscar por múltiplos filtros
	List<Produto> buscarPorFiltros(String categoria, String marca, String estadoConservacao);

	// ============ RELATÓRIOS E ESTATÍSTICAS ============

	// Obter estatísticas por categoria
	Map<String, Long> obterEstatisticasPorCategoria();

	// Listar produtos com baixa disponibilidade
	List<Produto> listarComBaixaDisponibilidade(Integer quantidadeMinima);

	// Obter contagem total de itens
	Map<String, Integer> obterContagemTotalItens();

	// Verificar duplicidade de código ou número de série
	boolean existeCodigo(String codigo);

	boolean existeNumeroSerie(String numeroSerie);

	// ============ VALIDAÇÕES ============

	// Validar dados do produto antes de salvar
	void validarProduto(Produto produto);

	// Validar quantidade para empréstimo
	void validarQuantidadeEmprestimo(Long id, Integer quantidade);
}
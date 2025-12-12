package com.br.service;

import com.br.model.Produto;
import com.br.repository.ProdutoRepository;
import com.br.service.ProdutoService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProdutoServiceImpl implements ProdutoService {
    
    @Autowired
    private ProdutoRepository produtoRepository;
    
    // ============ CRUD BÁSICO ============
    
    @Override
    @Transactional
    public Produto salvarProduto(Produto produto) {
        validarProduto(produto);
        
        // Garantir que quantidade disponível = quantidade total inicialmente
        if (produto.getQuantidadeDisponivel() == null) {
            produto.setQuantidadeDisponivel(produto.getQuantidadeTotal());
        }
        
        return produtoRepository.save(produto);
    }
    
    @Override
    @Transactional
    public Produto atualizarProduto(Long id, Produto produtoAtualizado) {
        Produto produtoExistente = produtoRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado com ID: " + id));
        
        // Validar se código foi alterado e já existe em outro produto
        if (!produtoExistente.getCodigo().equals(produtoAtualizado.getCodigo()) &&
            produtoRepository.existsByCodigo(produtoAtualizado.getCodigo())) {
            throw new IllegalArgumentException("Código já existe: " + produtoAtualizado.getCodigo());
        }
        
        // Validar se número de série foi alterado e já existe
        if (produtoAtualizado.getNumeroSerie() != null &&
            !produtoAtualizado.getNumeroSerie().equals(produtoExistente.getNumeroSerie()) &&
            produtoRepository.existsByNumeroSerie(produtoAtualizado.getNumeroSerie())) {
            throw new IllegalArgumentException("Número de série já existe: " + produtoAtualizado.getNumeroSerie());
        }
        
        // Atualizar campos permitidos
        produtoExistente.setNome(produtoAtualizado.getNome());
        produtoExistente.setDescricao(produtoAtualizado.getDescricao());
        produtoExistente.setCodigo(produtoAtualizado.getCodigo());
        produtoExistente.setNumeroSerie(produtoAtualizado.getNumeroSerie());
        produtoExistente.setCategoria(produtoAtualizado.getCategoria());
        produtoExistente.setMarca(produtoAtualizado.getMarca());
        produtoExistente.setModelo(produtoAtualizado.getModelo());
        produtoExistente.setQuantidadeTotal(produtoAtualizado.getQuantidadeTotal());
        produtoExistente.setLocalizacao(produtoAtualizado.getLocalizacao());
        produtoExistente.setEstadoConservacao(produtoAtualizado.getEstadoConservacao());
        produtoExistente.setDataAquisicao(produtoAtualizado.getDataAquisicao());
        produtoExistente.setGarantiaAte(produtoAtualizado.getGarantiaAte());
        produtoExistente.setObservacoes(produtoAtualizado.getObservacoes());
        
        return produtoRepository.save(produtoExistente);
    }
    
    @Override
    public Optional<Produto> buscarPorId(Long id) {
        return produtoRepository.findById(id);
    }
    
    @Override
    public Optional<Produto> buscarPorCodigo(String codigo) {
        return produtoRepository.findByCodigo(codigo);
    }
    
    @Override
    public Optional<Produto> buscarPorNumeroSerie(String numeroSerie) {
        return produtoRepository.findByNumeroSerie(numeroSerie);
    }
    
    @Override
    public List<Produto> listarTodos() {
        return produtoRepository.findAll();
    }
    
    @Override
    public List<Produto> listarAtivos() {
        return produtoRepository.findByAtivoTrue();
    }
    
    @Override
    @Transactional
    public void desativarProduto(Long id) {
        Produto produto = produtoRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado com ID: " + id));
        
        produto.setAtivo(false);
        produtoRepository.save(produto);
    }
    
    // ============ CONTROLE DE DISPONIBILIDADE ============
    
    @Override
    @Transactional
    public Produto registrarEmprestimo(Long id, Integer quantidade) {
        validarQuantidadeEmprestimo(id, quantidade);
        
        Produto produto = produtoRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado com ID: " + id));
        
        int novaDisponibilidade = produto.getQuantidadeDisponivel() - quantidade;
        
        if (novaDisponibilidade < 0) {
            throw new IllegalArgumentException(
                "Disponibilidade insuficiente. Disponível: " + 
                produto.getQuantidadeDisponivel() + 
                ", Solicitado: " + quantidade
            );
        }
        
        produto.setQuantidadeDisponivel(novaDisponibilidade);
        return produtoRepository.save(produto);
    }
    
    @Override
    @Transactional
    public Produto registrarDevolucao(Long id, Integer quantidade) {
        Produto produto = produtoRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado com ID: " + id));
        
        int novaDisponibilidade = produto.getQuantidadeDisponivel() + quantidade;
        
        if (novaDisponibilidade > produto.getQuantidadeTotal()) {
            throw new IllegalArgumentException(
                "Devolução excede quantidade total. Total: " + 
                produto.getQuantidadeTotal() + 
                ", Após devolução: " + novaDisponibilidade
            );
        }
        
        produto.setQuantidadeDisponivel(novaDisponibilidade);
        return produtoRepository.save(produto);
    }
    
    @Override
    public boolean verificarDisponibilidade(Long id, Integer quantidadeRequerida) {
        Optional<Produto> produtoOpt = produtoRepository.findById(id);
        
        if (produtoOpt.isEmpty()) {
            return false;
        }
        
        Produto produto = produtoOpt.get();
        return produto.getAtivo() && 
               produto.getQuantidadeDisponivel() >= quantidadeRequerida;
    }
    
    @Override
    public List<Produto> listarDisponiveisParaEmprestimo() {
        return produtoRepository.findProdutosDisponiveisParaEmprestimo();
    }
    
    @Override
    public List<Produto> listarSemDisponibilidade() {
        return produtoRepository.findProdutosSemDisponibilidade();
    }
    
    // ============ CONSULTAS POR ATRIBUTOS ============
    
    @Override
    public List<Produto> buscarPorCategoria(String categoria) {
        return produtoRepository.findByCategoria(categoria);
    }
    
    @Override
    public List<Produto> buscarPorMarca(String marca) {
        return produtoRepository.findByMarca(marca);
    }
    
    @Override
    public List<Produto> buscarPorModelo(String modelo) {
        // Implementação simples - pode ser melhorada com repository method
        return produtoRepository.findAll().stream()
            .filter(p -> p.getModelo() != null && p.getModelo().equalsIgnoreCase(modelo))
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Produto> buscarPorLocalizacao(String localizacao) {
        return produtoRepository.findByLocalizacao(localizacao);
    }
    
    @Override
    public List<Produto> buscarPorEstadoConservacao(String estadoConservacao) {
        return produtoRepository.findByEstadoConservacao(estadoConservacao);
    }
    
    // ============ BUSCA AVANÇADA ============
    
    @Override
    public List<Produto> buscarPorTermo(String termo) {
        if (termo == null || termo.trim().isEmpty()) {
            return listarAtivos();
        }
        return produtoRepository.buscarPorTermoGeral(termo.trim());
    }
    
    @Override
    public List<Produto> buscarPorFiltros(String categoria, String marca, String estadoConservacao) {
        return produtoRepository.findProdutosPorFiltros(categoria, marca, estadoConservacao);
    }
    
    // ============ CONTROLE DE GARANTIA ============
    
    
    
    
    // ============ RELATÓRIOS E ESTATÍSTICAS ============
    
    @Override
    public Map<String, Long> obterEstatisticasPorCategoria() {
        List<Object[]> resultados = produtoRepository.contarProdutosPorCategoria();
        Map<String, Long> estatisticas = new HashMap<>();
        
        for (Object[] resultado : resultados) {
            String categoria = (String) resultado[0];
            Long quantidade = (Long) resultado[1];
            estatisticas.put(categoria, quantidade);
        }
        
        return estatisticas;
    }
    
    @Override
    public List<Produto> listarComBaixaDisponibilidade(Integer quantidadeMinima) {
        return produtoRepository.findProdutosComBaixaDisponibilidade(quantidadeMinima);
    }
    
    @Override
    public Map<String, Integer> obterContagemTotalItens() {
        List<Produto> produtosAtivos = listarAtivos();
        
        int totalItens = 0;
        int totalDisponiveis = 0;
        int totalEmprestados = 0;
        
        for (Produto produto : produtosAtivos) {
            totalItens += produto.getQuantidadeTotal();
            totalDisponiveis += produto.getQuantidadeDisponivel();
            totalEmprestados += (produto.getQuantidadeTotal() - produto.getQuantidadeDisponivel());
        }
        
        Map<String, Integer> contagem = new HashMap<>();
        contagem.put("totalItens", totalItens);
        contagem.put("totalDisponiveis", totalDisponiveis);
        contagem.put("totalEmprestados", totalEmprestados);
        
        return contagem;
    }
    
    @Override
    public boolean existeCodigo(String codigo) {
        return produtoRepository.existsByCodigo(codigo);
    }
    
    @Override
    public boolean existeNumeroSerie(String numeroSerie) {
        return produtoRepository.existsByNumeroSerie(numeroSerie);
    }
    
    // ============ VALIDAÇÕES ============
    
    @Override
    public void validarProduto(Produto produto) {
        if (produto.getNome() == null || produto.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do produto é obrigatório");
        }
        
        if (produto.getCodigo() == null || produto.getCodigo().trim().isEmpty()) {
            throw new IllegalArgumentException("Código do produto é obrigatório");
        }
        
        if (produto.getCategoria() == null || produto.getCategoria().trim().isEmpty()) {
            throw new IllegalArgumentException("Categoria do produto é obrigatória");
        }
        
        if (produto.getQuantidadeTotal() == null || produto.getQuantidadeTotal() < 0) {
            throw new IllegalArgumentException("Quantidade total deve ser maior ou igual a zero");
        }
        
        // Validar duplicidade
        if (produto.getId() == null) {
            if (produtoRepository.existsByCodigo(produto.getCodigo())) {
                throw new IllegalArgumentException("Código já existe: " + produto.getCodigo());
            }
            
            if (produto.getNumeroSerie() != null && 
                produtoRepository.existsByNumeroSerie(produto.getNumeroSerie())) {
                throw new IllegalArgumentException("Número de série já existe: " + produto.getNumeroSerie());
            }
        }
    }
    
    @Override
    public void validarQuantidadeEmprestimo(Long id, Integer quantidade) {
        if (quantidade == null || quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        }
        
        Produto produto = produtoRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado"));
        
        if (!produto.getAtivo()) {
            throw new IllegalArgumentException("Produto está inativo");
        }
    }
}
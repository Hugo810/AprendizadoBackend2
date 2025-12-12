package com.br.controller;

import com.br.model.Produto;
import com.br.service.ProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/produtos")
@CrossOrigin(origins = "http://localhost:4200")
public class ProdutoController {
    
    @Autowired
    private ProdutoService produtoService;
    
    // ============ CRUD BÁSICO ============
    
    // POST - Criar novo produto
    @PostMapping
    public ResponseEntity<?> criarProduto(@RequestBody Produto produto) {
        try {
            produtoService.validarProduto(produto);
            Produto novoProduto = produtoService.salvarProduto(produto);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoProduto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "erro", e.getMessage(),
                "tipo", "VALIDACAO"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("erro", "Erro interno ao criar produto"));
        }
    }
    
    // PUT - Atualizar produto
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarProduto(
            @PathVariable Long id, 
            @RequestBody Produto produto) {
        try {
            Produto produtoAtualizado = produtoService.atualizarProduto(id, produto);
            return ResponseEntity.ok(produtoAtualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // GET - Buscar por ID
    @GetMapping("/{id}")
    public ResponseEntity<Produto> buscarPorId(@PathVariable Long id) {
        Optional<Produto> produtoOpt = produtoService.buscarPorId(id);
        return produtoOpt.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // GET - Buscar por código patrimonial
    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<Produto> buscarPorCodigo(@PathVariable String codigo) {
        Optional<Produto> produtoOpt = produtoService.buscarPorCodigo(codigo);
        return produtoOpt.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // GET - Buscar por número de série
    @GetMapping("/numero-serie/{numeroSerie}")
    public ResponseEntity<Produto> buscarPorNumeroSerie(@PathVariable String numeroSerie) {
        Optional<Produto> produtoOpt = produtoService.buscarPorNumeroSerie(numeroSerie);
        return produtoOpt.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // GET - Listar todos
    @GetMapping
    public ResponseEntity<List<Produto>> listarTodos() {
        List<Produto> produtos = produtoService.listarTodos();
        return ResponseEntity.ok(produtos);
    }
    
    // GET - Listar ativos
    @GetMapping("/ativos")
    public ResponseEntity<List<Produto>> listarAtivos() {
        List<Produto> produtos = produtoService.listarAtivos();
        return ResponseEntity.ok(produtos);
    }
    
    // DELETE - Desativar produto
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desativarProduto(@PathVariable Long id) {
        try {
            produtoService.desativarProduto(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // ============ CONTROLE DE DISPONIBILIDADE ============
    
    // POST - Registrar empréstimo
    @PostMapping("/{id}/emprestimo")
    public ResponseEntity<?> registrarEmprestimo(
            @PathVariable Long id,
            @RequestParam Integer quantidade) {
        try {
            produtoService.validarQuantidadeEmprestimo(id, quantidade);
            Produto produto = produtoService.registrarEmprestimo(id, quantidade);
            return ResponseEntity.ok(produto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // POST - Registrar devolução
    @PostMapping("/{id}/devolucao")
    public ResponseEntity<?> registrarDevolucao(
            @PathVariable Long id,
            @RequestParam Integer quantidade) {
        try {
            Produto produto = produtoService.registrarDevolucao(id, quantidade);
            return ResponseEntity.ok(produto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // GET - Verificar disponibilidade
    @GetMapping("/{id}/disponivel")
    public ResponseEntity<Boolean> verificarDisponibilidade(
            @PathVariable Long id,
            @RequestParam Integer quantidade) {
        boolean disponivel = produtoService.verificarDisponibilidade(id, quantidade);
        return ResponseEntity.ok(disponivel);
    }
    
    // GET - Listar disponíveis para empréstimo
    @GetMapping("/disponiveis")
    public ResponseEntity<List<Produto>> listarDisponiveis() {
        List<Produto> produtos = produtoService.listarDisponiveisParaEmprestimo();
        return ResponseEntity.ok(produtos);
    }
    
    // GET - Listar sem disponibilidade
    @GetMapping("/indisponiveis")
    public ResponseEntity<List<Produto>> listarIndisponiveis() {
        List<Produto> produtos = produtoService.listarSemDisponibilidade();
        return ResponseEntity.ok(produtos);
    }
    
    // ============ CONSULTAS POR ATRIBUTOS ============
    
    // GET - Buscar por categoria
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<Produto>> buscarPorCategoria(@PathVariable String categoria) {
        List<Produto> produtos = produtoService.buscarPorCategoria(categoria);
        return ResponseEntity.ok(produtos);
    }
    
    // GET - Buscar por marca
    @GetMapping("/marca/{marca}")
    public ResponseEntity<List<Produto>> buscarPorMarca(@PathVariable String marca) {
        List<Produto> produtos = produtoService.buscarPorMarca(marca);
        return ResponseEntity.ok(produtos);
    }
    
    // GET - Buscar por localização
    @GetMapping("/localizacao/{localizacao}")
    public ResponseEntity<List<Produto>> buscarPorLocalizacao(@PathVariable String localizacao) {
        List<Produto> produtos = produtoService.buscarPorLocalizacao(localizacao);
        return ResponseEntity.ok(produtos);
    }
    
    // GET - Buscar por estado de conservação
    @GetMapping("/estado/{estadoConservacao}")
    public ResponseEntity<List<Produto>> buscarPorEstadoConservacao(@PathVariable String estadoConservacao) {
        List<Produto> produtos = produtoService.buscarPorEstadoConservacao(estadoConservacao);
        return ResponseEntity.ok(produtos);
    }
    
    // ============ BUSCA AVANÇADA ============
    
    // GET - Buscar por termo (nome, código ou número de série)
    @GetMapping("/buscar")
    public ResponseEntity<List<Produto>> buscarPorTermo(@RequestParam String termo) {
        List<Produto> produtos = produtoService.buscarPorTermo(termo);
        return ResponseEntity.ok(produtos);
    }
    
    // GET - Buscar por múltiplos filtros
    @GetMapping("/filtros")
    public ResponseEntity<List<Produto>> buscarPorFiltros(
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String marca,
            @RequestParam(required = false) String estadoConservacao) {
        List<Produto> produtos = produtoService.buscarPorFiltros(categoria, marca, estadoConservacao);
        return ResponseEntity.ok(produtos);
    }
    
    // ============ CONTROLE DE GARANTIA ============
    
    
  
    
    // ============ RELATÓRIOS E ESTATÍSTICAS ============
    
    // GET - Estatísticas por categoria
    @GetMapping("/estatisticas/categoria")
    public ResponseEntity<Map<String, Long>> obterEstatisticasCategoria() {
        Map<String, Long> estatisticas = produtoService.obterEstatisticasPorCategoria();
        return ResponseEntity.ok(estatisticas);
    }
    
    // GET - Produtos com baixa disponibilidade
    @GetMapping("/baixa-disponibilidade")
    public ResponseEntity<List<Produto>> listarBaixaDisponibilidade(
            @RequestParam(defaultValue = "2") Integer quantidadeMinima) {
        List<Produto> produtos = produtoService.listarComBaixaDisponibilidade(quantidadeMinima);
        return ResponseEntity.ok(produtos);
    }
    
    // GET - Contagem total de itens
    @GetMapping("/contagem-itens")
    public ResponseEntity<Map<String, Integer>> obterContagemItens() {
        Map<String, Integer> contagem = produtoService.obterContagemTotalItens();
        return ResponseEntity.ok(contagem);
    }
    
    // GET - Verificar se código existe
    @GetMapping("/verificar/codigo/{codigo}")
    public ResponseEntity<Boolean> verificarCodigoExistente(@PathVariable String codigo) {
        boolean existe = produtoService.existeCodigo(codigo);
        return ResponseEntity.ok(existe);
    }
    
    // GET - Verificar se número de série existe
    @GetMapping("/verificar/numero-serie/{numeroSerie}")
    public ResponseEntity<Boolean> verificarNumeroSerieExistente(@PathVariable String numeroSerie) {
        boolean existe = produtoService.existeNumeroSerie(numeroSerie);
        return ResponseEntity.ok(existe);
    }
}
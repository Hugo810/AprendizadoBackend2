package com.br.controller;

import com.br.model.Produto;
import com.br.model.Categoria;
import com.br.service.ProdutoService;
import com.br.service.CategoriaService;
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
    
    @Autowired
    private CategoriaService categoriaService;
    
    // ============ CRUD BÁSICO ============
    
    @PostMapping
    public ResponseEntity<?> criarProduto(@RequestBody Map<String, Object> requestBody) {
        try {
            System.out.println("=== DADOS RECEBIDOS ===");
            for (Map.Entry<String, Object> entry : requestBody.entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue() + 
                                 " (tipo: " + (entry.getValue() != null ? entry.getValue().getClass().getSimpleName() : "null") + ")");
            }
            
            String nome = (String) requestBody.get("nome");
            String codigo = (String) requestBody.get("codigo");
            Object categoriaIdObj = requestBody.get("categoriaId");
            
            if (categoriaIdObj == null) {
                return ResponseEntity.badRequest().body(Map.of("erro", "categoriaId é obrigatório"));
            }
            
            Long categoriaId = null;
            if (categoriaIdObj instanceof Integer) {
                categoriaId = ((Integer) categoriaIdObj).longValue();
            } else if (categoriaIdObj instanceof Long) {
                categoriaId = (Long) categoriaIdObj;
            } else if (categoriaIdObj instanceof String) {
                try {
                    categoriaId = Long.parseLong((String) categoriaIdObj);
                } catch (NumberFormatException e) {
                    return ResponseEntity.badRequest().body(Map.of("erro", "categoriaId inválido"));
                }
            }
            
            Categoria categoria = categoriaService.findById(categoriaId)
                .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada"));
            
            Produto produto = new Produto();
            produto.setNome(nome);
            produto.setDescricao((String) requestBody.get("descricao"));
            produto.setCodigo(codigo);
            produto.setNumeroSerie((String) requestBody.get("numeroSerie"));
            produto.setCategoria(categoria);
            produto.setMarca((String) requestBody.get("marca"));
            produto.setModelo((String) requestBody.get("modelo"));
            
            Object qtdTotalObj = requestBody.get("quantidadeTotal");
            if (qtdTotalObj instanceof Integer) {
                produto.setQuantidadeTotal((Integer) qtdTotalObj);
                produto.setQuantidadeDisponivel((Integer) qtdTotalObj);
            } else if (qtdTotalObj instanceof String) {
                try {
                    int quantidade = Integer.parseInt((String) qtdTotalObj);
                    produto.setQuantidadeTotal(quantidade);
                    produto.setQuantidadeDisponivel(quantidade);
                } catch (NumberFormatException e) {
                    produto.setQuantidadeTotal(0);
                    produto.setQuantidadeDisponivel(0);
                }
            } else {
                produto.setQuantidadeTotal(0);
                produto.setQuantidadeDisponivel(0);
            }
            
            produto.setLocalizacao((String) requestBody.get("localizacao"));
            produto.setEstadoConservacao((String) requestBody.get("estadoConservacao"));
            produto.setObservacoes((String) requestBody.get("observacoes"));
            
            Object ativoObj = requestBody.get("ativo");
            if (ativoObj instanceof Boolean) {
                produto.setAtivo((Boolean) ativoObj);
            } else {
                produto.setAtivo(true);
            }
            
            // CORRIGIDO: Usar salvarProduto() em vez de save()
            produtoService.validarProduto(produto);
            Produto novoProduto = produtoService.salvarProduto(produto);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(novoProduto);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("erro", e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarProduto(
            @PathVariable Long id, 
            @RequestBody Map<String, Object> requestBody) {
        try {
            System.out.println("=== ATUALIZANDO PRODUTO " + id + " ===");
            
            Object categoriaIdObj = requestBody.get("categoriaId");
            Long categoriaId = null;
            
            if (categoriaIdObj != null) {
                if (categoriaIdObj instanceof Integer) {
                    categoriaId = ((Integer) categoriaIdObj).longValue();
                } else if (categoriaIdObj instanceof Long) {
                    categoriaId = (Long) categoriaIdObj;
                } else if (categoriaIdObj instanceof String) {
                    try {
                        categoriaId = Long.parseLong((String) categoriaIdObj);
                    } catch (NumberFormatException e) {
                        return ResponseEntity.badRequest().body(Map.of("erro", "categoriaId inválido"));
                    }
                }
            }
            
            // CORRIGIDO: Usar buscarPorId() em vez de findById()
            Produto produtoExistente = produtoService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado"));
            
            if (categoriaId != null) {
                Categoria categoria = categoriaService.findById(categoriaId)
                    .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada"));
                produtoExistente.setCategoria(categoria);
            }
            
            if (requestBody.containsKey("nome")) {
                produtoExistente.setNome((String) requestBody.get("nome"));
            }
            if (requestBody.containsKey("descricao")) {
                produtoExistente.setDescricao((String) requestBody.get("descricao"));
            }
            if (requestBody.containsKey("codigo")) {
                produtoExistente.setCodigo((String) requestBody.get("codigo"));
            }
            if (requestBody.containsKey("numeroSerie")) {
                produtoExistente.setNumeroSerie((String) requestBody.get("numeroSerie"));
            }
            if (requestBody.containsKey("marca")) {
                produtoExistente.setMarca((String) requestBody.get("marca"));
            }
            if (requestBody.containsKey("modelo")) {
                produtoExistente.setModelo((String) requestBody.get("modelo"));
            }
            if (requestBody.containsKey("quantidadeTotal")) {
                Object qtdObj = requestBody.get("quantidadeTotal");
                if (qtdObj instanceof Integer) {
                    int novaQuantidade = (Integer) qtdObj;
                    produtoExistente.setQuantidadeTotal(novaQuantidade);
                    if (novaQuantidade < produtoExistente.getQuantidadeDisponivel()) {
                        produtoExistente.setQuantidadeDisponivel(novaQuantidade);
                    }
                }
            }
            if (requestBody.containsKey("localizacao")) {
                produtoExistente.setLocalizacao((String) requestBody.get("localizacao"));
            }
            if (requestBody.containsKey("estadoConservacao")) {
                produtoExistente.setEstadoConservacao((String) requestBody.get("estadoConservacao"));
            }
            if (requestBody.containsKey("observacoes")) {
                produtoExistente.setObservacoes((String) requestBody.get("observacoes"));
            }
            if (requestBody.containsKey("ativo")) {
                Object ativoObj = requestBody.get("ativo");
                if (ativoObj instanceof Boolean) {
                    produtoExistente.setAtivo((Boolean) ativoObj);
                }
            }
            
            // CORRIGIDO: Usar atualizarProduto() em vez de save()
            Produto produtoAtualizado = produtoService.atualizarProduto(id, produtoExistente);
            return ResponseEntity.ok(produtoAtualizado);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("erro", e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Produto> buscarPorId(@PathVariable Long id) {
        // CORRIGIDO: Usar buscarPorId()
        Optional<Produto> produtoOpt = produtoService.buscarPorId(id);
        return produtoOpt.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<Produto> buscarPorCodigo(@PathVariable String codigo) {
        Optional<Produto> produtoOpt = produtoService.buscarPorCodigo(codigo);
        return produtoOpt.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/numero-serie/{numeroSerie}")
    public ResponseEntity<Produto> buscarPorNumeroSerie(@PathVariable String numeroSerie) {
        Optional<Produto> produtoOpt = produtoService.buscarPorNumeroSerie(numeroSerie);
        return produtoOpt.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping
    public ResponseEntity<List<Produto>> listarTodos() {
        // CORRIGIDO: Usar listarTodos() em vez de findAll()
        List<Produto> produtos = produtoService.listarTodos();
        return ResponseEntity.ok(produtos);
    }
    
    @GetMapping("/ativos")
    public ResponseEntity<List<Produto>> listarAtivos() {
        List<Produto> produtos = produtoService.listarAtivos();
        return ResponseEntity.ok(produtos);
    }
    
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
    
    @GetMapping("/{id}/disponivel")
    public ResponseEntity<Boolean> verificarDisponibilidade(
            @PathVariable Long id,
            @RequestParam Integer quantidade) {
        try {
            boolean disponivel = produtoService.verificarDisponibilidade(id, quantidade);
            return ResponseEntity.ok(disponivel);
        } catch (Exception e) {
            return ResponseEntity.ok(false);
        }
    }
    
    @GetMapping("/disponiveis")
    public ResponseEntity<List<Produto>> listarDisponiveis() {
        List<Produto> produtos = produtoService.listarDisponiveisParaEmprestimo();
        return ResponseEntity.ok(produtos);
    }
    
    @GetMapping("/indisponiveis")
    public ResponseEntity<List<Produto>> listarIndisponiveis() {
        List<Produto> produtos = produtoService.listarSemDisponibilidade();
        return ResponseEntity.ok(produtos);
    }
    
    // ============ CONSULTAS POR ATRIBUTOS ============
    
    @GetMapping("/categoria/{nomeCategoria}")
    public ResponseEntity<List<Produto>> buscarPorCategoria(@PathVariable String nomeCategoria) {
        try {
            List<Produto> produtos = produtoService.buscarPorCategoria(nomeCategoria);
            return ResponseEntity.ok(produtos);
        } catch (Exception e) {
            return ResponseEntity.ok(List.of());
        }
    }
    
    @GetMapping("/marca/{marca}")
    public ResponseEntity<List<Produto>> buscarPorMarca(@PathVariable String marca) {
        List<Produto> produtos = produtoService.buscarPorMarca(marca);
        return ResponseEntity.ok(produtos);
    }
    
    @GetMapping("/modelo/{modelo}")
    public ResponseEntity<List<Produto>> buscarPorModelo(@PathVariable String modelo) {
        try {
            List<Produto> produtos = produtoService.buscarPorModelo(modelo);
            return ResponseEntity.ok(produtos);
        } catch (Exception e) {
            return ResponseEntity.ok(List.of());
        }
    }
    
    @GetMapping("/localizacao/{localizacao}")
    public ResponseEntity<List<Produto>> buscarPorLocalizacao(@PathVariable String localizacao) {
        List<Produto> produtos = produtoService.buscarPorLocalizacao(localizacao);
        return ResponseEntity.ok(produtos);
    }
    
    @GetMapping("/estado/{estadoConservacao}")
    public ResponseEntity<List<Produto>> buscarPorEstadoConservacao(@PathVariable String estadoConservacao) {
        List<Produto> produtos = produtoService.buscarPorEstadoConservacao(estadoConservacao);
        return ResponseEntity.ok(produtos);
    }
    
    // ============ BUSCA AVANÇADA ============
    
    @GetMapping("/buscar")
    public ResponseEntity<List<Produto>> buscarPorTermo(@RequestParam String termo) {
        List<Produto> produtos = produtoService.buscarPorTermo(termo);
        return ResponseEntity.ok(produtos);
    }
    
    @GetMapping("/filtros")
    public ResponseEntity<List<Produto>> buscarPorFiltros(
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String marca,
            @RequestParam(required = false) String estadoConservacao) {
        List<Produto> produtos = produtoService.buscarPorFiltros(categoria, marca, estadoConservacao);
        return ResponseEntity.ok(produtos);
    }
    
    // ============ RELATÓRIOS E ESTATÍSTICAS ============
    
    @GetMapping("/estatisticas/categoria")
    public ResponseEntity<Map<String, Long>> obterEstatisticasCategoria() {
        try {
            Map<String, Long> estatisticas = produtoService.obterEstatisticasPorCategoria();
            return ResponseEntity.ok(estatisticas);
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of());
        }
    }
    
    @GetMapping("/baixa-disponibilidade")
    public ResponseEntity<List<Produto>> listarBaixaDisponibilidade(
            @RequestParam(defaultValue = "2") Integer quantidadeMinima) {
        List<Produto> produtos = produtoService.listarComBaixaDisponibilidade(quantidadeMinima);
        return ResponseEntity.ok(produtos);
    }
    
    @GetMapping("/contagem-itens")
    public ResponseEntity<Map<String, Integer>> obterContagemItens() {
        try {
            Map<String, Integer> contagem = produtoService.obterContagemTotalItens();
            return ResponseEntity.ok(contagem);
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("total", 0));
        }
    }
    
    @GetMapping("/verificar/codigo/{codigo}")
    public ResponseEntity<Boolean> verificarCodigoExistente(@PathVariable String codigo) {
        boolean existe = produtoService.existeCodigo(codigo);
        return ResponseEntity.ok(existe);
    }
    
    @GetMapping("/verificar/numero-serie/{numeroSerie}")
    public ResponseEntity<Boolean> verificarNumeroSerieExistente(@PathVariable String numeroSerie) {
        boolean existe = produtoService.existeNumeroSerie(numeroSerie);
        return ResponseEntity.ok(existe);
    }
}
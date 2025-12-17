package com.br.controller;

import com.br.model.Produto;
import com.br.model.Categoria;
import com.br.model.Marca;
import com.br.model.Localizacao;
import com.br.service.ProdutoService;
import com.br.service.CategoriaService;
import com.br.service.MarcaService;
import com.br.service.LocalizacaoService;
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
    
    @Autowired
    private MarcaService marcaService;
    
    @Autowired
    private LocalizacaoService localizacaoService;
    
    // ============ CRUD BÁSICO ============
    
    @PostMapping
    public ResponseEntity<?> criarProduto(@RequestBody Map<String, Object> requestBody) {
        try {
            System.out.println("=== DADOS RECEBIDOS ===");
            for (Map.Entry<String, Object> entry : requestBody.entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue() + 
                                 " (tipo: " + (entry.getValue() != null ? entry.getValue().getClass().getSimpleName() : "null") + ")");
            }
            
            // Extrai campos básicos
            String nome = (String) requestBody.get("nome");
            String codigo = (String) requestBody.get("codigo");
            Object categoriaIdObj = requestBody.get("categoriaId");
            Object marcaIdObj = requestBody.get("marcaId");
            Object localizacaoIdObj = requestBody.get("localizacaoId");
            
            if (categoriaIdObj == null) {
                return ResponseEntity.badRequest().body(Map.of("erro", "categoriaId é obrigatório"));
            }
            
            // Converte IDs
            Long categoriaId = converterParaLong(categoriaIdObj, "categoriaId");
            if (categoriaId == null) {
                return ResponseEntity.badRequest().body(Map.of("erro", "categoriaId inválido"));
            }
            
            Long marcaId = converterParaLong(marcaIdObj, "marcaId");
            Long localizacaoId = converterParaLong(localizacaoIdObj, "localizacaoId");
            
            // Busca categoria
            Categoria categoria = categoriaService.findById(categoriaId)
                .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada"));
            
            // Busca marca (se fornecida)
            Marca marca = null;
            if (marcaId != null) {
                marca = marcaService.findById(marcaId).orElse(null);
            }
            
            // Busca localização (se fornecida)
            Localizacao localizacao = null;
            if (localizacaoId != null) {
                localizacao = localizacaoService.findById(localizacaoId).orElse(null);
            }
            
            // Cria produto
            Produto produto = new Produto();
            produto.setNome(nome);
            produto.setDescricao((String) requestBody.get("descricao"));
            produto.setCodigo(codigo);
            produto.setNumeroSerie((String) requestBody.get("numeroSerie"));
            produto.setCategoria(categoria);
            produto.setMarca(marca);
            produto.setModelo((String) requestBody.get("modelo"));
            produto.setLocalizacao(localizacao);
            
            // Quantidades
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
            
            // Outros campos
            produto.setEstadoConservacao((String) requestBody.get("estadoConservacao"));
            produto.setObservacoes((String) requestBody.get("observacoes"));
            
            // Ativo (default true)
            Object ativoObj = requestBody.get("ativo");
            if (ativoObj instanceof Boolean) {
                produto.setAtivo((Boolean) ativoObj);
            } else {
                produto.setAtivo(true);
            }
            
            // Salva produto
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
            System.out.println("Dados: " + requestBody);
            
            // Busca produto existente
            Produto produtoExistente = produtoService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado"));
            
            // Processa categoria
            Object categoriaIdObj = requestBody.get("categoriaId");
            if (categoriaIdObj != null) {
                Long categoriaId = converterParaLong(categoriaIdObj, "categoriaId");
                if (categoriaId != null) {
                    Categoria categoria = categoriaService.findById(categoriaId)
                        .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada"));
                    produtoExistente.setCategoria(categoria);
                }
            }
            
            // Processa marca
            Object marcaIdObj = requestBody.get("marcaId");
            if (marcaIdObj != null) {
                Long marcaId = converterParaLong(marcaIdObj, "marcaId");
                if (marcaId != null) {
                    Marca marca = marcaService.findById(marcaId).orElse(null);
                    produtoExistente.setMarca(marca);
                }
            }
            
            // Processa localização
            Object localizacaoIdObj = requestBody.get("localizacaoId");
            if (localizacaoIdObj != null) {
                Long localizacaoId = converterParaLong(localizacaoIdObj, "localizacaoId");
                if (localizacaoId != null) {
                    Localizacao localizacao = localizacaoService.findById(localizacaoId).orElse(null);
                    produtoExistente.setLocalizacao(localizacao);
                }
            }
            
            // Atualiza outros campos
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
            if (requestBody.containsKey("modelo")) {
                produtoExistente.setModelo((String) requestBody.get("modelo"));
            }
            if (requestBody.containsKey("quantidadeTotal")) {
                Object qtdObj = requestBody.get("quantidadeTotal");
                if (qtdObj instanceof Integer) {
                    int novaQuantidade = (Integer) qtdObj;
                    produtoExistente.setQuantidadeTotal(novaQuantidade);
                    // Ajusta disponível se necessário
                    if (novaQuantidade < produtoExistente.getQuantidadeDisponivel()) {
                        produtoExistente.setQuantidadeDisponivel(novaQuantidade);
                    }
                }
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
            
            // Atualiza produto
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
    
    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<List<Produto>> buscarPorCategoria(@PathVariable Long categoriaId) {
        try {
            // Busca o nome da categoria pelo ID
            String nomeCategoria = categoriaService.findById(categoriaId)
                .map(Categoria::getNome)
                .orElse(null);
            
            if (nomeCategoria != null) {
                List<Produto> produtos = produtoService.buscarPorCategoria(nomeCategoria);
                return ResponseEntity.ok(produtos);
            }
            return ResponseEntity.ok(List.of());
        } catch (Exception e) {
            return ResponseEntity.ok(List.of());
        }
    }
    
    @GetMapping("/marca/{marcaId}")
    public ResponseEntity<List<Produto>> buscarPorMarca(@PathVariable Long marcaId) {
        try {
            // Busca o nome da marca pelo ID
            String nomeMarca = marcaService.findById(marcaId)
                .map(Marca::getNome)
                .orElse(null);
            
            if (nomeMarca != null) {
                List<Produto> produtos = produtoService.buscarPorMarca(nomeMarca);
                return ResponseEntity.ok(produtos);
            }
            return ResponseEntity.ok(List.of());
        } catch (Exception e) {
            return ResponseEntity.ok(List.of());
        }
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
    
    @GetMapping("/localizacao/{localizacaoId}")
    public ResponseEntity<List<Produto>> buscarPorLocalizacao(@PathVariable Long localizacaoId) {
        try {
            // Busca o nome da localização pelo ID
            String nomeLocalizacao = localizacaoService.findById(localizacaoId)
                .map(Localizacao::getNome)
                .orElse(null);
            
            if (nomeLocalizacao != null) {
                List<Produto> produtos = produtoService.buscarPorLocalizacao(nomeLocalizacao);
                return ResponseEntity.ok(produtos);
            }
            return ResponseEntity.ok(List.of());
        } catch (Exception e) {
            return ResponseEntity.ok(List.of());
        }
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
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) Long marcaId,
            @RequestParam(required = false) String estadoConservacao) {
        try {
            // Converte IDs para nomes
            String categoriaNome = null;
            if (categoriaId != null) {
                categoriaNome = categoriaService.findById(categoriaId)
                    .map(Categoria::getNome)
                    .orElse(null);
            }
            
            String marcaNome = null;
            if (marcaId != null) {
                marcaNome = marcaService.findById(marcaId)
                    .map(Marca::getNome)
                    .orElse(null);
            }
            
            List<Produto> produtos = produtoService.buscarPorFiltros(categoriaNome, marcaNome, estadoConservacao);
            return ResponseEntity.ok(produtos);
        } catch (Exception e) {
            return ResponseEntity.ok(List.of());
        }
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
    
    // ============ MÉTODOS AUXILIARES ============
    
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
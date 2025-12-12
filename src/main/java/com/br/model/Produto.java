package com.br.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "produtos")
public class Produto {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
    @Column(nullable = false, length = 100)
    private String nome;
    
    @Size(max = 500, message = "Descrição muito longa")
    @Column(length = 500)
    private String descricao;
    
    @NotBlank(message = "Código patrimonial/SN é obrigatório")
    @Size(min = 3, max = 50, message = "Código deve ter entre 3 e 50 caracteres")
    @Column(nullable = false, length = 50, unique = true)
    private String codigo; // Código patrimonial ou Número de Série
    
    @Column(name = "numero_serie", length = 100)
    private String numeroSerie; // Número de série do equipamento
    
    // ALTERAÇÃO AQUI: Agora é relação ManyToOne, não String
    @ManyToOne
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria; // Mudou de String para Categoria
    
    @Column(name = "marca", length = 50)
    private String marca; // Dell, HP, Logitech, Microsoft, etc.
    
    @Column(name = "modelo", length = 100)
    private String modelo; // Inspiron 15, MX Master 3, etc.
    
    @NotNull(message = "Quantidade total é obrigatória")
    @Min(value = 0, message = "Quantidade não pode ser negativa")
    @Column(name = "quantidade_total", nullable = false)
    private Integer quantidadeTotal = 1; // Para itens únicos (notebook) = 1
    
    @Column(name = "quantidade_disponivel", nullable = false)
    private Integer quantidadeDisponivel = 1; // Quantos estão disponíveis para empréstimo
    
    @Column(name = "localizacao", length = 100)
    private String localizacao; // Estante A, Almoxarifado, Sala TI
    
    @Column(name = "estado_conservacao", length = 20)
    private String estadoConservacao; // NOVO, BOM, REGULAR, RUIM
    
    @Column(name = "data_aquisicao")
    private LocalDateTime dataAquisicao; // Quando foi adquirido
    
    @Column(name = "garantia_ate")
    private LocalDateTime garantiaAte; // Data fim da garantia
    
    @Column(name = "observacoes", length = 1000)
    private String observacoes;
    
    @Column(name = "ativo", nullable = false)
    private Boolean ativo = true;
    
    @Column(name = "data_criacao")
    private LocalDateTime dataCriacao;
    
    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;
    
    // Construtor padrão
    public Produto() {
        this.dataCriacao = LocalDateTime.now();
    }
    
    // Construtor simplificado para inventário (ATUALIZADO)
    public Produto(String nome, String codigo, Categoria categoria) { // Mudou aqui
        this();
        this.nome = nome;
        this.codigo = codigo;
        this.categoria = categoria; // Agora recebe objeto Categoria
    }
    
    // Método executado antes de persistir
    @PrePersist
    protected void onCreate() {
        if (dataCriacao == null) {
            dataCriacao = LocalDateTime.now();
        }
        if (quantidadeDisponivel == null) {
            quantidadeDisponivel = quantidadeTotal;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        dataAtualizacao = LocalDateTime.now();
    }
    
    // Getters e Setters (ATUALIZE O GETTER/SETTER DE CATEGORIA)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    
    public String getNumeroSerie() { return numeroSerie; }
    public void setNumeroSerie(String numeroSerie) { this.numeroSerie = numeroSerie; }
    
    // GETTER E SETTER ATUALIZADOS PARA CATEGORIA
    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }
    
    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }
    
    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }
    
    public Integer getQuantidadeTotal() { return quantidadeTotal; }
    public void setQuantidadeTotal(Integer quantidadeTotal) { 
        this.quantidadeTotal = quantidadeTotal; 
    }
    
    public Integer getQuantidadeDisponivel() { return quantidadeDisponivel; }
    public void setQuantidadeDisponivel(Integer quantidadeDisponivel) { 
        this.quantidadeDisponivel = quantidadeDisponivel; 
    }
    
    public String getLocalizacao() { return localizacao; }
    public void setLocalizacao(String localizacao) { this.localizacao = localizacao; }
    
    public String getEstadoConservacao() { return estadoConservacao; }
    public void setEstadoConservacao(String estadoConservacao) { 
        this.estadoConservacao = estadoConservacao; 
    }
    
    public LocalDateTime getDataAquisicao() { return dataAquisicao; }
    public void setDataAquisicao(LocalDateTime dataAquisicao) { 
        this.dataAquisicao = dataAquisicao; 
    }
    
    public LocalDateTime getGarantiaAte() { return garantiaAte; }
    public void setGarantiaAte(LocalDateTime garantiaAte) { 
        this.garantiaAte = garantiaAte; 
    }
    
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
    
    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }
    
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { 
        this.dataCriacao = dataCriacao; 
    }
    
    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
    public void setDataAtualizacao(LocalDateTime dataAtualizacao) { 
        this.dataAtualizacao = dataAtualizacao; 
    }
}
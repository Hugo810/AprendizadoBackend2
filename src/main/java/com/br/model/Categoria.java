package com.br.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "categorias")
public class Categoria {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 2, max = 50, message = "Nome deve ter entre 2 e 50 caracteres")
    @Column(nullable = false, length = 50, unique = true)
    private String nome;
    
    @Size(max = 200, message = "Descrição muito longa")
    @Column(length = 200)
    private String descricao;
    
    @Column(name = "tipo_sistema", nullable = false)
    private Boolean tipoSistema = false; // true = categoria do sistema, false = personalizada
    
    @Column(name = "ativo", nullable = false)
    private Boolean ativo = true;
    
    @Column(name = "data_criacao")
    private LocalDateTime dataCriacao;
    
    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;
    
    // Construtor
    public Categoria() {
        this.dataCriacao = LocalDateTime.now();
    }
    
    public Categoria(String nome, Boolean tipoSistema) {
        this();
        this.nome = nome;
        this.tipoSistema = tipoSistema;
        this.ativo = true; // Adicione esta linha
    }
    
    @PrePersist
    protected void onCreate() {
        if (dataCriacao == null) {
            dataCriacao = LocalDateTime.now();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        dataAtualizacao = LocalDateTime.now();
    }
    
    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    
    public Boolean getTipoSistema() { return tipoSistema; }
    public void setTipoSistema(Boolean tipoSistema) { this.tipoSistema = tipoSistema; }
    
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
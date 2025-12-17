package com.br.model;



import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "localizacoes")
public class Localizacao {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String nome;
    
    private String descricao;
    
    @Column(nullable = false)
    private Boolean ativo = true;
    
    @Column(name = "tipo_sistema")
    private Boolean tipoSistema = false; // Localizações do sistema não podem ser excluídas
    
    @Column(name = "data_criacao")
    private LocalDateTime dataCriacao = LocalDateTime.now();
    
    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao = LocalDateTime.now();
    
    // Construtores
    public Localizacao() {}
    
    public Localizacao(String nome, String descricao) {
        this.nome = nome;
        this.descricao = descricao;
    }
    
    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNome() { return nome; }
    public void setNome(String nome) { 
        this.nome = nome;
        this.dataAtualizacao = LocalDateTime.now();
    }
    
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { 
        this.descricao = descricao;
        this.dataAtualizacao = LocalDateTime.now();
    }
    
    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { 
        this.ativo = ativo;
        this.dataAtualizacao = LocalDateTime.now();
    }
    
    public Boolean getTipoSistema() { return tipoSistema; }
    public void setTipoSistema(Boolean tipoSistema) { this.tipoSistema = tipoSistema; }
    
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }
    
    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
    public void setDataAtualizacao(LocalDateTime dataAtualizacao) { this.dataAtualizacao = dataAtualizacao; }
}
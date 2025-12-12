package com.br.model.enums;

public enum EstadoConservacao {
    NOVO("Novo"),
    EXCELENTE("Excelente"),
    BOM("Bom"),
    REGULAR("Regular"),
    RUIM("Ruim"),
    FORA_DE_USO("Fora de Uso");
    
    private final String descricao;
    
    EstadoConservacao(String descricao) {
        this.descricao = descricao;
    }
    
    public String getDescricao() {
        return descricao;
    }
}
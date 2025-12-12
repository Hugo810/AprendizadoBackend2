package com.br.model.enums;

public enum CategoriaProduto {
    NOTEBOOK("Notebook"),
    DESKTOP("Desktop"),
    MONITOR("Monitor"),
    TECLADO("Teclado"),
    MOUSE("Mouse"),
    IMPRESSORA("Impressora"),
    SCANNER("Scanner"),
    SERVIDOR("Servidor"),
    ROTEADOR("Roteador"),
    SWITCH("Switch"),
    SOFTWARE("Software"),
    LICENCA("Licença"),
    COMPONENTE("Componente"),
    PERIFERICO("Periférico"),
    OUTRO("Outro");
    
    private final String descricao;
    
    CategoriaProduto(String descricao) {
        this.descricao = descricao;
    }
    
    public String getDescricao() {
        return descricao;
    }
}
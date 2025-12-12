package com.br.config;

import com.br.model.Categoria;
import com.br.repository.CategoriaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

@Configuration
public class CategoriaInitializer {
    
    @Bean
    @Transactional
    CommandLineRunner initCategorias(CategoriaRepository categoriaRepository) {
        return args -> {
            // Verifica categoria por categoria
            String[] categoriasSistema = {
                "Notebook", "Desktop", "Monitor", "Teclado", "Mouse",
                "Impressora", "Scanner", "Servidor", "Roteador", "Switch",
                "Software", "Licença", "Componente", "Periférico", "Outro"
            };
            
            for (String nome : categoriasSistema) {
                // Só cria se não existir
                if (categoriaRepository.findByNome(nome).isEmpty()) {
                    Categoria categoria = new Categoria();
                    categoria.setNome(nome);
                    categoria.setDescricao("Categoria do sistema - " + nome);
                    categoria.setTipoSistema(true);
                    categoria.setAtivo(true);
                    
                    categoriaRepository.save(categoria);
                    System.out.println("Categoria criada: " + nome);
                }
            }
            
            System.out.println("Inicialização de categorias concluída!");
        };
    }
}
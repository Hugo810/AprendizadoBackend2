package com.br.model;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name="usuarios")
public class Usuario {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)//Auto incremento
	private Long id;
	
	@Column(nullable = false, length = 100)
	@Size(min=3,max=100,message = "Nome deve ter de 3 a 100 caracteres")
	@NotBlank(message="Nome deve ser obrigatório")
	private String nome;
	
	@NotBlank(message = "O email é obrigatório")
	@Email(message="Email invalido")
	@Column(nullable = false,unique = true)
	private String email;
	
	@Column(name="matricula",unique = true, length = 50)
	private String matricula;
	
	@Column(name = "departamento", length  = 100)
	private String departamento;
	
	@Column(name="cargo",length = 100)
	private String cargo;
	
	@Column(name ="telefone", length = 20)
	private String telefone;
	
	@Column(name ="ativo", nullable = false)
	private Boolean ativo= true;
	
	@Column(name="data_criacao")
	private LocalDateTime dataCriacao;
	
	@Column(name="data_atualizacao")
	private LocalDateTime dataAtualzacao;
	
	public Usuario () {
		dataCriacao=LocalDateTime.now();
	}
	
	public Usuario(String nome, String email, String matricula){
		this.nome = nome;
		this.email= email;
		this.matricula= matricula;
	}
	
	//Metodos de ciclo de vida
	
	@PrePersist
	protected void onCreate() {
		if(dataCriacao==null){
			dataCriacao = LocalDateTime.now();
		}
	}
	
	@PreUpdate
	protected void onUpdate() {
		dataAtualzacao=LocalDateTime.now();
	}
	
	//Getters e setters
	
	public Long getId() {return id;}
	public void setId(Long id) {this.id=id;}
	
	public String getNome() {return nome;}
	public void setNome(String nome) {this.nome = nome;}
	
	public String getEmail(){return email;}
	public void setEmail(String email) {this.email=email;}
	
	public String getMatricula() {return matricula;}
	public void setMatricula(String matricula) {this.matricula = matricula;}
	
	public String getDepartamento() {return departamento;}
	public void setDepartamento(String departamento){this.departamento = departamento;}
	
	public String getCargo() {return cargo;}
	public void setCargo(String cargo) {this.cargo = cargo;}
	
	public String getTelefone() {return telefone;}
	public void setTelefone(String telefone) {this.telefone = telefone;}
	
	public Boolean getAtivo() {return ativo;}
	public void setAtivo(Boolean ativo) {this.ativo = ativo;}
	
	public LocalDateTime getDataCriacao() {return dataCriacao;}
	public void setDataCriacao(LocalDateTime dataCriacao) {this.dataCriacao = dataCriacao;}
	
	public LocalDateTime getDataAtualizacao() {return dataAtualzacao;}
	public void setDataAtualizacao(LocalDateTime dataAtualizacao) {this.dataAtualzacao= dataAtualizacao;}
		
	
}

package com.generation.blogpessoal.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

@Entity
@Table(name = "tb_temas")
public class Tema {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O Atributo Descrição é obrigatório")
    @Size(max = 255, message = "O atributo Descrição deve ter no máximo 255 caracteres")
    @Column(length = 255)
    private String descricao;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "tema", cascade = CascadeType.REMOVE)
    @JsonIgnoreProperties(value = "tema", allowSetters = true)
    private List<Postagem> postagem;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return this.descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public List<Postagem> getPostagem() {
        return this.postagem;
    }

    public void setPostagem(List<Postagem> postagem) {
        this.postagem = postagem;
    }
}

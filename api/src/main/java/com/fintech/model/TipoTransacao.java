package com.fintech.model;

import javax.persistence.*;

@Entity
@Table(name = "CD_TP_TRANSACAO")
public class TipoTransacao {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_transacao")
    private Integer idTipoTransacao;
    
    @Column(name = "descricao", length = 100)
    private String descricao;

    // Construtores
    public TipoTransacao() {
    }

    public TipoTransacao(String descricao) {
        this.descricao = descricao;
    }

    // Getters e Setters
    public Integer getIdTipoTransacao() {
        return idTipoTransacao;
    }

    public void setIdTipoTransacao(Integer idTipoTransacao) {
        this.idTipoTransacao = idTipoTransacao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return "TipoTransacao{" +
                "idTipoTransacao=" + idTipoTransacao +
                ", descricao='" + descricao + '\'' +
                '}';
    }
}


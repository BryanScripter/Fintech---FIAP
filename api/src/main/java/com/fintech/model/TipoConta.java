package com.fintech.model;

import javax.persistence.*;

@Entity
@Table(name = "CD_TP_CONTA")
public class TipoConta {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_conta")
    private Integer idTipoConta;
    
    @Column(name = "descricao", length = 100)
    private String descricao;

    // Construtores
    public TipoConta() {
    }

    public TipoConta(String descricao) {
        this.descricao = descricao;
    }

    // Getters e Setters
    public Integer getIdTipoConta() {
        return idTipoConta;
    }

    public void setIdTipoConta(Integer idTipoConta) {
        this.idTipoConta = idTipoConta;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return "TipoConta{" +
                "idTipoConta=" + idTipoConta +
                ", descricao='" + descricao + '\'' +
                '}';
    }
}


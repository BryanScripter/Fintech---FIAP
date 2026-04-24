package com.fintech.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "DIVIDA")
public class Divida {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_divida")
    private Integer idDivida;
    
    @Column(name = "CONTA_id_conta", nullable = true)
    private Integer contaIdConta;
    
    @Column(name = "descricao", nullable = false, length = 200)
    private String descricao;
    
    @Column(name = "valor_total", nullable = false)
    private Double valorTotal;
    
    @Column(name = "juros_mensal")
    private Double jurosMensal;
    
    @Column(name = "dt_inicio")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dtInicio;
    
    @Column(name = "dt_vencimento", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dtVencimento;
    
    @Column(name = "quitada")
    private String quitada;
    
    @Column(name = "tp_divida", nullable = false, length = 50)
    private String tpDivida;

    // Construtores
    public Divida() {
        this.quitada = "N";
        this.jurosMensal = 0.0;
        this.dtInicio = new Date();
    }

    public Divida(Integer contaIdConta, String descricao, Double valorTotal, Date dtVencimento, String tpDivida) {
        this.contaIdConta = contaIdConta;
        this.descricao = descricao;
        this.valorTotal = valorTotal;
        this.dtVencimento = dtVencimento;
        this.tpDivida = tpDivida;
        this.quitada = "N";
        this.jurosMensal = 0.0;
        this.dtInicio = new Date();
    }

    @PrePersist
    protected void onCreate() {
        if (dtInicio == null) {
            dtInicio = new Date();
        }
        if (quitada == null) {
            quitada = "N";
        }
        if (jurosMensal == null) {
            jurosMensal = 0.0;
        }
    }

    // Getters e Setters
    public Integer getIdDivida() {
        return idDivida;
    }

    public void setIdDivida(Integer idDivida) {
        this.idDivida = idDivida;
    }

    public Integer getContaIdConta() {
        return contaIdConta;
    }

    public void setContaIdConta(Integer contaIdConta) {
        this.contaIdConta = contaIdConta;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(Double valorTotal) {
        this.valorTotal = valorTotal;
    }

    public Double getJurosMensal() {
        return jurosMensal;
    }

    public void setJurosMensal(Double jurosMensal) {
        this.jurosMensal = jurosMensal;
    }

    public Date getDtInicio() {
        return dtInicio;
    }

    public void setDtInicio(Date dtInicio) {
        this.dtInicio = dtInicio;
    }

    public Date getDtVencimento() {
        return dtVencimento;
    }

    public void setDtVencimento(Date dtVencimento) {
        this.dtVencimento = dtVencimento;
    }

    public String getQuitada() {
        return quitada;
    }

    public void setQuitada(String quitada) {
        this.quitada = quitada;
    }

    public String getTpDivida() {
        return tpDivida;
    }

    public void setTpDivida(String tpDivida) {
        this.tpDivida = tpDivida;
    }

    @Override
    public String toString() {
        return "Divida{" +
                "idDivida=" + idDivida +
                ", contaIdConta=" + contaIdConta +
                ", descricao='" + descricao + '\'' +
                ", valorTotal=" + valorTotal +
                ", jurosMensal=" + jurosMensal +
                ", dtInicio=" + dtInicio +
                ", dtVencimento=" + dtVencimento +
                ", quitada='" + quitada + '\'' +
                ", tpDivida='" + tpDivida + '\'' +
                '}';
    }
}

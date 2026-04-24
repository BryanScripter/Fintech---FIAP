package com.fintech.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "TRANSACAO")
public class Transacao {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_transacao")
    private Integer idTransacao;
    
    @ManyToOne
    @JoinColumn(name = "CONTA_ID_CONTA", nullable = true)
    private Conta conta;
    
    @ManyToOne
    @JoinColumn(name = "CD_TP_TRANSACAO_ID_TIPO_TRANSACAO", nullable = false)
    private TipoTransacao tipoTransacao;
    
    // Campo transiente para receber o ID do JSON do frontend
    @Transient
    private Integer cdTpTransacaoIdTipoTransacao;
    
    @Column(name = "valor", nullable = false)
    private Double valor;
    
    @Column(name = "dt_transacao")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dtTransacao;
    
    @Column(name = "descricao", nullable = false, length = 200)
    private String descricao;

    // Construtores
    public Transacao() {
    }

    public Transacao(Conta conta, TipoTransacao tipoTransacao, Double valor, String descricao) {
        this.conta = conta;
        this.tipoTransacao = tipoTransacao;
        this.valor = valor;
        this.descricao = descricao;
        this.dtTransacao = new Date();
    }

    @PrePersist
    protected void onCreate() {
        if (dtTransacao == null) {
            dtTransacao = new Date();
        }
    }

    // Getters e Setters
    public Integer getIdTransacao() {
        return idTransacao;
    }

    public void setIdTransacao(Integer idTransacao) {
        this.idTransacao = idTransacao;
    }

    public Conta getConta() {
        return conta;
    }

    public void setConta(Conta conta) {
        this.conta = conta;
    }
    
    // Método auxiliar para manter compatibilidade
    public Integer getContaIdConta() {
        return conta != null ? conta.getIdConta() : null;
    }

    public TipoTransacao getTipoTransacao() {
        return tipoTransacao;
    }

    public void setTipoTransacao(TipoTransacao tipoTransacao) {
        this.tipoTransacao = tipoTransacao;
    }
    
    // Método auxiliar para manter compatibilidade com frontend
    public Integer getCdTpTransacaoIdTipoTransacao() {
        // Retorna o ID do tipoTransacao se existir, senão retorna o campo transiente
        if (tipoTransacao != null && tipoTransacao.getIdTipoTransacao() != null) {
            return tipoTransacao.getIdTipoTransacao();
        }
        return cdTpTransacaoIdTipoTransacao;
    }
    
    public void setCdTpTransacaoIdTipoTransacao(Integer id) {
        // Apenas guarda o ID que veio do frontend
        // O Service será responsável por buscar o TipoTransacao completo do banco
        this.cdTpTransacaoIdTipoTransacao = id;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public Date getDtTransacao() {
        return dtTransacao;
    }

    public void setDtTransacao(Date dtTransacao) {
        this.dtTransacao = dtTransacao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return "Transacao{" +
                "idTransacao=" + idTransacao +
                ", conta=" + (conta != null ? conta.getIdConta() : "null") +
                ", tipoTransacao=" + (tipoTransacao != null ? tipoTransacao.getIdTipoTransacao() : "null") +
                ", valor=" + valor +
                ", dtTransacao=" + dtTransacao +
                ", descricao='" + descricao + '\'' +
                '}';
    }
}

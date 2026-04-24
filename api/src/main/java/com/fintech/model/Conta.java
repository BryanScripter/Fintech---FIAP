package com.fintech.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "CONTA")
public class Conta {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_conta")
    private Integer idConta;
    
    @ManyToOne
    @JoinColumn(name = "USUARIO_id_usuario", nullable = false)
    private Usuario usuario;
    
    @ManyToOne
    @JoinColumn(name = "CD_TP_CONTA_ID_TIPO_CONTA", nullable = false)
    private TipoConta tipoConta;
    
    // Campo adicional para CD_TIPO_CONTA_ID_TIPO_CONTA (banco tem dois campos de tipo conta)
    @ManyToOne
    @JoinColumn(name = "CD_TIPO_CONTA_ID_TIPO_CONTA", nullable = false)
    private TipoConta tipoContaAlternativo;
    
    @Column(name = "saldo")
    private Double saldo = 0.0;
    
    @Column(name = "saldo_inicial")
    private Double saldoInicial = 0.0;
    
    @Column(name = "dt_criacao")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dtCriacao;
    
    @Column(name = "dt_inicio")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dtInicio;

    // Construtores
    public Conta() {
        this.saldo = 0.0;
        this.saldoInicial = 0.0;
        Date agora = new Date();
        this.dtCriacao = agora;
        this.dtInicio = agora;
    }

    // Construtor que recebe o objeto Usuario e TipoConta (assumindo que o TipoConta com ID 1 é o padrão)
    public Conta(Usuario usuario, TipoConta tipoConta) {
        this.usuario = usuario;
        this.tipoConta = tipoConta;
        this.tipoContaAlternativo = tipoConta; // Usa o mesmo TipoConta para ambos os campos
        this.saldo = 0.0;
        this.saldoInicial = 0.0;
        Date agora = new Date();
        this.dtCriacao = agora;
        this.dtInicio = agora;
    }
    
    // Construtor que recebe apenas o Usuario (usando TipoConta padrão)
    public Conta(Usuario usuario) {
        this.usuario = usuario;
        // O TipoConta deve ser setado no service ou controller antes de salvar,
        // mas para manter a compatibilidade temporária, vamos deixar o campo tipoConta nulo aqui.
        // A correção completa será feita no service.
        this.saldo = 0.0;
        this.saldoInicial = 0.0;
        Date agora = new Date();
        this.dtCriacao = agora;
        this.dtInicio = agora;
    }


    @PrePersist
    protected void onCreate() {
        Date agora = new Date();
        if (dtCriacao == null) {
            dtCriacao = agora;
        }
        if (dtInicio == null) {
            dtInicio = agora;
        }
        if (saldo == null) {
            saldo = 0.0;
        }
        if (saldoInicial == null) {
            saldoInicial = 0.0;
        }
        // O tipoConta deve ser setado antes de chamar o save.
        // Se o tipoConta for nulo aqui, a constraint será violada.
        // Este método não pode buscar o TipoConta do banco, então
        // é responsabilidade do Service garantir que tipoConta seja definido.
    }

    // Getters e Setters
    public Integer getIdConta() {
        return idConta;
    }

    public void setIdConta(Integer idConta) {
        this.idConta = idConta;
    }

    public Usuario getUsuario() {
        return usuario;
    }
    
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
    
    public TipoConta getTipoConta() {
        return tipoConta;
    }
    
    public void setTipoConta(TipoConta tipoConta) {
        this.tipoConta = tipoConta;
        // Atualiza também o campo alternativo se necessário
        if (this.tipoContaAlternativo == null) {
            this.tipoContaAlternativo = tipoConta;
        }
    }
    
    public TipoConta getTipoContaAlternativo() {
        return tipoContaAlternativo;
    }
    
    public void setTipoContaAlternativo(TipoConta tipoContaAlternativo) {
        this.tipoContaAlternativo = tipoContaAlternativo;
    }

    public Double getSaldo() {
        return saldo;
    }

    public void setSaldo(Double saldo) {
        this.saldo = saldo;
    }

    public Date getDtCriacao() {
        return dtCriacao;
    }

    public void setDtCriacao(Date dtCriacao) {
        this.dtCriacao = dtCriacao;
    }
    
    public Double getSaldoInicial() {
        return saldoInicial;
    }
    
    public void setSaldoInicial(Double saldoInicial) {
        this.saldoInicial = saldoInicial;
    }
    
    public Date getDtInicio() {
        return dtInicio;
    }
    
    public void setDtInicio(Date dtInicio) {
        this.dtInicio = dtInicio;
    }

    @Override
    public String toString() {
        return "Conta{" +
                "idConta=" + idConta +
                ", usuarioIdUsuario=" + (usuario != null ? usuario.getIdUsuario() : "null") +
                ", tipoContaId=" + (tipoConta != null ? tipoConta.getIdTipoConta() : "null") +
                ", saldo=" + saldo +
                ", dtCriacao=" + dtCriacao +
                '}';
    }
}

package com.fintech.service;

import com.fintech.model.Conta;
import com.fintech.model.TipoConta;
import com.fintech.model.Usuario;
import com.fintech.repository.ContaRepository;
import com.fintech.repository.TipoContaRepository;
import com.fintech.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ContaService {
    
    @Autowired
    private ContaRepository contaRepository;
    
    @Autowired
    private TipoContaRepository tipoContaRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    /**
     * Salva uma conta garantindo que tenha TipoConta
     * Se o tipoConta for nulo, cria ou busca um tipoConta padrão automaticamente
     * Não depende do banco ter dados pré-existentes
     */
    @Transactional
    public Conta save(Conta conta) {
        // Sempre garantir que temos um TipoConta válido
        TipoConta tipoContaGerenciado = null;
        
        if (conta.getTipoConta() != null && conta.getTipoConta().getIdTipoConta() != null) {
            // Se já tem TipoConta com ID, buscar do banco ou criar se não existir
            tipoContaGerenciado = tipoContaRepository.findById(conta.getTipoConta().getIdTipoConta())
                    .orElseGet(() -> {
                        // Se não encontrou, criar um novo TipoConta padrão
                        TipoConta novo = new TipoConta("Conta Corrente");
                        return tipoContaRepository.save(novo);
                    });
        } else {
            // Se não tem TipoConta, buscar ID 1 ou criar se não existir
            tipoContaGerenciado = tipoContaRepository.findById(1)
                    .orElseGet(() -> {
                        // Criar TipoConta padrão se não existir
                        TipoConta novo = new TipoConta("Conta Corrente");
                        TipoConta salvo = tipoContaRepository.save(novo);
                        // Se o ID gerado não for 1, tentar buscar novamente
                        if (salvo.getIdTipoConta() != 1) {
                            return tipoContaRepository.findById(1).orElse(salvo);
                        }
                        return salvo;
                    });
        }
        
        // Definir o TipoConta na conta (ambos os campos - banco tem dois campos de tipo conta)
        conta.setTipoConta(tipoContaGerenciado);
        conta.setTipoContaAlternativo(tipoContaGerenciado); // Usa o mesmo TipoConta para ambos os campos
        
        // Salvar a conta
        Conta contaSalva = contaRepository.save(conta);
        
        // Garantir que o ID foi gerado e a conta está persistida
        // O save() do JPA já retorna a entidade com ID, mas garantimos aqui
        if (contaSalva.getIdConta() == null) {
            // Se por algum motivo o ID não foi gerado, forçar flush
            contaRepository.flush();
            // Recarregar do banco
            if (contaSalva.getUsuario() != null && contaSalva.getUsuario().getIdUsuario() != null) {
                return contaRepository.findByUsuario_IdUsuario(contaSalva.getUsuario().getIdUsuario())
                        .orElse(contaSalva);
            }
        }
        
        return contaSalva;
    }
    
    /**
     * Cria uma conta para um usuário específico
     * Útil para usuários antigos que não têm conta ainda
     */
    @Transactional
    public Conta criarContaParaUsuario(Integer userId) {
        // Verifica se o usuário existe e está gerenciado pelo Hibernate
        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado com ID: " + userId));
        
        // Verifica se já não existe uma conta para este usuário
        if (contaRepository.findByUsuario_IdUsuario(userId).isPresent()) {
            throw new RuntimeException("Usuário já possui uma conta cadastrada");
        }
        
        // Busca o TipoConta padrão (ID 1 = Conta Corrente) antes de criar a conta
        TipoConta tipoContaPadrao = tipoContaRepository.findById(1)
                .orElseGet(() -> {
                    // Se não encontrou, cria o TipoConta padrão
                    TipoConta novo = new TipoConta("Conta Corrente");
                    return tipoContaRepository.save(novo);
                });
        
        // Cria a nova conta para o usuário usando o construtor que define TUDO
        // Este construtor define: usuario, tipoConta, saldo e dtCriacao
        Conta novaConta = new Conta(usuario, tipoContaPadrao);
        
        // Garantir que todos os campos obrigatórios sejam preenchidos (redundância de segurança)
        if (novaConta.getSaldo() == null) {
            novaConta.setSaldo(0.0);
        }
        if (novaConta.getSaldoInicial() == null) {
            novaConta.setSaldoInicial(0.0);
        }
        if (novaConta.getDtCriacao() == null) {
            novaConta.setDtCriacao(new java.util.Date());
        }
        if (novaConta.getDtInicio() == null) {
            novaConta.setDtInicio(new java.util.Date());
        }
        if (novaConta.getTipoConta() == null) {
            novaConta.setTipoConta(tipoContaPadrao);
        }
        if (novaConta.getTipoContaAlternativo() == null) {
            novaConta.setTipoContaAlternativo(tipoContaPadrao); // Ambos os campos precisam ser preenchidos
        }
        if (novaConta.getUsuario() == null) {
            novaConta.setUsuario(usuario);
        }
        
        // Usa o método save() que faz validações finais e garante que tudo está correto
        return save(novaConta);
    }
    
    /**
     * Corrige todas as contas que não possuem TipoConta
     * Este método deve ser chamado uma vez para atualizar contas existentes no banco
     */
    public int corrigirContasSemTipo() {
        // Buscar todas as contas
        List<Conta> todasContas = contaRepository.findAll();
        int contador = 0;
        
        // Buscar TipoConta padrão (ID 1 = Conta Corrente)
        Optional<TipoConta> tipoContaOpt = tipoContaRepository.findById(1);
        TipoConta tipoContaPadrao = tipoContaOpt.orElseGet(() -> {
            TipoConta novo = new TipoConta("Conta Corrente");
            return tipoContaRepository.save(novo);
        });
        
        // Atualizar contas sem tipo de conta
        for (Conta conta : todasContas) {
            if (conta.getTipoConta() == null) {
                conta.setTipoConta(tipoContaPadrao);
                contaRepository.save(conta);
                contador++;
            }
        }
        
        return contador;
    }
}


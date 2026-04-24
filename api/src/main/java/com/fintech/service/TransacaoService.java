package com.fintech.service;

import com.fintech.model.Conta;
import com.fintech.model.TipoTransacao;
import com.fintech.model.Transacao;
import com.fintech.repository.ContaRepository;
import com.fintech.repository.TipoTransacaoRepository;
import com.fintech.repository.TransacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;

@Service
public class TransacaoService {
    
    @Autowired
    private TransacaoRepository transacaoRepository;
    
    @Autowired
    private TipoTransacaoRepository tipoTransacaoRepository;
    
    @Autowired
    private ContaRepository contaRepository;
    
    // Conta sistema fixa para testes (usa conta existente, nunca cria)
    private static Integer CONTA_SISTEMA_ID = null;
    
    /**
     * Busca uma conta existente na inicialização (NÃO cria conta)
     */
    @PostConstruct
    public void buscarContaSistema() {
        try {
            // Tenta usar conta ID 1 se existir
            Optional<Conta> contaSistema = contaRepository.findById(1);
            if (contaSistema.isPresent()) {
                CONTA_SISTEMA_ID = 1;
                return;
            }
            
            // Se não tem conta ID 1, busca qualquer conta existente
            List<Conta> todasContas = contaRepository.findAll();
            if (!todasContas.isEmpty()) {
                CONTA_SISTEMA_ID = todasContas.get(0).getIdConta();
            }
            // Se não tem nenhuma conta, deixa null (será tratado no saveForUser)
            
        } catch (Exception e) {
            // Ignora erro - será tratado no saveForUser
            CONTA_SISTEMA_ID = null;
        }
    }
    
    public List<Transacao> findAll() {
        return transacaoRepository.findAll();
    }
    
    public Optional<Transacao> findById(Integer id) {
        return transacaoRepository.findById(id);
    }
    
    public List<Transacao> findByContaId(Integer contaId) {
        return transacaoRepository.findByConta_IdConta(contaId);
    }
    
    /**
     * Busca transações por ID do usuário
     * Retorna lista vazia já que conta é opcional
     */
    public List<Transacao> findByUserId(Integer userId) {
        // Como conta é opcional, não há como buscar por userId diretamente
        // Retorna todas as transações (ou implementar busca por outro campo se necessário)
        return transacaoRepository.findAll();
    }
    
    public List<Transacao> findByTipoTransacao(Integer tipoTransacaoId) {
        return transacaoRepository.findByTipoTransacao_IdTipoTransacao(tipoTransacaoId);
    }
    
    public Transacao save(Transacao transacao) {
        // Validação básica
        if (transacao.getValor() == null || transacao.getValor() <= 0) {
            throw new IllegalArgumentException("Valor deve ser maior que zero");
        }
        
        if (transacao.getDescricao() == null || transacao.getDescricao().isEmpty()) {
            throw new IllegalArgumentException("Descrição é obrigatória");
        }
        
        // Validar tipo de transação
        if (transacao.getTipoTransacao() == null) {
            throw new IllegalArgumentException("Tipo de transação é obrigatório");
        }
        
        return transacaoRepository.save(transacao);
    }
    
    /**
     * Salva transação (sem depender de conta ou usuário)
     * A conta é opcional - pode ser null
     */
    @Transactional
    public Transacao saveForUser(Transacao transacao, Integer userId) {
        // 1️⃣ Validações básicas
        if (transacao.getValor() == null || transacao.getValor() <= 0) {
            throw new IllegalArgumentException("Valor deve ser maior que zero");
        }
        
        if (transacao.getDescricao() == null || transacao.getDescricao().trim().isEmpty()) {
            throw new IllegalArgumentException("Descrição é obrigatória");
        }
        
        // 2️⃣ Identificar o tipo de transação corretamente
        Integer tipoId = null;
        if (transacao.getTipoTransacao() != null && transacao.getTipoTransacao().getIdTipoTransacao() != null) {
            tipoId = transacao.getTipoTransacao().getIdTipoTransacao();
        } else if (transacao.getCdTpTransacaoIdTipoTransacao() != null) {
            tipoId = transacao.getCdTpTransacaoIdTipoTransacao();
        }
        
        // Se o tipo não vier do frontend, define automaticamente como "Receita" (ID 1)
        if (tipoId == null) {
            tipoId = 1; // Padrão: Receita
        }
        
        // Buscar o TipoTransacao do banco (entidade gerenciada)
        TipoTransacao tipoTransacao = tipoTransacaoRepository.findById(tipoId)
                .orElseThrow(() -> new RuntimeException("Tipo de transação não encontrado"));
        
        // 3️⃣ Associa o tipoTransacao à transação
        transacao.setTipoTransacao(tipoTransacao);
        
        // 4️⃣ Para projeto de teste: usa conta sistema ou conta do usuário (NUNCA cria conta nova)
        Conta conta = null;
        
        // Primeiro tenta buscar conta do usuário
        Optional<Conta> contaOpt = contaRepository.findByUsuario_IdUsuario(userId);
        if (contaOpt.isPresent()) {
            conta = contaOpt.get();
        } else {
            // Se não tem conta do usuário, usa conta sistema (criada automaticamente na inicialização)
            if (CONTA_SISTEMA_ID != null) {
                conta = contaRepository.findById(CONTA_SISTEMA_ID).orElse(null);
            }
            
            // Se ainda não tem conta, busca qualquer conta existente
            if (conta == null) {
                List<Conta> todasContas = contaRepository.findAll();
                if (!todasContas.isEmpty()) {
                    conta = todasContas.get(0);
                    CONTA_SISTEMA_ID = conta.getIdConta(); // Salva para próxima vez
                }
            }
        }
        
        // Se ainda não tem conta, lança erro explicativo (mas NUNCA tenta criar)
        if (conta == null) {
            throw new IllegalArgumentException(
                "Não há contas cadastradas no sistema. " +
                "Para criar transações, é necessário ter pelo menos uma conta no banco de dados. " +
                "Crie uma conta diretamente no banco via SQL ou use o endpoint /api/contas/criar (se funcionar)."
            );
        }
        
        // Associa a conta à transação
        transacao.setConta(conta);
        
        // 5️⃣ Define data se não veio
        if (transacao.getDtTransacao() == null) {
            transacao.setDtTransacao(new java.util.Date());
        }
        
        // 6️⃣ Salva
        return transacaoRepository.save(transacao);
    }
    
    public Transacao update(Integer id, Transacao transacaoAtualizada) {
        Transacao transacao = transacaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transação não encontrada com id: " + id));
        
        transacao.setValor(transacaoAtualizada.getValor());
        transacao.setDescricao(transacaoAtualizada.getDescricao());
        transacao.setTipoTransacao(transacaoAtualizada.getTipoTransacao());
        transacao.setConta(transacaoAtualizada.getConta());
        
        return transacaoRepository.save(transacao);
    }
    
    /**
     * Atualiza transação (sem verificação de conta)
     */
    public Transacao updateForUser(Integer id, Transacao transacaoAtualizada, Integer userId) {
        Transacao transacao = transacaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transação não encontrada com id: " + id));
        
        transacao.setValor(transacaoAtualizada.getValor());
        transacao.setDescricao(transacaoAtualizada.getDescricao());
        
        // Atualizar TipoTransacao se fornecido
        if (transacaoAtualizada.getTipoTransacao() != null) {
            transacao.setTipoTransacao(transacaoAtualizada.getTipoTransacao());
        } else if (transacaoAtualizada.getCdTpTransacaoIdTipoTransacao() != null) {
            TipoTransacao tipo = tipoTransacaoRepository.findById(transacaoAtualizada.getCdTpTransacaoIdTipoTransacao())
                    .orElseThrow(() -> new IllegalArgumentException("Tipo de transação não encontrado"));
            transacao.setTipoTransacao(tipo);
        }
        
        // Conta é opcional - pode ser atualizada se fornecida
        if (transacaoAtualizada.getConta() != null) {
            transacao.setConta(transacaoAtualizada.getConta());
        }
        
        return transacaoRepository.save(transacao);
    }
    
    public void delete(Integer id) {
        if (!transacaoRepository.existsById(id)) {
            throw new RuntimeException("Transação não encontrada com id: " + id);
        }
        transacaoRepository.deleteById(id);
    }
    
    /**
     * Deleta transação (sem verificação de conta)
     */
    public void deleteForUser(Integer id, Integer userId) {
        if (!transacaoRepository.existsById(id)) {
            throw new RuntimeException("Transação não encontrada com id: " + id);
        }
        
        transacaoRepository.deleteById(id);
    }
}

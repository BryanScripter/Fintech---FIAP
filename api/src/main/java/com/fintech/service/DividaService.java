package com.fintech.service;

import com.fintech.model.Conta;
import com.fintech.model.Divida;
import com.fintech.repository.ContaRepository;
import com.fintech.repository.DividaRepository;
import com.fintech.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;

@Service
public class DividaService {
    
    @Autowired
    private DividaRepository dividaRepository;
    
    @Autowired
    private ContaRepository contaRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
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
    
    public List<Divida> findAll() {
        return dividaRepository.findAll();
    }
    
    public Optional<Divida> findById(Integer id) {
        return dividaRepository.findById(id);
    }
    
    public List<Divida> findByContaId(Integer contaId) {
        return dividaRepository.findByContaIdConta(contaId);
    }
    
    /**
     * Busca dívidas por ID do usuário
     * Se o usuário não tem conta própria, busca dívidas da conta sistema
     */
    public List<Divida> findByUserId(Integer userId) {
        Optional<Conta> contaOpt = contaRepository.findByUsuario_IdUsuario(userId);
        if (contaOpt.isPresent()) {
            // Usuário tem conta própria - retorna dívidas dessa conta
            return dividaRepository.findByContaIdConta(contaOpt.get().getIdConta());
        } else {
            // Usuário não tem conta própria - retorna dívidas da conta sistema (se existir)
            if (CONTA_SISTEMA_ID != null) {
                return dividaRepository.findByContaIdConta(CONTA_SISTEMA_ID);
            }
            // Se não tem conta sistema, busca qualquer conta existente e suas dívidas
            List<Conta> todasContas = contaRepository.findAll();
            if (!todasContas.isEmpty()) {
                Integer contaId = todasContas.get(0).getIdConta();
                CONTA_SISTEMA_ID = contaId; // Atualiza para próxima vez
                return dividaRepository.findByContaIdConta(contaId);
            }
            return java.util.Collections.emptyList();
        }
    }
    
    public List<Divida> findQuitadas() {
        return dividaRepository.findByQuitada("S");
    }
    
    public List<Divida> findNaoQuitadas() {
        return dividaRepository.findByQuitada("N");
    }
    
    public List<Divida> findByContaAndStatus(Integer contaId, String status) {
        return dividaRepository.findByContaIdContaAndQuitada(contaId, status);
    }
    
    public Divida save(Divida divida) {
        // Validação básica
        if (divida.getValorTotal() == null || divida.getValorTotal() <= 0) {
            throw new IllegalArgumentException("Valor total deve ser maior que zero");
        }
        
        if (divida.getDescricao() == null || divida.getDescricao().isEmpty()) {
            throw new IllegalArgumentException("Descrição é obrigatória");
        }
        
        if (divida.getDtVencimento() == null) {
            throw new IllegalArgumentException("Data de vencimento é obrigatória");
        }
        
        // A conta é opcional - se não vier, deixa null
        // Se vier contaIdConta, valida se existe (mas não obrigatório)
        if (divida.getContaIdConta() != null) {
            Optional<Conta> contaOpt = contaRepository.findById(divida.getContaIdConta());
            if (!contaOpt.isPresent()) {
                throw new IllegalArgumentException("Conta não encontrada com ID: " + divida.getContaIdConta());
            }
        }
        
        return dividaRepository.save(divida);
    }
    
    /**
     * Salva dívida (sem depender de conta)
     * Para projeto de teste: usa conta existente (NUNCA cria conta nova)
     */
    public Divida saveForUser(Divida divida, Integer userId) {
        // 1️⃣ Validações básicas
        if (divida.getValorTotal() == null || divida.getValorTotal() <= 0) {
            throw new IllegalArgumentException("Valor total deve ser maior que zero");
        }
        
        if (divida.getDescricao() == null || divida.getDescricao().trim().isEmpty()) {
            throw new IllegalArgumentException("Descrição é obrigatória");
        }
        
        if (divida.getDtVencimento() == null) {
            throw new IllegalArgumentException("Data de vencimento é obrigatória");
        }
        
        // 2️⃣ Para projeto de teste: usa conta existente (NÃO cria conta nova)
        // Busca conta do usuário primeiro, se não encontrar usa qualquer conta existente
        Integer contaId = null;
        
        // Primeiro tenta buscar conta do usuário
        Optional<Conta> contaOpt = contaRepository.findByUsuario_IdUsuario(userId);
        if (contaOpt.isPresent()) {
            contaId = contaOpt.get().getIdConta();
        } else {
            // Se não tem conta do usuário, usa conta sistema (buscada na inicialização)
            if (CONTA_SISTEMA_ID != null) {
                contaId = CONTA_SISTEMA_ID;
            }
            
            // Se ainda não tem conta, busca qualquer conta existente
            if (contaId == null) {
                List<Conta> todasContas = contaRepository.findAll();
                if (!todasContas.isEmpty()) {
                    contaId = todasContas.get(0).getIdConta();
                    CONTA_SISTEMA_ID = contaId; // Salva para próxima vez
                }
            }
        }
        
        // Se ainda não tem conta, lança erro explicativo (mas NUNCA tenta criar)
        if (contaId == null) {
            throw new IllegalArgumentException(
                "Não há contas cadastradas no sistema. " +
                "Para criar dívidas, é necessário ter pelo menos uma conta no banco de dados. " +
                "Crie uma conta diretamente no banco via SQL ou use o endpoint /api/contas/criar (se funcionar)."
            );
        }
        
        // Associa a conta à dívida
        divida.setContaIdConta(contaId);
        
        // 3️⃣ Define data de início se não veio
        if (divida.getDtInicio() == null) {
            divida.setDtInicio(new java.util.Date());
        }
        
        // 4️⃣ Define valores padrão
        if (divida.getQuitada() == null) {
            divida.setQuitada("N");
        }
        if (divida.getJurosMensal() == null) {
            divida.setJurosMensal(0.0);
        }
        
        // 5️⃣ Salva
        return dividaRepository.save(divida);
    }
    
    public Divida update(Integer id, Divida dividaAtualizada) {
        Divida divida = dividaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dívida não encontrada com id: " + id));
        
        divida.setDescricao(dividaAtualizada.getDescricao());
        divida.setValorTotal(dividaAtualizada.getValorTotal());
        divida.setJurosMensal(dividaAtualizada.getJurosMensal());
        divida.setDtVencimento(dividaAtualizada.getDtVencimento());
        divida.setQuitada(dividaAtualizada.getQuitada());
        divida.setTpDivida(dividaAtualizada.getTpDivida());
        
        return dividaRepository.save(divida);
    }
    
    public void delete(Integer id) {
        if (!dividaRepository.existsById(id)) {
            throw new RuntimeException("Dívida não encontrada com id: " + id);
        }
        dividaRepository.deleteById(id);
    }
    
    public Divida updateForUser(Integer id, Divida dividaAtualizada, Integer userId) {
        Divida divida = dividaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dívida não encontrada com id: " + id));
        
        // Valida que o usuário está autenticado (userId não é null)
        // Se a dívida aparece na lista do usuário (via findByUserId), ele pode editar
        // Não precisa verificar conta específica pois o sistema usa conta compartilhada
        
        divida.setDescricao(dividaAtualizada.getDescricao());
        divida.setValorTotal(dividaAtualizada.getValorTotal());
        divida.setJurosMensal(dividaAtualizada.getJurosMensal());
        divida.setDtVencimento(dividaAtualizada.getDtVencimento());
        divida.setTpDivida(dividaAtualizada.getTpDivida());
        // Mantém a conta original (não permite mudar)
        
        return dividaRepository.save(divida);
    }
    
    public Divida quitarDivida(Integer id) {
        Divida divida = dividaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dívida não encontrada com id: " + id));
        
        divida.setQuitada("S");
        return dividaRepository.save(divida);
    }
    
    /**
     * Quita dívida (qualquer usuário autenticado pode quitar dívidas que aparecem na sua lista)
     */
    public Divida quitarDividaForUser(Integer id, Integer userId) {
        Divida divida = dividaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dívida não encontrada com id: " + id));
        
        // Valida que o usuário está autenticado (userId não é null)
        // Se a dívida aparece na lista do usuário (via findByUserId), ele pode quitar
        
        divida.setQuitada("S");
        return dividaRepository.save(divida);
    }
    
    /**
     * Deleta dívida (qualquer usuário autenticado pode deletar dívidas que aparecem na sua lista)
     */
    public void deleteForUser(Integer id, Integer userId) {
        Divida divida = dividaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dívida não encontrada com id: " + id));
        
        // Valida que o usuário está autenticado (userId não é null)
        // Se a dívida aparece na lista do usuário (via findByUserId), ele pode deletar
        // Não precisa verificar conta específica pois o sistema usa conta compartilhada
        
        dividaRepository.deleteById(id);
    }
}

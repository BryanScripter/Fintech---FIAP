package com.fintech.repository;

import com.fintech.model.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransacaoRepository extends JpaRepository<Transacao, Integer> {
    
    List<Transacao> findByConta_IdConta(Integer contaIdConta);
    
    List<Transacao> findByTipoTransacao_IdTipoTransacao(Integer idTipoTransacao);
}

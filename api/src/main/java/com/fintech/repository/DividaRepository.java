package com.fintech.repository;

import com.fintech.model.Divida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DividaRepository extends JpaRepository<Divida, Integer> {
    
    List<Divida> findByContaIdConta(Integer contaIdConta);
    
    List<Divida> findByQuitada(String quitada);
    
    List<Divida> findByContaIdContaAndQuitada(Integer contaIdConta, String quitada);
}

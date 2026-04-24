package com.fintech.repository;

import com.fintech.model.TipoConta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TipoContaRepository extends JpaRepository<TipoConta, Integer> {
    
    Optional<TipoConta> findById(Integer id);
}


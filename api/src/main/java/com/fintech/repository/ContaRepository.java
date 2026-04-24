package com.fintech.repository;

import com.fintech.model.Conta;
import com.fintech.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContaRepository extends JpaRepository<Conta, Integer> {
    
    Optional<Conta> findByUsuario_IdUsuario(Integer usuarioIdUsuario);
    
    Optional<Conta> findByUsuario(Usuario usuario);
    
    boolean existsById(Integer id);
}

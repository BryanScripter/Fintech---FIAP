package com.fintech.repository;

import com.fintech.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    
    Optional<Usuario> findByEmail(String email);
    
    Optional<Usuario> findByEmailAndSenha(String email, String senha);
    
    boolean existsByEmail(String email);
}

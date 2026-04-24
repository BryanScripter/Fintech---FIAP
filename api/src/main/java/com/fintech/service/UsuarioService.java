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
public class UsuarioService {
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private ContaRepository contaRepository;
    
    @Autowired
    private TipoContaRepository tipoContaRepository;
    
    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }
    
    public Optional<Usuario> findById(Integer id) {
        return usuarioRepository.findById(id);
    }
    
    public Optional<Usuario> findByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }
    
    @Transactional
    public Usuario save(Usuario usuario) {
        // Validação básica
        if (usuario.getEmail() == null || usuario.getEmail().isEmpty()) {
            throw new IllegalArgumentException("Email é obrigatório");
        }
        
        if (usuario.getSenha() == null || usuario.getSenha().isEmpty()) {
            throw new IllegalArgumentException("Senha é obrigatória");
        }
        
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new IllegalArgumentException("Email já cadastrado");
        }
        
        // Salva o usuário
        Usuario usuarioSalvo = usuarioRepository.save(usuario);
        
        // Cria uma conta automaticamente para o novo usuário
        // Verifica se já não existe uma conta para este usuário (evita duplicatas)
        if (!contaRepository.findByUsuario_IdUsuario(usuarioSalvo.getIdUsuario()).isPresent()) {
            // Busca o TipoConta padrão (ID 1 = Conta Corrente)
            TipoConta tipoContaPadrao = tipoContaRepository.findById(1)
                    .orElseGet(() -> {
                        // Se não encontrou, cria o TipoConta padrão
                        TipoConta novo = new TipoConta("Conta Corrente");
                        return tipoContaRepository.save(novo);
                    });
            
            // Cria a nova conta para o usuário
            Conta novaConta = new Conta(usuarioSalvo, tipoContaPadrao);
            // Garantir que ambos os campos de tipo conta estão preenchidos
            if (novaConta.getTipoContaAlternativo() == null) {
                novaConta.setTipoContaAlternativo(tipoContaPadrao);
            }
            contaRepository.save(novaConta);
        }
        
        return usuarioSalvo;
    }
    
    public Usuario update(Integer id, Usuario usuarioAtualizado) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com id: " + id));
        
        usuario.setNome(usuarioAtualizado.getNome());
        usuario.setEmail(usuarioAtualizado.getEmail());
        usuario.setTelefone(usuarioAtualizado.getTelefone());
        usuario.setSenha(usuarioAtualizado.getSenha());
        
        return usuarioRepository.save(usuario);
    }
    
    public void delete(Integer id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuário não encontrado com id: " + id);
        }
        usuarioRepository.deleteById(id);
    }
    
    public Usuario login(String email, String senha) {
        // Login normal - busca usuário por email e senha
        return usuarioRepository.findByEmailAndSenha(email, senha)
                .orElseThrow(() -> new RuntimeException("Email ou senha inválidos"));
    }
    
    public boolean isAdmin(Usuario usuario) {
        return usuario != null && "admin@gmail.com".equalsIgnoreCase(usuario.getEmail());
    }
    
    public boolean isAdmin(Integer usuarioId) {
        if (usuarioId == null) return false;
        return usuarioRepository.findById(usuarioId)
                .map(usuario -> "admin@gmail.com".equalsIgnoreCase(usuario.getEmail()))
                .orElse(false);
    }
    
    public boolean existsByEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }
}

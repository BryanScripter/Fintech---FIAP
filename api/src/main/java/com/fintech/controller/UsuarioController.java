package com.fintech.controller;

import com.fintech.model.Usuario;
import com.fintech.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {
    
    @Autowired
    private UsuarioService usuarioService;
    
    // GET - Teste de conexão
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Backend funcionando!");
    }
    
    // GET - Listar todos os usuários (apenas admin)
    @GetMapping
    public ResponseEntity<?> findAll(@RequestHeader(value = "X-User-Id", required = false) Integer userId) {
        if (userId == null || !usuarioService.isAdmin(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso negado. Apenas administradores podem acessar esta funcionalidade.");
        }
        List<Usuario> usuarios = usuarioService.findAll();
        return ResponseEntity.ok(usuarios);
    }
    
    // POST - Criar novo usuário (qualquer pessoa pode criar usuário normal)
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Usuario usuario) {
        try {
            Usuario novoUsuario = usuarioService.save(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoUsuario);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // GET - Buscar usuário por ID
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> findById(@PathVariable Integer id) {
        return usuarioService.findById(id)
                .map(usuario -> ResponseEntity.ok(usuario))
                .orElse(ResponseEntity.notFound().build());
    }
    
    // GET - Buscar usuário por email
    @GetMapping("/email/{email}")
    public ResponseEntity<Usuario> findByEmail(@PathVariable String email) {
        return usuarioService.findByEmail(email)
                .map(usuario -> ResponseEntity.ok(usuario))
                .orElse(ResponseEntity.notFound().build());
    }
    
    // POST - Criar novo usuário (apenas admin - para gerenciamento)
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Usuario usuario, @RequestHeader(value = "X-User-Id", required = false) Integer userId) {
        if (userId == null || !usuarioService.isAdmin(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso negado. Apenas administradores podem criar usuários.");
        }
        try {
            Usuario novoUsuario = usuarioService.save(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoUsuario);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // POST - Login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        try {
            String email = credentials.get("email");
            String senha = credentials.get("senha");
            Usuario usuario = usuarioService.login(email, senha);
            return ResponseEntity.ok(usuario);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
    
    // GET - Verificar se é admin
    @GetMapping("/check-admin/{id}")
    public ResponseEntity<Map<String, Boolean>> checkAdmin(@PathVariable Integer id) {
        boolean isAdmin = usuarioService.isAdmin(id);
        Map<String, Boolean> response = new HashMap<>();
        response.put("isAdmin", isAdmin);
        return ResponseEntity.ok(response);
    }
    
    // PUT - Atualizar usuário (apenas admin)
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody Usuario usuario, @RequestHeader(value = "X-User-Id", required = false) Integer userId) {
        if (userId == null || !usuarioService.isAdmin(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso negado. Apenas administradores podem atualizar usuários.");
        }
        try {
            Usuario usuarioAtualizado = usuarioService.update(id, usuario);
            return ResponseEntity.ok(usuarioAtualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // DELETE - Deletar usuário (apenas admin)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id, @RequestHeader(value = "X-User-Id", required = false) Integer userId) {
        if (userId == null || !usuarioService.isAdmin(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso negado. Apenas administradores podem deletar usuários.");
        }
        try {
            usuarioService.delete(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}

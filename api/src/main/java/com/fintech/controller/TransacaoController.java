package com.fintech.controller;

import com.fintech.model.Transacao;
import com.fintech.service.TransacaoService;
import com.fintech.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transacoes")
public class TransacaoController {
    
    @Autowired
    private TransacaoService transacaoService;
    
    @Autowired
    private UsuarioService usuarioService;
    
    // GET - Listar transações (admin vê todas, usuário vê apenas as suas)
    @GetMapping
    public ResponseEntity<?> findAll(@RequestHeader(value = "X-User-Id", required = false) Integer userId) {
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não autenticado");
        }
        
        boolean isAdmin = usuarioService.isAdmin(userId);
        List<Transacao> transacoes;
        
        if (isAdmin) {
            // Admin vê todas as transações
            transacoes = transacaoService.findAll();
        } else {
            // Usuário comum vê apenas suas transações
            transacoes = transacaoService.findByUserId(userId);
        }
        
        return ResponseEntity.ok(transacoes);
    }
    
    // GET - Buscar transação por ID
    @GetMapping("/{id}")
    public ResponseEntity<Transacao> findById(@PathVariable Integer id) {
        return transacaoService.findById(id)
                .map(transacao -> ResponseEntity.ok(transacao))
                .orElse(ResponseEntity.notFound().build());
    }
    
    // GET - Buscar transações por conta
    @GetMapping("/conta/{contaId}")
    public ResponseEntity<List<Transacao>> findByContaId(@PathVariable Integer contaId) {
        List<Transacao> transacoes = transacaoService.findByContaId(contaId);
        return ResponseEntity.ok(transacoes);
    }
    
    // GET - Buscar transações por tipo
    @GetMapping("/tipo/{tipoTransacaoId}")
    public ResponseEntity<List<Transacao>> findByTipoTransacao(@PathVariable Integer tipoTransacaoId) {
        List<Transacao> transacoes = transacaoService.findByTipoTransacao(tipoTransacaoId);
        return ResponseEntity.ok(transacoes);
    }
    
    // POST - Criar nova transação (cada usuário cria para si mesmo)
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Transacao transacao, @RequestHeader(value = "X-User-Id", required = false) Integer userId) {
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não autenticado");
        }
        try {
            // Chama o método saveForUser que garante que conta e tipoTransacao sejam associados corretamente
            Transacao novaTransacao = transacaoService.saveForUser(transacao, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(novaTransacao);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    
    // PUT - Atualizar transação (usuário pode editar apenas suas próprias)
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody Transacao transacao, @RequestHeader(value = "X-User-Id", required = false) Integer userId) {
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não autenticado");
        }
        try {
            Transacao transacaoAtualizada = transacaoService.updateForUser(id, transacao, userId);
            return ResponseEntity.ok(transacaoAtualizada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // DELETE - Deletar transação (usuário pode deletar apenas suas próprias)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id, @RequestHeader(value = "X-User-Id", required = false) Integer userId) {
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não autenticado");
        }
        try {
            transacaoService.deleteForUser(id, userId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}

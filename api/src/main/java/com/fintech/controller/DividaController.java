package com.fintech.controller;

import com.fintech.model.Divida;
import com.fintech.service.DividaService;
import com.fintech.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dividas")
public class DividaController {
    
    @Autowired
    private DividaService dividaService;
    
    @Autowired
    private UsuarioService usuarioService;
    
    // GET - Listar dívidas (admin vê todas, usuário vê apenas as suas)
    @GetMapping
    public ResponseEntity<?> findAll(@RequestHeader(value = "X-User-Id", required = false) Integer userId) {
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não autenticado");
        }
        
        boolean isAdmin = usuarioService.isAdmin(userId);
        List<Divida> dividas;
        
        if (isAdmin) {
            // Admin vê todas as dívidas
            dividas = dividaService.findAll();
        } else {
            // Usuário comum vê apenas suas dívidas
            dividas = dividaService.findByUserId(userId);
        }
        
        return ResponseEntity.ok(dividas);
    }
    
    // GET - Buscar dívida por ID
    @GetMapping("/{id}")
    public ResponseEntity<Divida> findById(@PathVariable Integer id) {
        return dividaService.findById(id)
                .map(divida -> ResponseEntity.ok(divida))
                .orElse(ResponseEntity.notFound().build());
    }
    
    // GET - Buscar dívidas por conta
    @GetMapping("/conta/{contaId}")
    public ResponseEntity<List<Divida>> findByContaId(@PathVariable Integer contaId) {
        List<Divida> dividas = dividaService.findByContaId(contaId);
        return ResponseEntity.ok(dividas);
    }
    
    // GET - Buscar dívidas quitadas
    @GetMapping("/quitadas")
    public ResponseEntity<List<Divida>> findQuitadas() {
        List<Divida> dividas = dividaService.findQuitadas();
        return ResponseEntity.ok(dividas);
    }
    
    // GET - Buscar dívidas não quitadas
    @GetMapping("/nao-quitadas")
    public ResponseEntity<List<Divida>> findNaoQuitadas() {
        List<Divida> dividas = dividaService.findNaoQuitadas();
        return ResponseEntity.ok(dividas);
    }
    
    // POST - Criar nova dívida (cada usuário cria para si mesmo)
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Divida divida, @RequestHeader(value = "X-User-Id", required = false) Integer userId) {
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não autenticado");
        }
        try {
            Divida novaDivida = dividaService.saveForUser(divida, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(novaDivida);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // PUT - Atualizar dívida (usuário pode editar apenas suas próprias)
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody Divida divida, @RequestHeader(value = "X-User-Id", required = false) Integer userId) {
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não autenticado");
        }
        try {
            Divida dividaAtualizada = dividaService.updateForUser(id, divida, userId);
            return ResponseEntity.ok(dividaAtualizada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // PUT - Quitar dívida (usuário pode quitar apenas suas próprias)
    @PutMapping("/{id}/quitar")
    public ResponseEntity<?> quitarDivida(@PathVariable Integer id, @RequestHeader(value = "X-User-Id", required = false) Integer userId) {
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não autenticado");
        }
        try {
            Divida divida = dividaService.quitarDividaForUser(id, userId);
            return ResponseEntity.ok(divida);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    
    // DELETE - Deletar dívida (usuário pode deletar apenas suas próprias)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id, @RequestHeader(value = "X-User-Id", required = false) Integer userId) {
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não autenticado");
        }
        try {
            dividaService.deleteForUser(id, userId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}

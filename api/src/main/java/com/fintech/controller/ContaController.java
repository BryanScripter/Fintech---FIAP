package com.fintech.controller;

import com.fintech.model.Conta;
import com.fintech.service.ContaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/contas")
public class ContaController {
    
    @Autowired
    private ContaService contaService;
    
    /**
     * Endpoint para criar conta para o usuário logado
     * Útil para usuários antigos que não têm conta ainda
     */
    @PostMapping("/criar")
    public ResponseEntity<?> criarConta(@RequestHeader(value = "X-User-Id", required = false) Integer userId) {
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não autenticado");
        }
        
        try {
            Conta conta = contaService.criarContaParaUsuario(userId);
            Map<String, Object> response = new java.util.HashMap<>();
            response.put("mensagem", "Conta criada com sucesso");
            response.put("conta", conta);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            Map<String, Object> error = new java.util.HashMap<>();
            error.put("erro", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        }
    }
    
    /**
     * Endpoint para corrigir todas as contas existentes que não têm TipoConta
     * Pode ser chamado uma vez para atualizar o banco de dados
     */
    @PostMapping("/corrigir-tipos")
    public ResponseEntity<?> corrigirContasSemTipo() {
        try {
            int contasCorrigidas = contaService.corrigirContasSemTipo();
            Map<String, Object> response = new java.util.HashMap<>();
            response.put("mensagem", "Contas corrigidas com sucesso");
            response.put("contasCorrigidas", contasCorrigidas);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new java.util.HashMap<>();
            error.put("erro", "Erro ao corrigir contas: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
}


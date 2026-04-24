package com.fintech.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    
    // Trata erros de conversão de tipo (String para Integer, etc)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException e) {
        Map<String, Object> error = new HashMap<>();
        
        String paramName = e.getName();
        String paramValue = String.valueOf(e.getValue());
        
        Class<?> requiredTypeClass = e.getRequiredType();
        String requiredType = "desconhecido";
        if (requiredTypeClass != null) {
            requiredType = requiredTypeClass.getSimpleName();
        }
        
        error.put("message", String.format(
            "Parâmetro '%s' com valor '%s' não pode ser convertido para o tipo %s. " +
            "Verifique se você está enviando um valor válido. Exemplo: para ID, envie um número inteiro.",
            paramName, paramValue, requiredType
        ));
        error.put("error", "ErroDeConversoDeParametro");
        error.put("parametro", paramName);
        error.put("valorEnviado", paramValue);
        error.put("tipoEsperado", requiredType);
        
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
    
    // Trata parâmetros de requisição faltando
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, Object>> handleMissingParams(MissingServletRequestParameterException e) {
        Map<String, Object> error = new HashMap<>();
        error.put("message", "Parâmetro obrigatório faltando: " + e.getParameterName());
        error.put("error", "ParametroObrigatorioFaltando");
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
    
    // Trata outros erros genéricos
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception e) {
        Map<String, Object> error = new HashMap<>();
        error.put("message", e.getMessage());
        error.put("error", e.getClass().getSimpleName());
        e.printStackTrace(); // Log completo no console do IntelliJ
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}


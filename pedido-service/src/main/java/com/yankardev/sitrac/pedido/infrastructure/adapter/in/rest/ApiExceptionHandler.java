package com.yankardev.sitrac.pedido.infrastructure.adapter.in.rest;

import com.yankardev.sitrac.pedido.application.exception.*;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(PedidoNoEncontradoException.class)
    public ResponseEntity<Map<String,Object>> notFound(PedidoNoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<Map<String,Object>> negocio(ReglaNegocioException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,Object>> validacion(MethodArgumentNotValidException ex) {
        Map<String,Object> campos = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(e -> campos.put(e.getField(), e.getDefaultMessage()));
        Map<String,Object> body = new LinkedHashMap<>();
        body.put("error","Datos inválidos");
        body.put("campos",campos);
        return ResponseEntity.badRequest().body(body);
    }
}

package com.yankardev.sitrac.programacion.infrastructure.adapter.in.rest;
import com.yankardev.sitrac.programacion.application.exception.*;import org.springframework.http.*;import org.springframework.web.bind.MethodArgumentNotValidException;import org.springframework.web.bind.annotation.*;import java.util.*;
@RestControllerAdvice public class ApiExceptionHandler{
 @ExceptionHandler(ProgramacionNoEncontradaException.class) public ResponseEntity<Map<String,Object>> nf(ProgramacionNoEncontradaException e){return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error",e.getMessage()));}
 @ExceptionHandler(ReglaNegocioException.class) public ResponseEntity<Map<String,Object>> rn(ReglaNegocioException e){return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error",e.getMessage()));}
 @ExceptionHandler(MethodArgumentNotValidException.class) public ResponseEntity<Map<String,Object>> val(MethodArgumentNotValidException e){Map<String,Object> c=new LinkedHashMap<>();e.getBindingResult().getFieldErrors().forEach(x->c.put(x.getField(),x.getDefaultMessage()));return ResponseEntity.badRequest().body(Map.of("error","Datos inválidos","campos",c));}
}
package com.yankardev.sitrac.combustible.application.exception;

public class AbastecimientoNoEncontradoException extends RuntimeException {
    public AbastecimientoNoEncontradoException(Long id) {
        super("Abastecimiento no encontrado: " + id);
    }
}
package com.yankardev.sitrac.conductor.application.exception;

public class ConductorNoEncontradoException extends RuntimeException {

    public ConductorNoEncontradoException(Long id) {
        super("Conductor no encontrado con id: " + id);
    }
}

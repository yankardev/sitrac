package com.yankardev.sitrac.cliente.application.exception;

public class ClienteNoEncontradoException extends RuntimeException {

    public ClienteNoEncontradoException(Long id) {
        super("Cliente no encontrado con id: " + id);
    }
}

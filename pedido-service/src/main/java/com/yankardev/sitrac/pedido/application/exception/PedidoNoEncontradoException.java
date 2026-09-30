package com.yankardev.sitrac.pedido.application.exception;

public class PedidoNoEncontradoException extends RuntimeException {
    public PedidoNoEncontradoException(Long id) {
        super("Pedido no encontrado con id: " + id);
    }
}

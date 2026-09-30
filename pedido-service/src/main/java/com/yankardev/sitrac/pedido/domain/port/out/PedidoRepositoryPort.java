package com.yankardev.sitrac.pedido.domain.port.out;

import com.yankardev.sitrac.pedido.domain.model.Pedido;
import java.util.List;
import java.util.Optional;

public interface PedidoRepositoryPort {
    Pedido guardar(Pedido pedido);
    List<Pedido> listar();
    Optional<Pedido> buscarPorId(Long id);
    void eliminarPorId(Long id);
}

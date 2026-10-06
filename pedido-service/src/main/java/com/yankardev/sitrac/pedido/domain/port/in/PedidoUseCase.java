package com.yankardev.sitrac.pedido.domain.port.in;

import com.yankardev.sitrac.pedido.domain.model.EstadoPedido;
import com.yankardev.sitrac.pedido.domain.model.Pedido;
import java.util.List;

public interface PedidoUseCase {
    Pedido crear(Pedido pedido);
    List<Pedido> listar();
    Pedido obtenerPorId(Long id);
    Pedido actualizar(Long id, Pedido pedido);
    Pedido cambiarEstado(Long id, EstadoPedido estado);
    void eliminar(Long id);
}

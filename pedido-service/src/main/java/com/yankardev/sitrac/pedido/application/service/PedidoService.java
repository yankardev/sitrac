package com.yankardev.sitrac.pedido.application.service;

import com.yankardev.sitrac.pedido.application.exception.PedidoNoEncontradoException;
import com.yankardev.sitrac.pedido.application.exception.ReglaNegocioException;
import com.yankardev.sitrac.pedido.domain.model.EstadoPedido;
import com.yankardev.sitrac.pedido.domain.model.Pedido;
import com.yankardev.sitrac.pedido.domain.port.in.PedidoUseCase;
import com.yankardev.sitrac.pedido.domain.port.out.PedidoRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PedidoService implements PedidoUseCase {
    private final PedidoRepositoryPort repository;

    @Override
    public Pedido crear(Pedido pedido) {
        validar(pedido);
        Pedido nuevo = Pedido.builder()
                .clienteId(pedido.getClienteId())
                .tipoCarga(pedido.getTipoCarga())
                .descripcionCarga(pedido.getDescripcionCarga())
                .toneladas(pedido.getToneladas())
                .origen(pedido.getOrigen())
                .destino(pedido.getDestino())
                .fechaSolicitud(pedido.getFechaSolicitud())
                .estado(EstadoPedido.REGISTRADO)
                .build();
        return repository.guardar(nuevo);
    }

    @Override public List<Pedido> listar() { return repository.listar(); }

    @Override
    public Pedido obtenerPorId(Long id) {
        return repository.buscarPorId(id).orElseThrow(() -> new PedidoNoEncontradoException(id));
    }

    @Override
    public Pedido actualizar(Long id, Pedido pedido) {
        Pedido actual = obtenerPorId(id);
        validar(pedido);
        Pedido actualizado = Pedido.builder()
                .id(actual.getId())
                .clienteId(pedido.getClienteId())
                .tipoCarga(pedido.getTipoCarga())
                .descripcionCarga(pedido.getDescripcionCarga())
                .toneladas(pedido.getToneladas())
                .origen(pedido.getOrigen())
                .destino(pedido.getDestino())
                .fechaSolicitud(pedido.getFechaSolicitud())
                .estado(pedido.getEstado() == null ? actual.getEstado() : pedido.getEstado())
                .build();
        return repository.guardar(actualizado);
    }

    @Override
    public void eliminar(Long id) {
        obtenerPorId(id);
        repository.eliminarPorId(id);
    }

    private void validar(Pedido pedido) {
        if (pedido.getToneladas() == null || pedido.getToneladas().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ReglaNegocioException("Las toneladas deben ser mayores que cero");
        }
        if (pedido.getOrigen() != null && pedido.getOrigen().equalsIgnoreCase(pedido.getDestino())) {
            throw new ReglaNegocioException("El origen y el destino no pueden ser iguales");
        }
    }
}

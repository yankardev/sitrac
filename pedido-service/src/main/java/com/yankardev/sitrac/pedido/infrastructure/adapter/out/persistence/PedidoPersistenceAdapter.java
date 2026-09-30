package com.yankardev.sitrac.pedido.infrastructure.adapter.out.persistence;

import com.yankardev.sitrac.pedido.domain.model.Pedido;
import com.yankardev.sitrac.pedido.domain.port.out.PedidoRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PedidoPersistenceAdapter implements PedidoRepositoryPort {
    private final PedidoJpaRepository repository;

    @Override public Pedido guardar(Pedido p) { return toDomain(repository.save(toEntity(p))); }
    @Override public List<Pedido> listar() { return repository.findAll().stream().map(this::toDomain).toList(); }
    @Override public Optional<Pedido> buscarPorId(Long id) { return repository.findById(id).map(this::toDomain); }
    @Override public void eliminarPorId(Long id) { repository.deleteById(id); }

    private PedidoJpaEntity toEntity(Pedido p) {
        return PedidoJpaEntity.builder()
                .id(p.getId()).clienteId(p.getClienteId()).tipoCarga(p.getTipoCarga())
                .descripcionCarga(p.getDescripcionCarga()).toneladas(p.getToneladas())
                .origen(p.getOrigen()).destino(p.getDestino()).fechaSolicitud(p.getFechaSolicitud())
                .estado(p.getEstado()).build();
    }

    private Pedido toDomain(PedidoJpaEntity e) {
        return Pedido.builder()
                .id(e.getId()).clienteId(e.getClienteId()).tipoCarga(e.getTipoCarga())
                .descripcionCarga(e.getDescripcionCarga()).toneladas(e.getToneladas())
                .origen(e.getOrigen()).destino(e.getDestino()).fechaSolicitud(e.getFechaSolicitud())
                .estado(e.getEstado()).build();
    }
}

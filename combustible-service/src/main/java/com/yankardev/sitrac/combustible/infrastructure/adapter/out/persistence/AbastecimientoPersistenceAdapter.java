package com.yankardev.sitrac.combustible.infrastructure.adapter.out.persistence;

import com.yankardev.sitrac.combustible.domain.model.AbastecimientoCombustible;
import com.yankardev.sitrac.combustible.domain.port.out.AbastecimientoCombustibleRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AbastecimientoPersistenceAdapter implements AbastecimientoCombustibleRepositoryPort {
    private final AbastecimientoJpaRepository repository;

    @Override
    public AbastecimientoCombustible guardar(AbastecimientoCombustible a) {
        return toDomain(repository.save(toEntity(a)));
    }

    @Override
    public List<AbastecimientoCombustible> listar() {
        return repository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<AbastecimientoCombustible> buscarPorId(Long id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public void eliminarPorId(Long id) {
        repository.deleteById(id);
    }

    private AbastecimientoJpaEntity toEntity(AbastecimientoCombustible a) {
        return AbastecimientoJpaEntity.builder()
                .id(a.getId())
                .programacionId(a.getProgramacionId())
                .viajeId(a.getViajeId())
                .conductorId(a.getConductorId())
                .tractoId(a.getTractoId())
                .tipoAbastecimiento(a.getTipoAbastecimiento())
                .fechaHora(a.getFechaHora())
                .cantidadGalones(a.getCantidadGalones())
                .kilometraje(a.getKilometraje())
                .precioUnitario(a.getPrecioUnitario())
                .costoTotal(a.getCostoTotal())
                .tanqueOrigen(a.getTanqueOrigen())
                .proveedor(a.getProveedor())
                .numeroComprobante(a.getNumeroComprobante())
                .observacion(a.getObservacion())
                .build();
    }

    private AbastecimientoCombustible toDomain(AbastecimientoJpaEntity e) {
        return AbastecimientoCombustible.builder()
                .id(e.getId())
                .programacionId(e.getProgramacionId())
                .viajeId(e.getViajeId())
                .conductorId(e.getConductorId())
                .tractoId(e.getTractoId())
                .tipoAbastecimiento(e.getTipoAbastecimiento())
                .fechaHora(e.getFechaHora())
                .cantidadGalones(e.getCantidadGalones())
                .kilometraje(e.getKilometraje())
                .precioUnitario(e.getPrecioUnitario())
                .costoTotal(e.getCostoTotal())
                .tanqueOrigen(e.getTanqueOrigen())
                .proveedor(e.getProveedor())
                .numeroComprobante(e.getNumeroComprobante())
                .observacion(e.getObservacion())
                .build();
    }
}
package com.yankardev.sitrac.combustible.application.service;

import com.yankardev.sitrac.combustible.application.exception.AbastecimientoNoEncontradoException;
import com.yankardev.sitrac.combustible.application.exception.ReglaNegocioException;
import com.yankardev.sitrac.combustible.domain.model.AbastecimientoCombustible;
import com.yankardev.sitrac.combustible.domain.model.TipoAbastecimiento;
import com.yankardev.sitrac.combustible.domain.port.in.AbastecimientoCombustibleUseCase;
import com.yankardev.sitrac.combustible.domain.port.out.AbastecimientoCombustibleRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AbastecimientoCombustibleService implements AbastecimientoCombustibleUseCase {
    private final AbastecimientoCombustibleRepositoryPort repository;

    @Override
    public AbastecimientoCombustible crear(AbastecimientoCombustible abastecimiento) {
        validar(abastecimiento);
        return repository.guardar(normalizar(abastecimiento, null));
    }

    @Override
    public List<AbastecimientoCombustible> listar() {
        return repository.listar();
    }

    @Override
    public AbastecimientoCombustible obtenerPorId(Long id) {
        return repository.buscarPorId(id)
                .orElseThrow(() -> new AbastecimientoNoEncontradoException(id));
    }

    @Override
    public AbastecimientoCombustible actualizar(Long id, AbastecimientoCombustible abastecimiento) {
        obtenerPorId(id);
        validar(abastecimiento);
        return repository.guardar(normalizar(abastecimiento, id));
    }

    @Override
    public void eliminar(Long id) {
        obtenerPorId(id);
        repository.eliminarPorId(id);
    }

    private void validar(AbastecimientoCombustible a) {
        if (a.getTipoAbastecimiento() == TipoAbastecimiento.INTERNO
                && (a.getTanqueOrigen() == null || a.getTanqueOrigen().isBlank())) {
            throw new ReglaNegocioException("El abastecimiento interno debe indicar el tanque de origen");
        }
        if (a.getTipoAbastecimiento() == TipoAbastecimiento.TERCERO) {
            if (a.getProveedor() == null || a.getProveedor().isBlank()) {
                throw new ReglaNegocioException("La compra a tercero debe indicar el proveedor");
            }
            if (a.getNumeroComprobante() == null || a.getNumeroComprobante().isBlank()) {
                throw new ReglaNegocioException("La compra a tercero debe indicar el número de comprobante");
            }
        }
    }

    private AbastecimientoCombustible normalizar(AbastecimientoCombustible a, Long id) {
        BigDecimal costoTotal = a.getPrecioUnitario() == null
                ? null
                : a.getCantidadGalones().multiply(a.getPrecioUnitario()).setScale(2, RoundingMode.HALF_UP);

        return AbastecimientoCombustible.builder()
                .id(id)
                .programacionId(a.getProgramacionId())
                .viajeId(a.getViajeId())
                .conductorId(a.getConductorId())
                .tractoId(a.getTractoId())
                .tipoAbastecimiento(a.getTipoAbastecimiento())
                .fechaHora(a.getFechaHora())
                .cantidadGalones(a.getCantidadGalones())
                .kilometraje(a.getKilometraje())
                .precioUnitario(a.getPrecioUnitario())
                .costoTotal(costoTotal)
                .tanqueOrigen(limpiar(a.getTanqueOrigen()))
                .proveedor(limpiar(a.getProveedor()))
                .numeroComprobante(limpiar(a.getNumeroComprobante()))
                .observacion(limpiar(a.getObservacion()))
                .build();
    }

    private String limpiar(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }
}
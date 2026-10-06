package com.yankardev.sitrac.combustible.domain.port.in;

import com.yankardev.sitrac.combustible.domain.model.AbastecimientoCombustible;
import java.util.List;

public interface AbastecimientoCombustibleUseCase {
    AbastecimientoCombustible crear(AbastecimientoCombustible abastecimiento);
    List<AbastecimientoCombustible> listar();
    AbastecimientoCombustible obtenerPorId(Long id);
    AbastecimientoCombustible actualizar(Long id, AbastecimientoCombustible abastecimiento);
    void eliminar(Long id);
}
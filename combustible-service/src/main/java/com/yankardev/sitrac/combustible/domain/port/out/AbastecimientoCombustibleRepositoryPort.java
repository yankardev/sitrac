package com.yankardev.sitrac.combustible.domain.port.out;

import com.yankardev.sitrac.combustible.domain.model.AbastecimientoCombustible;
import java.util.List;
import java.util.Optional;

public interface AbastecimientoCombustibleRepositoryPort {
    AbastecimientoCombustible guardar(AbastecimientoCombustible abastecimiento);
    List<AbastecimientoCombustible> listar();
    Optional<AbastecimientoCombustible> buscarPorId(Long id);
    void eliminarPorId(Long id);
}
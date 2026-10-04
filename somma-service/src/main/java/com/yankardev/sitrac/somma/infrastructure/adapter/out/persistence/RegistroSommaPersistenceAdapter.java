package com.yankardev.sitrac.somma.infrastructure.adapter.out.persistence;

import com.yankardev.sitrac.somma.domain.model.RegistroSomma;
import com.yankardev.sitrac.somma.domain.port.out.RegistroSommaRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RegistroSommaPersistenceAdapter implements RegistroSommaRepositoryPort {

    private final RegistroSommaJpaRepository repo;

    @Override
    public RegistroSomma guardar(RegistroSomma registro) {
        return toDomain(repo.save(toEntity(registro)));
    }

    @Override
    public List<RegistroSomma> listar() {
        return repo.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<RegistroSomma> buscarPorId(Long id) {
        return repo.findById(id).map(this::toDomain);
    }

    @Override
    public void eliminarPorId(Long id) {
        repo.deleteById(id);
    }

    private RegistroSommaJpaEntity toEntity(RegistroSomma registro) {
        return RegistroSommaJpaEntity.builder()
                .id(registro.getId())
                .tipo(registro.getTipo())
                .programacionId(registro.getProgramacionId())
                .conductorId(registro.getConductorId())
                .fecha(registro.getFecha())
                .titulo(registro.getTitulo())
                .descripcion(registro.getDescripcion())
                .lugar(registro.getLugar())
                .estado(registro.getEstado())
                .build();
    }

    private RegistroSomma toDomain(RegistroSommaJpaEntity entity) {
        return RegistroSomma.builder()
                .id(entity.getId())
                .tipo(entity.getTipo())
                .programacionId(entity.getProgramacionId())
                .conductorId(entity.getConductorId())
                .fecha(entity.getFecha())
                .titulo(entity.getTitulo())
                .descripcion(entity.getDescripcion())
                .lugar(entity.getLugar())
                .estado(entity.getEstado())
                .build();
    }
}

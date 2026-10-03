package com.yankardev.sitrac.somma.infrastructure.adapter.out.persistence;
import com.yankardev.sitrac.somma.domain.model.RegistroSomma;import com.yankardev.sitrac.somma.domain.port.out.RegistroSommaRepositoryPort;import lombok.RequiredArgsConstructor;import org.springframework.stereotype.Component;import java.util.*;
@Component @RequiredArgsConstructor
public class RegistroSommaPersistenceAdapter implements RegistroSommaRepositoryPort{
 private final RegistroSommaJpaRepository repo;public RegistroSomma guardar(RegistroSomma r){return d(repo.save(e(r)));}public List<RegistroSomma> listar(){return repo.findAll().stream().map(this::d).toList();}public Optional<RegistroSomma> buscarPorId(Long id){return repo.findById(id).map(this::d);}public void eliminarPorId(Long id){repo.deleteById(id);}
 private RegistroSommaJpaEntity e(RegistroSomma r){return RegistroSommaJpaEntity.builder().id(r.getId()).tipo(r.getTipo()).conductorId(r.getConductorId()).fecha(r.getFecha()).titulo(r.getTitulo()).descripcion(r.getDescripcion()).lugar(r.getLugar()).estado(r.getEstado()).build();}
 private RegistroSomma d(RegistroSommaJpaEntity e){return RegistroSomma.builder().id(e.getId()).tipo(e.getTipo()).conductorId(e.getConductorId()).fecha(e.getFecha()).titulo(e.getTitulo()).descripcion(e.getDescripcion()).lugar(e.getLugar()).estado(e.getEstado()).build();}
}
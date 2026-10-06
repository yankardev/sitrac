package com.yankardev.sitrac.flota.infrastructure.adapter.out.persistence;
import com.yankardev.sitrac.flota.domain.model.Tracto; import com.yankardev.sitrac.flota.domain.port.out.TractoRepositoryPort;
import lombok.RequiredArgsConstructor; import org.springframework.stereotype.Component; import java.util.*;
@Component @RequiredArgsConstructor
public class TractoPersistenceAdapter implements TractoRepositoryPort {
 private final TractoJpaRepository repo;
 public Tracto guardar(Tracto t){return d(repo.save(e(t)));} public List<Tracto> listar(){return repo.findAll().stream().map(this::d).toList();}
 public Optional<Tracto> buscarPorId(Long id){return repo.findById(id).map(this::d);} public boolean existePorPlaca(String p){return repo.existsByPlaca(p);}
 public boolean existePorPlacaYIdDistinto(String p,Long id){return repo.existsByPlacaAndIdNot(p,id);} public void eliminarPorId(Long id){repo.deleteById(id);}
 private TractoJpaEntity e(Tracto t){return TractoJpaEntity.builder().id(t.getId()).placa(t.getPlaca()).marca(t.getMarca()).modelo(t.getModelo()).anio(t.getAnio()).capacidadToneladas(t.getCapacidadToneladas()).estado(t.getEstado()).activo(t.isActivo()).build();}
 private Tracto d(TractoJpaEntity e){return Tracto.builder().id(e.getId()).placa(e.getPlaca()).marca(e.getMarca()).modelo(e.getModelo()).anio(e.getAnio()).capacidadToneladas(e.getCapacidadToneladas()).estado(e.getEstado()).activo(e.isActivo()).build();}
}
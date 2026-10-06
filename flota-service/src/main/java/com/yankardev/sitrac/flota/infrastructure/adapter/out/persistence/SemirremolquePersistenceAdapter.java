package com.yankardev.sitrac.flota.infrastructure.adapter.out.persistence;
import com.yankardev.sitrac.flota.domain.model.Semirremolque; import com.yankardev.sitrac.flota.domain.port.out.SemirremolqueRepositoryPort;
import lombok.RequiredArgsConstructor; import org.springframework.stereotype.Component; import java.util.*;
@Component @RequiredArgsConstructor
public class SemirremolquePersistenceAdapter implements SemirremolqueRepositoryPort {
 private final SemirremolqueJpaRepository repo;
 public Semirremolque guardar(Semirremolque s){return d(repo.save(e(s)));} public List<Semirremolque> listar(){return repo.findAll().stream().map(this::d).toList();}
 public Optional<Semirremolque> buscarPorId(Long id){return repo.findById(id).map(this::d);} public boolean existePorPlaca(String p){return repo.existsByPlaca(p);}
 public boolean existePorPlacaYIdDistinto(String p,Long id){return repo.existsByPlacaAndIdNot(p,id);} public void eliminarPorId(Long id){repo.deleteById(id);}
 private SemirremolqueJpaEntity e(Semirremolque s){return SemirremolqueJpaEntity.builder().id(s.getId()).placa(s.getPlaca()).tipo(s.getTipo()).capacidadToneladas(s.getCapacidadToneladas()).estado(s.getEstado()).activo(s.isActivo()).build();}
 private Semirremolque d(SemirremolqueJpaEntity e){return Semirremolque.builder().id(e.getId()).placa(e.getPlaca()).tipo(e.getTipo()).capacidadToneladas(e.getCapacidadToneladas()).estado(e.getEstado()).activo(e.isActivo()).build();}
}
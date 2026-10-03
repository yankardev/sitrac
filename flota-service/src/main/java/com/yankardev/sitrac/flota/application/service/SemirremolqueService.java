package com.yankardev.sitrac.flota.application.service;
import com.yankardev.sitrac.flota.application.exception.*; import com.yankardev.sitrac.flota.domain.model.*;
import com.yankardev.sitrac.flota.domain.port.in.SemirremolqueUseCase; import com.yankardev.sitrac.flota.domain.port.out.SemirremolqueRepositoryPort;
import lombok.RequiredArgsConstructor; import org.springframework.stereotype.Service; import java.util.List;
@Service @RequiredArgsConstructor
public class SemirremolqueService implements SemirremolqueUseCase {
    private final SemirremolqueRepositoryPort repo;
    public Semirremolque crear(Semirremolque s){if(repo.existePorPlaca(s.getPlaca())) throw new ReglaNegocioException("Ya existe un semirremolque con la placa "+s.getPlaca());
        return repo.guardar(Semirremolque.builder().placa(s.getPlaca()).tipo(s.getTipo()).capacidadToneladas(s.getCapacidadToneladas()).estado(EstadoUnidad.DISPONIBLE).activo(true).build());}
    public List<Semirremolque> listar(){return repo.listar();}
    public Semirremolque obtenerPorId(Long id){return repo.buscarPorId(id).orElseThrow(()->new RecursoNoEncontradoException("Semirremolque",id));}
    public Semirremolque actualizar(Long id,Semirremolque s){Semirremolque a=obtenerPorId(id); if(repo.existePorPlacaYIdDistinto(s.getPlaca(),id)) throw new ReglaNegocioException("Ya existe otro semirremolque con la placa "+s.getPlaca());
        return repo.guardar(Semirremolque.builder().id(a.getId()).placa(s.getPlaca()).tipo(s.getTipo()).capacidadToneladas(s.getCapacidadToneladas()).estado(s.getEstado()==null?a.getEstado():s.getEstado()).activo(s.isActivo()).build());}
    public void eliminar(Long id){obtenerPorId(id);repo.eliminarPorId(id);}
}
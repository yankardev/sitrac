package com.yankardev.sitrac.flota.application.service;
import com.yankardev.sitrac.flota.application.exception.*; import com.yankardev.sitrac.flota.domain.model.*;
import com.yankardev.sitrac.flota.domain.port.in.TractoUseCase; import com.yankardev.sitrac.flota.domain.port.out.TractoRepositoryPort;
import lombok.RequiredArgsConstructor; import org.springframework.stereotype.Service; import java.util.List;
@Service @RequiredArgsConstructor
public class TractoService implements TractoUseCase {
    private final TractoRepositoryPort repo;
    public Tracto crear(Tracto t){ if(repo.existePorPlaca(t.getPlaca())) throw new ReglaNegocioException("Ya existe un tracto con la placa "+t.getPlaca());
        return repo.guardar(Tracto.builder().placa(t.getPlaca()).marca(t.getMarca()).modelo(t.getModelo()).anio(t.getAnio()).capacidadToneladas(t.getCapacidadToneladas()).estado(EstadoUnidad.DISPONIBLE).activo(true).build());}
    public List<Tracto> listar(){return repo.listar();}
    public Tracto obtenerPorId(Long id){return repo.buscarPorId(id).orElseThrow(()->new RecursoNoEncontradoException("Tracto",id));}
    public Tracto actualizar(Long id,Tracto t){Tracto a=obtenerPorId(id); if(repo.existePorPlacaYIdDistinto(t.getPlaca(),id)) throw new ReglaNegocioException("Ya existe otro tracto con la placa "+t.getPlaca());
        return repo.guardar(Tracto.builder().id(a.getId()).placa(t.getPlaca()).marca(t.getMarca()).modelo(t.getModelo()).anio(t.getAnio()).capacidadToneladas(t.getCapacidadToneladas()).estado(t.getEstado()==null?a.getEstado():t.getEstado()).activo(t.isActivo()).build());}
    public void eliminar(Long id){obtenerPorId(id);repo.eliminarPorId(id);}
}
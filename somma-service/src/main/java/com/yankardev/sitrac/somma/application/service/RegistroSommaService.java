package com.yankardev.sitrac.somma.application.service;
import com.yankardev.sitrac.somma.application.exception.RegistroSommaNoEncontradoException;import com.yankardev.sitrac.somma.domain.model.*;import com.yankardev.sitrac.somma.domain.port.in.RegistroSommaUseCase;import com.yankardev.sitrac.somma.domain.port.out.RegistroSommaRepositoryPort;
import lombok.RequiredArgsConstructor;import org.springframework.stereotype.Service;import java.util.List;
@Service @RequiredArgsConstructor
public class RegistroSommaService implements RegistroSommaUseCase{
 private final RegistroSommaRepositoryPort repo;
 public RegistroSomma crear(RegistroSomma r){return repo.guardar(RegistroSomma.builder().tipo(r.getTipo()).conductorId(r.getConductorId()).fecha(r.getFecha()).titulo(r.getTitulo()).descripcion(r.getDescripcion()).lugar(r.getLugar()).estado(EstadoRegistroSomma.REGISTRADO).build());}
 public List<RegistroSomma> listar(){return repo.listar();}
 public RegistroSomma obtenerPorId(Long id){return repo.buscarPorId(id).orElseThrow(()->new RegistroSommaNoEncontradoException(id));}
 public RegistroSomma actualizar(Long id,RegistroSomma r){RegistroSomma a=obtenerPorId(id);return repo.guardar(RegistroSomma.builder().id(a.getId()).tipo(r.getTipo()).conductorId(r.getConductorId()).fecha(r.getFecha()).titulo(r.getTitulo()).descripcion(r.getDescripcion()).lugar(r.getLugar()).estado(r.getEstado()==null?a.getEstado():r.getEstado()).build());}
 public void eliminar(Long id){obtenerPorId(id);repo.eliminarPorId(id);}
}
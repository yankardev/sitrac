package com.yankardev.sitrac.flota.domain.port.out;
import com.yankardev.sitrac.flota.domain.model.Tracto; import java.util.*;
public interface TractoRepositoryPort { Tracto guardar(Tracto t); List<Tracto> listar(); Optional<Tracto> buscarPorId(Long id); boolean existePorPlaca(String placa); boolean existePorPlacaYIdDistinto(String placa,Long id); void eliminarPorId(Long id); }
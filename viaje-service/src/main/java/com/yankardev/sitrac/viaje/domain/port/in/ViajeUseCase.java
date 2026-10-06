package com.yankardev.sitrac.viaje.domain.port.in;
import com.yankardev.sitrac.viaje.domain.model.Viaje;import java.util.List;
public interface ViajeUseCase{Viaje crear(Viaje v);List<Viaje> listar();Viaje obtenerPorId(Long id);Viaje actualizar(Long id,Viaje v);void eliminar(Long id);}
package com.yankardev.sitrac.programacion.application.service;

import com.yankardev.sitrac.programacion.application.exception.ProgramacionNoEncontradaException;
import com.yankardev.sitrac.programacion.application.exception.ReglaNegocioException;
import com.yankardev.sitrac.programacion.domain.model.EstadoProgramacion;
import com.yankardev.sitrac.programacion.domain.model.Programacion;
import com.yankardev.sitrac.programacion.domain.port.in.ProgramacionUseCase;
import com.yankardev.sitrac.programacion.domain.port.out.OperacionConsultaPort;
import com.yankardev.sitrac.programacion.domain.port.out.ProgramacionRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ProgramacionService implements ProgramacionUseCase {

    private static final String DISPONIBLE = "DISPONIBLE";
    private static final Map<String, String> COMPATIBILIDAD_CARGA = Map.of(
            "CAL_GRANEL", "BOMBONA",
            "CEMENTO_BOLSA", "PLATAFORMA",
            "MAQUINARIA", "CAMA_BAJA",
            "CARGA_ANCHA", "CAMA_BAJA",
            "ESPECIAL", "CAMA_BAJA"
    );

    private final ProgramacionRepositoryPort repo;
    private final OperacionConsultaPort consulta;

    @Override
    public Programacion crear(Programacion p) {
        validarDisponibilidadLocal(p, null);
        validarIntegracion(p, true);

        Programacion creada = repo.guardar(Programacion.builder()
                .pedidoId(p.getPedidoId())
                .conductorId(p.getConductorId())
                .tractoId(p.getTractoId())
                .semirremolqueId(p.getSemirremolqueId())
                .fechaProgramada(p.getFechaProgramada())
                .observacion(p.getObservacion())
                .estado(EstadoProgramacion.PROGRAMADA)
                .build());

        consulta.cambiarEstadoPedido(p.getPedidoId(), "PROGRAMADO");
        consulta.cambiarDisponibilidadConductor(p.getConductorId(), false);
        consulta.cambiarEstadoTracto(p.getTractoId(), "ASIGNADO");
        consulta.cambiarEstadoSemirremolque(p.getSemirremolqueId(), "ASIGNADO");
        return creada;
    }

    @Override
    public List<Programacion> listar() {
        return repo.listar();
    }

    @Override
    public Programacion obtenerPorId(Long id) {
        return repo.buscarPorId(id)
                .orElseThrow(() -> new ProgramacionNoEncontradaException(id));
    }

    @Override
    public Programacion actualizar(Long id, Programacion p) {
        Programacion actual = obtenerPorId(id);
        EstadoProgramacion nuevoEstado = p.getEstado() == null ? actual.getEstado() : p.getEstado();

        validarAsignacionInmutable(actual, p);

        if (actual.getEstado() == EstadoProgramacion.CANCELADA
                && nuevoEstado != EstadoProgramacion.CANCELADA) {
            throw new ReglaNegocioException(
                    "Una programación cancelada no puede volver a activarse; registre una nueva programación"
            );
        }

        Programacion actualizada = repo.guardar(Programacion.builder()
                .id(actual.getId())
                .pedidoId(actual.getPedidoId())
                .conductorId(actual.getConductorId())
                .tractoId(actual.getTractoId())
                .semirremolqueId(actual.getSemirremolqueId())
                .fechaProgramada(p.getFechaProgramada())
                .observacion(p.getObservacion())
                .estado(nuevoEstado)
                .build());

        if (actual.getEstado() == EstadoProgramacion.PROGRAMADA
                && nuevoEstado == EstadoProgramacion.CANCELADA) {
            consulta.cambiarEstadoPedido(actual.getPedidoId(), "REGISTRADO");
            consulta.cambiarDisponibilidadConductor(actual.getConductorId(), true);
            consulta.cambiarEstadoTracto(actual.getTractoId(), "DISPONIBLE");
            consulta.cambiarEstadoSemirremolque(actual.getSemirremolqueId(), "DISPONIBLE");
        }

        return actualizada;
    }

    @Override
    public void eliminar(Long id) {
        Programacion actual = obtenerPorId(id);

        if (actual.getEstado() == EstadoProgramacion.PROGRAMADA) {
            throw new ReglaNegocioException(
                    "No se puede eliminar una programación activa; primero debe cancelarla"
            );
        }

        repo.eliminarPorId(id);
    }

    private void validarAsignacionInmutable(Programacion actual, Programacion nueva) {
        boolean cambioAsignacion = !Objects.equals(actual.getPedidoId(), nueva.getPedidoId())
                || !Objects.equals(actual.getConductorId(), nueva.getConductorId())
                || !Objects.equals(actual.getTractoId(), nueva.getTractoId())
                || !Objects.equals(actual.getSemirremolqueId(), nueva.getSemirremolqueId());

        if (cambioAsignacion) {
            throw new ReglaNegocioException(
                    "No se pueden cambiar los recursos de una programación existente; cancélela y registre una nueva"
            );
        }
    }

    private void validarDisponibilidadLocal(Programacion p, Long programacionIdActual) {
        boolean pedidoOcupado = programacionIdActual == null
                ? repo.existePedidoConEstado(p.getPedidoId(), EstadoProgramacion.PROGRAMADA)
                : repo.existePedidoConEstadoExcepto(p.getPedidoId(), EstadoProgramacion.PROGRAMADA, programacionIdActual);

        if (pedidoOcupado) {
            throw new ReglaNegocioException("El pedido ya tiene una programación activa");
        }

        boolean conductorOcupado = programacionIdActual == null
                ? repo.existeConductorConEstado(p.getConductorId(), EstadoProgramacion.PROGRAMADA)
                : repo.existeConductorConEstadoExcepto(p.getConductorId(), EstadoProgramacion.PROGRAMADA, programacionIdActual);

        if (conductorOcupado) {
            throw new ReglaNegocioException("El conductor ya tiene una programación activa");
        }

        boolean tractoOcupado = programacionIdActual == null
                ? repo.existeTractoConEstado(p.getTractoId(), EstadoProgramacion.PROGRAMADA)
                : repo.existeTractoConEstadoExcepto(p.getTractoId(), EstadoProgramacion.PROGRAMADA, programacionIdActual);

        if (tractoOcupado) {
            throw new ReglaNegocioException("El tracto ya tiene una programación activa");
        }

        boolean semirremolqueOcupado = programacionIdActual == null
                ? repo.existeSemirremolqueConEstado(p.getSemirremolqueId(), EstadoProgramacion.PROGRAMADA)
                : repo.existeSemirremolqueConEstadoExcepto(p.getSemirremolqueId(), EstadoProgramacion.PROGRAMADA, programacionIdActual);

        if (semirremolqueOcupado) {
            throw new ReglaNegocioException("El semirremolque ya tiene una programación activa");
        }
    }

    private void validarIntegracion(Programacion p, boolean creacion) {
        OperacionConsultaPort.PedidoOperacion pedido = consulta.buscarPedido(p.getPedidoId())
                .orElseThrow(() -> new ReglaNegocioException("El pedido indicado no existe"));

        if (creacion && !"REGISTRADO".equals(pedido.estado())) {
            throw new ReglaNegocioException("Solo se puede programar un pedido en estado REGISTRADO");
        }

        if (!creacion && !"REGISTRADO".equals(pedido.estado()) && !"PROGRAMADO".equals(pedido.estado())) {
            throw new ReglaNegocioException("El pedido no se encuentra en un estado válido para programación");
        }

        OperacionConsultaPort.ConductorOperacion conductor = consulta.buscarConductor(p.getConductorId())
                .orElseThrow(() -> new ReglaNegocioException("El conductor indicado no existe"));

        if (!conductor.activo()) {
            throw new ReglaNegocioException("El conductor se encuentra inactivo");
        }

        if (!conductor.disponible()) {
            throw new ReglaNegocioException("El conductor no se encuentra disponible");
        }

        if (conductor.fechaVencimientoLicencia() == null
                || conductor.fechaVencimientoLicencia().isBefore(LocalDate.now())) {
            throw new ReglaNegocioException("La licencia del conductor no está vigente");
        }

        OperacionConsultaPort.TractoOperacion tracto = consulta.buscarTracto(p.getTractoId())
                .orElseThrow(() -> new ReglaNegocioException("El tracto indicado no existe"));

        if (!tracto.activo()) {
            throw new ReglaNegocioException("El tracto se encuentra inactivo");
        }

        if (!DISPONIBLE.equals(tracto.estado())) {
            throw new ReglaNegocioException("El tracto no se encuentra disponible");
        }

        OperacionConsultaPort.SemirremolqueOperacion semirremolque = consulta.buscarSemirremolque(p.getSemirremolqueId())
                .orElseThrow(() -> new ReglaNegocioException("El semirremolque indicado no existe"));

        if (!semirremolque.activo()) {
            throw new ReglaNegocioException("El semirremolque se encuentra inactivo");
        }

        if (!DISPONIBLE.equals(semirremolque.estado())) {
            throw new ReglaNegocioException("El semirremolque no se encuentra disponible");
        }

        validarCapacidad(pedido.toneladas(), tracto.capacidadToneladas(), "tracto");
        validarCapacidad(pedido.toneladas(), semirremolque.capacidadToneladas(), "semirremolque");
        validarCompatibilidad(pedido.tipoCarga(), semirremolque.tipo());
    }

    private void validarCapacidad(BigDecimal toneladas, BigDecimal capacidad, String recurso) {
        if (toneladas != null && capacidad != null && capacidad.compareTo(toneladas) < 0) {
            throw new ReglaNegocioException(
                    "La capacidad del " + recurso + " es menor al tonelaje solicitado"
            );
        }
    }

    private void validarCompatibilidad(String tipoCarga, String tipoSemirremolque) {
        String tipoEsperado = COMPATIBILIDAD_CARGA.get(tipoCarga);

        if (tipoEsperado != null && !tipoEsperado.equals(tipoSemirremolque)) {
            throw new ReglaNegocioException(
                    "El tipo de carga " + tipoCarga + " requiere un semirremolque " + tipoEsperado
            );
        }
    }
}

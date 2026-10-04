package com.yankardev.sitrac.somma.application.service;

import com.yankardev.sitrac.somma.application.exception.ReglaNegocioException;
import com.yankardev.sitrac.somma.application.exception.RegistroSommaNoEncontradoException;
import com.yankardev.sitrac.somma.domain.model.EstadoRegistroSomma;
import com.yankardev.sitrac.somma.domain.model.RegistroSomma;
import com.yankardev.sitrac.somma.domain.model.TipoRegistroSomma;
import com.yankardev.sitrac.somma.domain.port.in.RegistroSommaUseCase;
import com.yankardev.sitrac.somma.domain.port.out.ProgramacionConsultaPort;
import com.yankardev.sitrac.somma.domain.port.out.RegistroSommaRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RegistroSommaService implements RegistroSommaUseCase {

    private final RegistroSommaRepositoryPort repo;
    private final ProgramacionConsultaPort programacionConsulta;

    @Override
    public RegistroSomma crear(RegistroSomma registro) {
        validarRegistro(registro);

        return repo.guardar(RegistroSomma.builder()
                .programacionId(registro.getProgramacionId())
                .tipo(registro.getTipo())
                .conductorId(registro.getConductorId())
                .fecha(registro.getFecha())
                .titulo(registro.getTitulo())
                .descripcion(registro.getDescripcion())
                .lugar(registro.getLugar())
                .estado(EstadoRegistroSomma.REGISTRADO)
                .build());
    }

    @Override
    public List<RegistroSomma> listar() {
        return repo.listar();
    }

    @Override
    public RegistroSomma obtenerPorId(Long id) {
        return repo.buscarPorId(id)
                .orElseThrow(() -> new RegistroSommaNoEncontradoException(id));
    }

    @Override
    public RegistroSomma actualizar(Long id, RegistroSomma registro) {
        RegistroSomma actual = obtenerPorId(id);
        validarRegistro(registro);

        return repo.guardar(RegistroSomma.builder()
                .id(actual.getId())
                .programacionId(registro.getProgramacionId())
                .tipo(registro.getTipo())
                .conductorId(registro.getConductorId())
                .fecha(registro.getFecha())
                .titulo(registro.getTitulo())
                .descripcion(registro.getDescripcion())
                .lugar(registro.getLugar())
                .estado(registro.getEstado() == null ? actual.getEstado() : registro.getEstado())
                .build());
    }

    @Override
    public void eliminar(Long id) {
        obtenerPorId(id);
        repo.eliminarPorId(id);
    }

    private void validarRegistro(RegistroSomma registro) {
        if (registro.getTipo() != TipoRegistroSomma.CHARLA) {
            return;
        }

        if (registro.getProgramacionId() == null) {
            throw new ReglaNegocioException(
                    "La charla SOMMA debe estar asociada a una programación"
            );
        }

        if (registro.getConductorId() == null) {
            throw new ReglaNegocioException(
                    "La charla SOMMA debe indicar el conductor"
            );
        }

        ProgramacionConsultaPort.ProgramacionOperacion programacion =
                programacionConsulta.buscarProgramacion(registro.getProgramacionId())
                        .orElseThrow(() -> new ReglaNegocioException(
                                "La programación indicada no existe"
                        ));

        if (!"PROGRAMADA".equals(programacion.estado())) {
            throw new ReglaNegocioException(
                    "La programación debe estar en estado PROGRAMADA para registrar la charla SOMMA"
            );
        }

        if (!registro.getConductorId().equals(programacion.conductorId())) {
            throw new ReglaNegocioException(
                    "El conductor indicado no corresponde a la programación"
            );
        }
    }
}

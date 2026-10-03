package com.yankardev.sitrac.programacion.infrastructure.adapter.out.persistence;

import com.yankardev.sitrac.programacion.domain.model.EstadoProgramacion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProgramacionJpaRepository extends JpaRepository<ProgramacionJpaEntity, Long> {

    boolean existsByPedidoIdAndEstado(Long pedidoId, EstadoProgramacion estado);

    boolean existsByConductorIdAndEstado(Long conductorId, EstadoProgramacion estado);

    boolean existsByTractoIdAndEstado(Long tractoId, EstadoProgramacion estado);

    boolean existsBySemirremolqueIdAndEstado(Long semirremolqueId, EstadoProgramacion estado);

    boolean existsByPedidoIdAndEstadoAndIdNot(Long pedidoId, EstadoProgramacion estado, Long id);

    boolean existsByConductorIdAndEstadoAndIdNot(Long conductorId, EstadoProgramacion estado, Long id);

    boolean existsByTractoIdAndEstadoAndIdNot(Long tractoId, EstadoProgramacion estado, Long id);

    boolean existsBySemirremolqueIdAndEstadoAndIdNot(Long semirremolqueId, EstadoProgramacion estado, Long id);
}

package com.yankardev.sitrac.programacion.infrastructure.adapter.out.persistence;
import com.yankardev.sitrac.programacion.domain.model.EstadoProgramacion;import org.springframework.data.jpa.repository.JpaRepository;
public interface ProgramacionJpaRepository extends JpaRepository<ProgramacionJpaEntity,Long>{boolean existsByPedidoIdAndEstado(Long pedidoId,EstadoProgramacion estado);}
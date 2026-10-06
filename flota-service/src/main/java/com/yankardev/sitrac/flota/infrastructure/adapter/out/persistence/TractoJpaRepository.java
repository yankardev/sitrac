package com.yankardev.sitrac.flota.infrastructure.adapter.out.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
public interface TractoJpaRepository extends JpaRepository<TractoJpaEntity,Long>{boolean existsByPlaca(String placa);boolean existsByPlacaAndIdNot(String placa,Long id);}
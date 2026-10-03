package com.yankardev.sitrac.flota.infrastructure.adapter.out.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
public interface SemirremolqueJpaRepository extends JpaRepository<SemirremolqueJpaEntity,Long>{boolean existsByPlaca(String placa);boolean existsByPlacaAndIdNot(String placa,Long id);}
package com.nexttechstore.nexttech_backend.repository.orm;

import com.nexttechstore.nexttech_backend.model.entity.PuestoEntity;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PuestoJpaRepository extends JpaRepository<PuestoEntity, Integer> {

    long countByActivoTrue();

    List<PuestoEntity> findByDepartamento_Id(Integer departamentoId);

    // ====== NUEVO: para evitar LazyInitialization ======

    @Query("""
       select p from PuestoEntity p
       join fetch p.departamento d
       order by p.nombre asc
    """)
    List<PuestoEntity> findAllWithDepartamento();

    @Query("""
       select p from PuestoEntity p
       join fetch p.departamento d
       where d.id = :departamentoId
       order by p.nombre asc
    """)
    List<PuestoEntity> findAllWithDepartamentoByDepto(@Param("departamentoId") Integer departamentoId);
}

package com.nexttechstore.nexttech_backend.repository.orm;

import com.nexttechstore.nexttech_backend.model.entity.EmpleadoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmpleadoJpaRepository extends JpaRepository<EmpleadoEntity, Integer> {

    // estados: 'A','I','S' (mapeado como String en tu entidad)
    long countByEstado(String estado);

    long countByPuesto_Departamento_Id(Integer deptoId);

    // ===== NUEVO: para conteo por puesto en el dashboard =====
    long countByPuesto_Id(Integer puestoId);

    Page<EmpleadoEntity> findByPuesto_Departamento_Id(Integer deptoId, Pageable pageable);

    Page<EmpleadoEntity> findByPuesto_Id(Integer puestoId, Pageable pageable);

    Page<EmpleadoEntity> findByEstado(String estado, Pageable pageable);

    Page<EmpleadoEntity> findByNombresContainingIgnoreCaseOrApellidosContainingIgnoreCase(
            String n, String a, Pageable pageable
    );
}

package com.nexttechstore.nexttech_backend.repository.orm;

import com.nexttechstore.nexttech_backend.model.entity.DepartamentoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DepartamentoJpaRepository extends JpaRepository<DepartamentoEntity, Integer> {

    long countByActivoTrue();

    Optional<DepartamentoEntity> findByNombreIgnoreCase(String nombre);
}

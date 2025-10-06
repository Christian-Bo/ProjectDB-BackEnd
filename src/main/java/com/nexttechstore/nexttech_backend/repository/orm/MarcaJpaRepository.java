package com.nexttechstore.nexttech_backend.repository.orm;


import com.nexttechstore.nexttech_backend.model.entity.MarcaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MarcaJpaRepository extends JpaRepository<MarcaEntity, Integer> {
    boolean existsByNombreIgnoreCase(String nombre);
}

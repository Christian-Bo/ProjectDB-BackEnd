package com.nexttechstore.nexttech_backend.repository.orm;

import com.nexttechstore.nexttech_backend.model.entity.UsuarioEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UsuarioJpaRepository extends JpaRepository<UsuarioEntity, Integer> {

    Optional<UsuarioEntity> findByNombreUsuario(String nombreUsuario);

    // Para auth/me
    @Query("""
       select u from UsuarioEntity u
       join fetch u.rol r
       where u.nombreUsuario = :username
    """)
    Optional<UsuarioEntity> findByNombreUsuarioWithRol(@Param("username") String username);

    // Búsqueda por texto
    Page<UsuarioEntity> findByNombreUsuarioContainingIgnoreCase(String q, Pageable pageable);

    // Validaciones de unicidad que usa tu service
    boolean existsByNombreUsuarioIgnoreCase(String nombreUsuario);

    boolean existsByEmpleadoId(Integer empleadoId);

    boolean existsByEmpleadoIdAndIdNot(Integer empleadoId, Integer id);
}

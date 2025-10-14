package com.nexttechstore.nexttech_backend.repository.orm;

import com.nexttechstore.nexttech_backend.model.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UsuarioJpaRepository extends JpaRepository<UsuarioEntity, Integer> {

    Optional<UsuarioEntity> findByNombreUsuario(String nombreUsuario);

    // ===== NUEVO: útil para /api/auth/me =====
    @Query("""
       select u from UsuarioEntity u
       join fetch u.rol r
       where u.nombreUsuario = :username
    """)
    Optional<UsuarioEntity> findByNombreUsuarioWithRol(@Param("username") String username);
}

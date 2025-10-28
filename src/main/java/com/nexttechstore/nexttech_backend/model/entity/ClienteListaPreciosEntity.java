package com.nexttechstore.nexttech_backend.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "clientes_lista_precios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClienteListaPreciosEntity {

    @Id
    @Column(name = "cliente_id")
    private Integer clienteId;

    @Column(name = "lista_id", nullable = false)
    private Integer listaId;

    @Column(name = "fecha_asignacion", nullable = false, updatable = false)
    private LocalDateTime fechaAsignacion;

    @PrePersist
    protected void onCreate() {
        fechaAsignacion = LocalDateTime.now();
    }
}

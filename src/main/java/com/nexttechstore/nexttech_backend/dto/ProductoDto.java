package com.nexttechstore.nexttech_backend.dto.catalogos;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ProductoDto {
    private int id;
    private String codigo;
    private String nombre;
    private String descripcion;
    private double precioCompra;
    private double precioVenta;
    private int stockMinimo;
    private int stockMaximo;
    private int estado;
    private int marcaId;
    private String marcaNombre;
    private int categoriaId;
    private String categoriaNombre;
    private String codigoBarras;
    private String unidadMedida;
    private double peso;
    private int garantiaMeses;
    private LocalDateTime fechaCreacion;
    private int creadoPor;

}
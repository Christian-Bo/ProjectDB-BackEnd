package com.nexttechstore.nexttech_backend.dto.catalogos;

public class ProductoLiteDto {
    private int id;
    private String codigo;
    private String nombre;

    public ProductoLiteDto(int id, String codigo, String nombre) {
        this.id = id;
        this.codigo = codigo;
        this.nombre = nombre;
    }

    public int getId() { return id; }
    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
}

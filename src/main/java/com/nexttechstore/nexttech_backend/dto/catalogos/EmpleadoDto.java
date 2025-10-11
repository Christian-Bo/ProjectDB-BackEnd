package com.nexttechstore.nexttech_backend.dto.catalogos;

public record EmpleadoDto(
        Integer id,
        String codigo,
        String nombres,
        String apellidos
) {
    public String nombreCompleto() {
        return (nombres == null ? "" : nombres) + " " + (apellidos == null ? "" : apellidos);
    }
}

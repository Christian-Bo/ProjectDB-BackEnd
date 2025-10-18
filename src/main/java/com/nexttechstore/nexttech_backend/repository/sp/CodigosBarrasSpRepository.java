package com.nexttechstore.nexttech_backend.repository.sp;

import com.nexttechstore.nexttech_backend.dto.catalogos.CodigoBarrasDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class CodigosBarrasSpRepository {

    private final JdbcTemplate jdbc;

    public CodigosBarrasSpRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final RowMapper<CodigoBarrasDto> MAPPER = new RowMapper<>() {
        @Override
        public CodigoBarrasDto mapRow(ResultSet rs, int rowNum) throws SQLException {
            CodigoBarrasDto dto = new CodigoBarrasDto();
            dto.setId(rs.getInt("id"));
            dto.setProductoId(rs.getInt("producto_id"));
            dto.setCodigoBarras(rs.getString("codigo_barras"));
            dto.setTipoCodigo(rs.getString("tipo_codigo"));
            dto.setActivo(rs.getBoolean("activo"));
            try {
                dto.setFechaCreacion(rs.getTimestamp("fecha_creacion").toLocalDateTime());
            } catch (Exception ignore) {}
            return dto;
        }
    };

    public List<CodigoBarrasDto> listar() {
        return jdbc.query("EXEC sp_CODIGOS_BARRAS_Listar", MAPPER);
    }

    public CodigoBarrasDto buscarPorId(int id) {
        return jdbc.queryForObject("EXEC sp_CODIGOS_BARRAS_BuscarPorId ?", MAPPER, id);
    }

    public int crear(CodigoBarrasDto dto) {
        String sql = "EXEC sp_CODIGOS_BARRAS_Crear ?, ?, ?, ?";
        return jdbc.queryForObject(sql, Integer.class,
                dto.getProductoId(),
                dto.getCodigoBarras(),
                dto.getTipoCodigo(),
                dto.isActivo());
    }

    public int editar(int id, CodigoBarrasDto dto) {
        String sql = "EXEC sp_CODIGOS_BARRAS_Editar ?, ?, ?, ?, ?";
        return jdbc.queryForObject(sql, Integer.class,
                id,
                dto.getProductoId(),
                dto.getCodigoBarras(),
                dto.getTipoCodigo(),
                dto.isActivo());
    }

    public int eliminar(int id) {
        return jdbc.queryForObject("EXEC sp_CODIGOS_BARRAS_Eliminar ?", Integer.class, id);
    }
}

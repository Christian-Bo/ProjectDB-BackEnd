package com.nexttechstore.nexttech_backend.repository.sql;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class InventarioRepository {

    public static final class InventarioInfo {
        public final int productoId;
        public final int bodegaId;
        public final BigDecimal cantidadActual;
        public final BigDecimal ultimoCosto;

        public InventarioInfo(int productoId, int bodegaId, BigDecimal cantidadActual, BigDecimal ultimoCosto) {
            this.productoId = productoId;
            this.bodegaId = bodegaId;
            this.cantidadActual = cantidadActual;
            this.ultimoCosto = ultimoCosto;
        }
    }

    private final JdbcTemplate jdbc;
    public InventarioRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    public InventarioInfo getInventario(int productoId, int bodegaId) {
        String sql = """
            SELECT producto_id, bodega_id, cantidad_actual, ultimo_costo
            FROM dbo.inventario
            WHERE producto_id = ? AND bodega_id = ?
            """;
        return jdbc.query(sql, rs -> rs.next() ? map(rs) : null, productoId, bodegaId);
    }

    private InventarioInfo map(ResultSet rs) throws SQLException {
        return new InventarioInfo(
                rs.getInt("producto_id"),
                rs.getInt("bodega_id"),
                rs.getBigDecimal("cantidad_actual"),
                rs.getBigDecimal("ultimo_costo")
        );
    }

    /** Descuenta stock de forma atómica; retorna true si logró descontar (>= solicitado). */
    public boolean descontarStock(int productoId, int bodegaId, BigDecimal cantidad) {
        String upd = """
            UPDATE dbo.inventario
               SET cantidad_actual = cantidad_actual - ?
             WHERE producto_id = ? AND bodega_id = ? AND cantidad_actual >= ?
            """;
        int rows = jdbc.update(upd, cantidad, productoId, bodegaId, cantidad);
        return rows == 1;
    }

    /** Inserta un movimiento de tipo SALIDA por venta. */
    public void insertarMovimientoSalida(int productoId,
                                         int bodegaId,
                                         BigDecimal cantidad,
                                         BigDecimal cantAnterior,
                                         BigDecimal cantNueva,
                                         BigDecimal costoUnitario,
                                         int ventaId,
                                         String numeroVenta,
                                         int responsableEmpleadoId) {
        String ins = """
            INSERT INTO dbo.movimientos_inventario
              (producto_id, bodega_id, tipo_movimiento, cantidad, cantidad_anterior, cantidad_nueva,
               costo_unitario, referencia_tipo, referencia_id, motivo, empleado_responsable_id)
            VALUES (?,?,?,?,?,?,?,?,?,?,?)
            """;
        jdbc.update(ins,
                productoId,
                bodegaId,
                "S",
                cantidad,
                cantAnterior,
                cantNueva,
                costoUnitario,
                "V",
                ventaId,
                "Venta " + numeroVenta,
                responsableEmpleadoId
        );
    }
}

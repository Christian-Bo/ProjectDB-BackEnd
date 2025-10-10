package com.nexttechstore.nexttech_backend.repository.orm;

import com.nexttechstore.nexttech_backend.dto.VentaItemDto;
import com.nexttechstore.nexttech_backend.dto.VentaRequestDto;
import com.nexttechstore.nexttech_backend.exception.BadRequestException;
import com.nexttechstore.nexttech_backend.exception.ResourceNotFoundException;
import org.springframework.jdbc.core.namedparam.*;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;

@Repository
public class VentasJdbcRepository {

    private final NamedParameterJdbcTemplate jdbc;

    public VentasJdbcRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public int registrarVenta(VentaRequestDto req) {
        // Validaciones base
        if (req.getItems() == null || req.getItems().isEmpty()) {
            throw new BadRequestException("La venta debe tener al menos un ítem.");
        }

        // Validar existencia de cliente
        Integer cliCount = jdbc.queryForObject(
                "SELECT COUNT(1) FROM dbo.clientes WHERE id = :id",
                new MapSqlParameterSource("id", req.getClienteId()),
                Integer.class
        );
        if (cliCount == null || cliCount == 0) {
            throw new ResourceNotFoundException("Cliente no existe: " + req.getClienteId());
        }

        // Tomamos la primera bodega como bodega_origen_id (puedes reforzar que todas sean iguales)
        Integer bodegaOrigen = req.getItems().get(0).getBodegaId();
        // Si quieres forzar una sola bodega:
        boolean distintasBodegas = req.getItems().stream().map(VentaItemDto::getBodegaId).distinct().count() > 1;
        if (distintasBodegas) {
            throw new BadRequestException("Todas las líneas deben usar la misma bodega de origen.");
        }

        // Validar stock requerido por (producto, bodega)
        // Para cada línea, revisamos si hay inventario suficiente
        for (VentaItemDto it : req.getItems()) {
            MapSqlParameterSource p = new MapSqlParameterSource()
                    .addValue("prod", it.getProductoId())
                    .addValue("bod", it.getBodegaId());
            Integer actual = jdbc.queryForObject(
                    "SELECT cantidad_actual FROM dbo.inventario WHERE producto_id=:prod AND bodega_id=:bod",
                    p,
                    Integer.class
            );
            int cantReq = it.getCantidad().setScale(0, BigDecimal.ROUND_HALF_UP).intValueExact();
            if (actual == null || actual < cantReq) {
                throw new BadRequestException("Stock insuficiente para producto " + it.getProductoId()
                        + " en bodega " + it.getBodegaId() + ". Disponible=" + (actual == null ? 0 : actual)
                        + ", requerido=" + cantReq);
            }
        }

        // === Transacción manual: usamos una sola conexión vía batch de updates en @Transactional (desde el Service)
        // Generar consecutivo simple: V-<n>. En ambientes concurrentes reales, se debe serializar o usar otra estrategia.
        Integer maxNum = jdbc.queryForObject(
                "SELECT MAX(CAST(SUBSTRING(numero_venta, 3, 50) AS INT)) FROM dbo.ventas WHERE ISNUMERIC(SUBSTRING(numero_venta,3,50))=1",
                new MapSqlParameterSource(),
                Integer.class
        );
        int consec = (maxNum == null ? 0 : maxNum) + 1;
        String numeroVenta = "V-" + consec;

        // Calcular totales (sin IVA general por ahora)
        BigDecimal subtotal = BigDecimal.ZERO;
        for (VentaItemDto it : req.getItems()) {
            BigDecimal lin = it.getCantidad().multiply(it.getPrecioUnitario());
            if (it.getDescuento() != null) lin = lin.subtract(it.getDescuento());
            subtotal = subtotal.add(lin);
        }
        BigDecimal descGen = BigDecimal.ZERO;
        BigDecimal iva = BigDecimal.ZERO;
        BigDecimal total = subtotal.subtract(descGen).add(iva);

        Date hoy = Date.valueOf(java.time.LocalDate.now(ZoneOffset.UTC));

        // Insert ventas (encabezado)
        String sqlVenta = """
            INSERT INTO dbo.ventas
              (numero_venta, fecha_venta, subtotal, descuento_general, iva, total,
               estado, tipo_pago, observaciones, cliente_id, vendedor_id, cajero_id, bodega_origen_id)
            VALUES
              (:num, :fec, :sub, :desc, :iva, :tot,
               'P','C', NULL, :cli, :vend, :caj, :bod)
            """;
        KeyHolder kh = new GeneratedKeyHolder();
        MapSqlParameterSource pv = new MapSqlParameterSource()
                .addValue("num", numeroVenta)
                .addValue("fec", hoy)
                .addValue("sub", subtotal)
                .addValue("desc", descGen)
                .addValue("iva", iva)
                .addValue("tot", total)
                .addValue("cli", req.getClienteId())
                .addValue("vend", req.getUsuarioId()) // Puedes mapear a otro empleado si quieres
                .addValue("caj", req.getUsuarioId())
                .addValue("bod", bodegaOrigen);

        jdbc.update(sqlVenta, pv, kh, new String[]{"id"});
        Number ventaIdNum = kh.getKey();
        if (ventaIdNum == null) {
            throw new IllegalStateException("No se pudo obtener el ID de la venta creada.");
        }
        int ventaId = ventaIdNum.intValue();

        // Insert detalle_ventas
        String sqlDetalle = """
            INSERT INTO dbo.detalle_ventas
               (venta_id, producto_id, cantidad, precio_unitario, descuento_linea, subtotal, lote, fecha_vencimiento)
            VALUES
               (:ven, :prod, :cant, :precio, :desc, :sub, NULL, NULL)
            """;
        List<MapSqlParameterSource> detalleParams = new ArrayList<>();
        for (VentaItemDto it : req.getItems()) {
            BigDecimal lin = it.getCantidad().multiply(it.getPrecioUnitario());
            BigDecimal desc = it.getDescuento() != null ? it.getDescuento() : BigDecimal.ZERO;
            BigDecimal linSub = lin.subtract(desc);

            MapSqlParameterSource pd = new MapSqlParameterSource()
                    .addValue("ven", ventaId)
                    .addValue("prod", it.getProductoId())
                    // cantidad en tabla es INT: redondeamos seguro
                    .addValue("cant", it.getCantidad().setScale(0, BigDecimal.ROUND_HALF_UP).intValueExact())
                    .addValue("precio", it.getPrecioUnitario())
                    .addValue("desc", desc)
                    .addValue("sub", linSub);
            detalleParams.add(pd);
        }
        jdbc.batchUpdate(sqlDetalle, detalleParams.toArray(new SqlParameterSource[0]));

        // Actualizar inventario y registrar movimiento S (salida) de forma atómica por producto/bodega
        // Consolidamos cantidades por producto/bodega:
        Map<String, Integer> porProdBod = new LinkedHashMap<>();
        for (VentaItemDto it : req.getItems()) {
            String key = it.getProductoId() + "#" + it.getBodegaId();
            int inc = it.getCantidad().setScale(0, BigDecimal.ROUND_HALF_UP).intValueExact();
            porProdBod.merge(key, inc, Integer::sum);
        }

        // Para costo_unitario en movimiento no tenemos costo en inventario; lo dejamos NULL,
        // o si quieres, podrías leer ultimo_costo de inventario/productos.
        String sqlSelectInv = "SELECT cantidad_actual FROM dbo.inventario WHERE producto_id=:p AND bodega_id=:b";
        String sqlUpdateInv = """
            UPDATE dbo.inventario
               SET cantidad_actual = cantidad_actual - :resta,
                   fecha_ultima_salida = :hoy,
                   fecha_actualizacion = SYSUTCDATETIME()
             WHERE producto_id=:p AND bodega_id=:b
               AND cantidad_actual >= :resta
            """;
        String sqlInsertMov = """
            INSERT INTO dbo.movimientos_inventario
              (producto_id, bodega_id, tipo_movimiento, cantidad,
               cantidad_anterior, cantidad_nueva, costo_unitario,
               referencia_tipo, referencia_id, motivo, empleado_responsable_id)
            VALUES
              (:p, :b, 'S', :cant,
               :antes, :despues, NULL,
               'V', :venta, :motivo, :emp)
            """;

        for (Map.Entry<String, Integer> e : porProdBod.entrySet()) {
            String[] parts = e.getKey().split("#");
            int p = Integer.parseInt(parts[0]);
            int b = Integer.parseInt(parts[1]);
            int resta = e.getValue();

            // leer cantidad_antes
            Integer antes = jdbc.queryForObject(
                    sqlSelectInv,
                    new MapSqlParameterSource().addValue("p", p).addValue("b", b),
                    Integer.class
            );
            if (antes == null || antes < resta) {
                throw new BadRequestException("Stock insuficiente durante la actualización (producto " + p + ", bodega " + b + ")");
            }

            // update atómico (evita negativos)
            int rows = jdbc.update(
                    sqlUpdateInv,
                    new MapSqlParameterSource()
                            .addValue("resta", resta)
                            .addValue("hoy", hoy)
                            .addValue("p", p)
                            .addValue("b", b)
            );
            if (rows != 1) {
                throw new IllegalStateException("No se pudo actualizar inventario (posible carrera). Producto " + p + ", bodega " + b);
            }

            int despues = antes - resta;

            // registrar movimiento
            jdbc.update(
                    sqlInsertMov,
                    new MapSqlParameterSource()
                            .addValue("p", p)
                            .addValue("b", b)
                            .addValue("cant", resta)
                            .addValue("antes", antes)
                            .addValue("despues", despues)
                            .addValue("venta", ventaId)
                            .addValue("motivo", "Salida por venta " + numeroVenta)
                            .addValue("emp", req.getUsuarioId())
            );
        }

        return ventaId;
    }
}

package com.nexttechstore.nexttech_backend.repository.sp;

import com.nexttechstore.nexttech_backend.dto.productos.ProductoRequestDTO;
import com.nexttechstore.nexttech_backend.dto.productos.ProductoResponseDTO;
import com.nexttechstore.nexttech_backend.dto.productos.ProductoSearchDTO;
import com.nexttechstore.nexttech_backend.dto.common.ApiResponse;
import com.nexttechstore.nexttech_backend.dto.common.PageRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ProductosSpRepository {

    private final JdbcTemplate jdbcTemplate;

    public ApiResponse<ProductoResponseDTO> create(ProductoRequestDTO request) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("sp_ProductoCreate")
                    .declareParameters(
                            new SqlParameter("codigo", Types.NVARCHAR),
                            new SqlParameter("nombre", Types.NVARCHAR),
                            new SqlParameter("descripcion", Types.NVARCHAR),
                            new SqlParameter("precio_compra", Types.DECIMAL),
                            new SqlParameter("precio_venta", Types.DECIMAL),
                            new SqlParameter("stock_minimo", Types.INTEGER),
                            new SqlParameter("stock_maximo", Types.INTEGER),
                            new SqlParameter("estado", Types.CHAR),
                            new SqlParameter("marca_id", Types.INTEGER),
                            new SqlParameter("categoria_id", Types.INTEGER),
                            new SqlParameter("codigo_barras", Types.NVARCHAR),
                            new SqlParameter("unidad_medida", Types.NVARCHAR),
                            new SqlParameter("peso", Types.DECIMAL),
                            new SqlParameter("garantia_meses", Types.INTEGER),
                            new SqlParameter("creado_por", Types.INTEGER),
                            new SqlOutParameter("productoId", Types.INTEGER),
                            new SqlOutParameter("productoNombre", Types.NVARCHAR)
                    );

            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("codigo", request.getCodigo())
                    .addValue("nombre", request.getNombre())
                    .addValue("descripcion", request.getDescripcion())
                    .addValue("precio_compra", request.getPrecioCompra())
                    .addValue("precio_venta", request.getPrecioVenta())
                    .addValue("stock_minimo", request.getStockMinimo())
                    .addValue("stock_maximo", request.getStockMaximo())
                    .addValue("estado", request.getEstado())
                    .addValue("marca_id", request.getMarcaId())
                    .addValue("categoria_id", request.getCategoriaId())
                    .addValue("codigo_barras", request.getCodigoBarras())
                    .addValue("unidad_medida", request.getUnidadMedida())
                    .addValue("peso", request.getPeso())
                    .addValue("garantia_meses", request.getGarantiaMeses())
                    .addValue("creado_por", request.getCreadoPor());

            Map<String, Object> result = jdbcCall.execute(params);

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> resultList = (List<Map<String, Object>>) result.get("#result-set-1");

            if (resultList != null && !resultList.isEmpty()) {
                Map<String, Object> row = resultList.get(0);

                ProductoResponseDTO producto = ProductoResponseDTO.builder()
                        .id((Integer) row.get("id"))
                        .codigo((String) row.get("codigo"))
                        .nombre((String) row.get("nombre"))
                        .precioVenta((java.math.BigDecimal) row.get("precio_venta_calculado"))
                        .build();

                return ApiResponse.success(
                        (String) row.get("mensaje"),
                        producto
                );
            }

            return ApiResponse.error("No se pudo crear el producto");

        } catch (Exception e) {
            log.error("Error al crear producto: ", e);
            return ApiResponse.error("Error al crear producto: " + e.getMessage());
        }
    }

    public ApiResponse<Void> update(Integer id, ProductoRequestDTO request) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("sp_ProductoUpdate")
                    .declareParameters(
                            new SqlParameter("id", Types.INTEGER),
                            new SqlParameter("nombre", Types.NVARCHAR),
                            new SqlParameter("descripcion", Types.NVARCHAR),
                            new SqlParameter("precio_compra", Types.DECIMAL),
                            new SqlParameter("precio_venta", Types.DECIMAL),
                            new SqlParameter("stock_minimo", Types.INTEGER),
                            new SqlParameter("stock_maximo", Types.INTEGER),
                            new SqlParameter("estado", Types.CHAR),
                            new SqlParameter("marca_id", Types.INTEGER),
                            new SqlParameter("categoria_id", Types.INTEGER),
                            new SqlParameter("codigo_barras", Types.NVARCHAR),
                            new SqlParameter("unidad_medida", Types.NVARCHAR),
                            new SqlParameter("peso", Types.DECIMAL),
                            new SqlParameter("garantia_meses", Types.INTEGER),
                            new SqlParameter("recalcular_precio", Types.BIT)
                    );

            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("id", id)
                    .addValue("nombre", request.getNombre())
                    .addValue("descripcion", request.getDescripcion())
                    .addValue("precio_compra", request.getPrecioCompra())
                    .addValue("precio_venta", request.getPrecioVenta())
                    .addValue("stock_minimo", request.getStockMinimo())
                    .addValue("stock_maximo", request.getStockMaximo())
                    .addValue("estado", request.getEstado())
                    .addValue("marca_id", request.getMarcaId())
                    .addValue("categoria_id", request.getCategoriaId())
                    .addValue("codigo_barras", request.getCodigoBarras())
                    .addValue("unidad_medida", request.getUnidadMedida())
                    .addValue("peso", request.getPeso())
                    .addValue("garantia_meses", request.getGarantiaMeses())
                    .addValue("recalcular_precio", request.getRecalcularPrecio());

            Map<String, Object> result = jdbcCall.execute(params);

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> resultList = (List<Map<String, Object>>) result.get("#result-set-1");

            if (resultList != null && !resultList.isEmpty()) {
                Map<String, Object> row = resultList.get(0);
                return ApiResponse.success((String) row.get("mensaje"), null);
            }

            return ApiResponse.error("No se pudo actualizar el producto");

        } catch (Exception e) {
            log.error("Error al actualizar producto: ", e);
            return ApiResponse.error("Error al actualizar producto: " + e.getMessage());
        }
    }

    public ApiResponse<Void> delete(Integer id) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("sp_ProductoDelete")
                    .declareParameters(
                            new SqlParameter("id", Types.INTEGER)
                    );

            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("id", id);

            Map<String, Object> result = jdbcCall.execute(params);

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> resultList = (List<Map<String, Object>>) result.get("#result-set-1");

            if (resultList != null && !resultList.isEmpty()) {
                Map<String, Object> row = resultList.get(0);
                return ApiResponse.success((String) row.get("mensaje"), null);
            }

            return ApiResponse.error("No se pudo eliminar el producto");

        } catch (Exception e) {
            log.error("Error al eliminar producto: ", e);
            return ApiResponse.error("Error al eliminar producto: " + e.getMessage());
        }
    }

    public ApiResponse<Void> activate(Integer id) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("sp_ProductoActivar")
                    .declareParameters(
                            new SqlParameter("id", Types.INTEGER)
                    );

            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("id", id);

            Map<String, Object> result = jdbcCall.execute(params);

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> resultList = (List<Map<String, Object>>) result.get("#result-set-1");

            if (resultList != null && !resultList.isEmpty()) {
                Map<String, Object> row = resultList.get(0);
                return ApiResponse.success((String) row.get("mensaje"), null);
            }

            return ApiResponse.error("No se pudo activar el producto");

        } catch (Exception e) {
            log.error("Error al activar producto: ", e);
            return ApiResponse.error("Error al activar producto: " + e.getMessage());
        }
    }

    public ApiResponse<Void> descontinuar(Integer id, String motivo) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("sp_ProductoDescontinuar")
                    .declareParameters(
                            new SqlParameter("id", Types.INTEGER),
                            new SqlParameter("motivo", Types.NVARCHAR)
                    );

            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("id", id)
                    .addValue("motivo", motivo);

            Map<String, Object> result = jdbcCall.execute(params);

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> resultList = (List<Map<String, Object>>) result.get("#result-set-1");

            if (resultList != null && !resultList.isEmpty()) {
                Map<String, Object> row = resultList.get(0);
                return ApiResponse.success((String) row.get("mensaje"), null);
            }

            return ApiResponse.error("No se pudo descontinuar el producto");

        } catch (Exception e) {
            log.error("Error al descontinuar producto: ", e);
            return ApiResponse.error("Error al descontinuar producto: " + e.getMessage());
        }
    }

    public Optional<ProductoResponseDTO> getById(Integer id) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("sp_ProductoGetById")
                    .declareParameters(
                            new SqlParameter("id", Types.INTEGER)
                    )
                    .returningResultSet("#result-set-1", (rs, rowNum) -> mapRowToProductoResponse(rs));

            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("id", id);

            Map<String, Object> result = jdbcCall.execute(params);

            @SuppressWarnings("unchecked")
            List<ProductoResponseDTO> productos = (List<ProductoResponseDTO>) result.get("#result-set-1");

            return productos != null && !productos.isEmpty()
                    ? Optional.of(productos.get(0))
                    : Optional.empty();

        } catch (Exception e) {
            log.error("Error al obtener producto por ID: ", e);
            return Optional.empty();
        }
    }

    public ApiResponse<List<ProductoResponseDTO>> getAll(Boolean soloActivos, Integer marcaId, Integer categoriaId, PageRequest pageRequest) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("sp_ProductoGetAll")
                    .declareParameters(
                            new SqlParameter("soloActivos", Types.BIT),
                            new SqlParameter("marca_id", Types.INTEGER),
                            new SqlParameter("categoria_id", Types.INTEGER)
                    )
                    .returningResultSet("#result-set-1", (rs, rowNum) -> mapRowToProductoResponse(rs));

            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("soloActivos", soloActivos)
                    .addValue("marca_id", marcaId)
                    .addValue("categoria_id", categoriaId);

            Map<String, Object> result = jdbcCall.execute(params);

            @SuppressWarnings("unchecked")
            List<ProductoResponseDTO> productos = (List<ProductoResponseDTO>) result.get("#result-set-1");

            if (productos != null) {
                return ApiResponse.success(
                        "Productos obtenidos exitosamente",
                        productos,
                        productos.size()
                );
            }

            return ApiResponse.error("No se pudieron obtener los productos");

        } catch (Exception e) {
            log.error("Error al obtener productos: ", e);
            return ApiResponse.error("Error al obtener productos: " + e.getMessage());
        }
    }

    public ApiResponse<List<ProductoSearchDTO>> search(String criterio, Boolean soloActivos) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("sp_ProductoSearch")
                    .declareParameters(
                            new SqlParameter("criterio", Types.NVARCHAR),
                            new SqlParameter("soloActivos", Types.BIT)
                    )
                    .returningResultSet("#result-set-1", (rs, rowNum) -> mapRowToProductoSearchDTO(rs));

            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("criterio", criterio)
                    .addValue("soloActivos", soloActivos);

            Map<String, Object> result = jdbcCall.execute(params);

            @SuppressWarnings("unchecked")
            List<ProductoSearchDTO> productos = (List<ProductoSearchDTO>) result.get("#result-set-1");

            if (productos != null) {
                return ApiResponse.success(
                        "Búsqueda completada",
                        productos,
                        productos.size()
                );
            }

            return ApiResponse.error("No se encontraron productos");

        } catch (Exception e) {
            log.error("Error al buscar productos: ", e);
            return ApiResponse.error("Error al buscar productos: " + e.getMessage());
        }
    }

    public Optional<ProductoSearchDTO> buscarPorCodigoBarras(String codigoBarras) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("sp_ProductoBuscarPorCodigoBarras")
                    .declareParameters(
                            new SqlParameter("codigo_barras", Types.NVARCHAR)
                    )
                    .returningResultSet("#result-set-1", (rs, rowNum) -> mapRowToProductoSearchDTO(rs));

            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("codigo_barras", codigoBarras);

            Map<String, Object> result = jdbcCall.execute(params);

            @SuppressWarnings("unchecked")
            List<ProductoSearchDTO> productos = (List<ProductoSearchDTO>) result.get("#result-set-1");

            return productos != null && !productos.isEmpty()
                    ? Optional.of(productos.get(0))
                    : Optional.empty();

        } catch (Exception e) {
            log.error("Error al buscar producto por código de barras: ", e);
            return Optional.empty();
        }
    }

    private ProductoResponseDTO mapRowToProductoResponse(ResultSet rs) throws SQLException {
        return ProductoResponseDTO.builder()
                .id(rs.getInt("id"))
                .codigo(rs.getString("codigo"))
                .nombre(rs.getString("nombre"))
                .descripcion(rs.getString("descripcion"))
                .precioCompra(rs.getBigDecimal("precio_compra"))
                .precioVenta(rs.getBigDecimal("precio_venta"))
                .stockMinimo(rs.getInt("stock_minimo"))
                .stockMaximo(rs.getInt("stock_maximo"))
                .estado(rs.getString("estado"))
                .estadoNombre(rs.getString("estado_nombre"))
                .marcaId((Integer) rs.getObject("marca_id"))
                .marcaNombre(rs.getString("marca_nombre"))
                .categoriaId((Integer) rs.getObject("categoria_id"))
                .categoriaNombre(rs.getString("categoria_nombre"))
                .codigoBarras(rs.getString("codigo_barras"))
                .unidadMedida(rs.getString("unidad_medida"))
                .peso(rs.getBigDecimal("peso"))
                .garantiaMeses(rs.getInt("garantia_meses"))
                .fechaCreacion(rs.getTimestamp("fecha_creacion").toLocalDateTime())
                .creadoPor(rs.getInt("creado_por"))
                .creadoPorNombre(rs.getString("creado_por_nombre"))
                .stockTotal(rs.getInt("stock_total"))
                .margenActualPct(rs.getBigDecimal("margen_actual_pct"))
                .build();
    }

    private ProductoSearchDTO mapRowToProductoSearchDTO(ResultSet rs) throws SQLException {
        return ProductoSearchDTO.builder()
                .id(rs.getInt("id"))
                .codigo(rs.getString("codigo"))
                .nombre(rs.getString("nombre"))
                .precioVenta(rs.getBigDecimal("precio_venta"))
                .estado(rs.getString("estado"))
                .marcaNombre(rs.getString("marca_nombre"))
                .categoriaNombre(rs.getString("categoria_nombre"))
                .unidadMedida(rs.getString("unidad_medida"))
                .stockTotal(rs.getInt("stock_total"))
                .build();
    }
}
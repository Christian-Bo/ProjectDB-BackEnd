package com.nexttechstore.nexttech_backend.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexttechstore.nexttech_backend.dto.DetalleTransferenciaDto;
import com.nexttechstore.nexttech_backend.dto.TransferenciaDto;
import com.nexttechstore.nexttech_backend.repository.sp.TransferenciaSpRepository;
import com.nexttechstore.nexttech_backend.service.api.TransferenciaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Impl robusta:
 * - Si no hay filtros (o vienen vacíos) intenta listar TODO.
 * - Si el SP con null no devuelve nada, hace "sondeo" por cada estado (P,E,R,C) y une resultados.
 * - Mapeo tolerante a nombres alternos de columnas (cantidad_enviada/cantidad_aprobada, etc.).
 * - Fechas tolerantes a "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ss", etc.
 * - Nunca retorna null en listados (si hay error o vacío -> lista vacía).
 */
@Service
public class TransferenciaServiceImpl implements TransferenciaService {

    @Autowired
    private TransferenciaSpRepository transferenciaSpRepository;

    /* =========================
     * LISTAR
     * ========================= */
    @Override
    public List<TransferenciaDto> listarTransferencias(Integer bodegaOrigenId, Integer bodegaDestinoId, String estado) {
        // Normaliza estado: "", "  " -> null ; a MAYÚSCULAS si viene algo
        String estadoNorm = normalizeEstado(estado);

        // Indicador: ¿realmente NO hay filtros?
        boolean sinFiltros = (bodegaOrigenId == null && bodegaDestinoId == null && estadoNorm == null);

        // 1) Intento directo con lo que venga (incluye sin filtros -> nulls)
        List<TransferenciaDto> direct = safeListar(bodegaOrigenId, bodegaDestinoId, estadoNorm);

        if (!direct.isEmpty() || !sinFiltros) {
            // Si ya trajo algo, o si el usuario sí mandó filtros, devolvemos de una
            return ordenarDescPorFecha(direct);
        }

        // 2) Plan B (sólo si no hay filtros y el SP con null no devuelve nada):
        //    Sondeo por estado y UNION de resultados.
        List<TransferenciaDto> merged = new ArrayList<>();
        for (String est : Arrays.asList("P", "E", "R", "C")) {
            merged.addAll(safeListar(null, null, est));
        }

        // De-duplicamos por ID, preservando orden de inserción (preferimos las primeras ocurrencias)
        Map<Integer, TransferenciaDto> unique = new LinkedHashMap<>();
        for (TransferenciaDto t : merged) {
            if (t.getId() != null && !unique.containsKey(t.getId())) {
                unique.put(t.getId(), t);
            }
        }

        return ordenarDescPorFecha(new ArrayList<>(unique.values()));
    }

    /* =========================
     * OBTENER POR ID
     * ========================= */
    @Override
    public TransferenciaDto obtenerTransferenciaPorId(Integer id) {
        Map<String, Object> row = null;
        try {
            row = transferenciaSpRepository.obtenerTransferenciaPorId(id);
        } catch (Exception ex) {
            // Evitamos propagar exception dura: devolvemos null para que Controller responda 404/500 según su flujo
            return null;
        }
        if (row == null) return null;

        TransferenciaDto dto = mapearTransferencia(row);

        // Detalle
        List<Map<String, Object>> detalleResult;
        try {
            detalleResult = transferenciaSpRepository.obtenerDetalleTransferencia(id);
        } catch (Exception ex) {
            detalleResult = Collections.emptyList();
        }
        List<DetalleTransferenciaDto> detalles = new ArrayList<>();

        for (Map<String, Object> detRow : detalleResult) {
            DetalleTransferenciaDto detDto = new DetalleTransferenciaDto();
            detDto.setId(nzInt(detRow.get("id")));
            detDto.setTransferenciaId(nzInt(detRow.get("transferencia_id")));
            detDto.setProductoId(nzInt(detRow.get("producto_id")));
            detDto.setProductoCodigo(nzStr(detRow.get("producto_codigo")));
            detDto.setProductoNombre(nzStr(detRow.get("producto_nombre")));

            // soporta cantidad_solicitada / cantidad_aprobada / cantidad_enviada / cantidad_recibida
            Integer cantSol = nzInt(detRow.get("cantidad_solicitada"));
            Integer cantEnv = firstInt(detRow.get("cantidad_enviada"), detRow.get("cantidad_aprobada"));
            Integer cantRec = nzInt(detRow.get("cantidad_recibida"));

            detDto.setCantidadSolicitada(cantSol);
            detDto.setCantidadAprobada(cantEnv);     // en tu DTO se llama "Aprobada"
            detDto.setCantidadRecibida(cantRec);

            detDto.setObservaciones(nzStr(detRow.get("observaciones")));
            detalles.add(detDto);
        }
        dto.setDetalles(detalles);

        return dto;
    }

    /* =========================
     * CREAR
     * ========================= */
    @Override
    public TransferenciaDto crearTransferencia(TransferenciaDto transferenciaDto) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            List<Map<String, Object>> detallesJson = new ArrayList<>();

            if (transferenciaDto.getDetalles() != null) {
                for (DetalleTransferenciaDto det : transferenciaDto.getDetalles()) {
                    // El SP espera "producto_id" + "cantidad" (solicitada)
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("producto_id", det.getProductoId());
                    item.put("cantidad", det.getCantidadSolicitada());
                    detallesJson.add(item);
                }
            }

            String detallesJsonString = mapper.writeValueAsString(detallesJson);

// ========== DEBUG AGREGADO ==========
            System.out.println("========================================");
            System.out.println("DEBUG EN SERVICE - ANTES DE REPOSITORY:");
            System.out.println("========================================");
            System.out.println("numeroTransferencia: " + transferenciaDto.getNumeroTransferencia());
            System.out.println("fechaTransferencia: " + transferenciaDto.getFechaTransferencia());
            System.out.println("bodegaOrigenId: " + transferenciaDto.getBodegaOrigenId());
            System.out.println("bodegaDestinoId: " + transferenciaDto.getBodegaDestinoId());
            System.out.println("solicitanteId: " + transferenciaDto.getSolicitanteId());
            System.out.println("observaciones: " + transferenciaDto.getObservaciones());
            System.out.println("detallesJson: " + detallesJsonString);
            System.out.println("========================================");
// ========== FIN DEBUG ==========

            Map<String, Object> result = transferenciaSpRepository.crearTransferencia(
                    transferenciaDto.getNumeroTransferencia(),
                    (transferenciaDto.getFechaTransferencia() != null
                            ? transferenciaDto.getFechaTransferencia().toString()
                            : LocalDate.now().toString()),
                    transferenciaDto.getBodegaOrigenId(),
                    transferenciaDto.getBodegaDestinoId(),
                    transferenciaDto.getSolicitanteId(),
                    transferenciaDto.getObservaciones(),
                    detallesJsonString
            );

            if (result != null && result.containsKey("id")) {
                Integer newId = toInt(result.get("id"));
                return obtenerTransferenciaPorId(newId);
            }

            return null;
        } catch (Exception e) {
            System.err.println("❌ ERROR EN crearTransferencia:");
            System.err.println("Mensaje: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /* =========================
     * ESTADOS
     * ========================= */
    @Override
    public void aprobarTransferencia(Integer id, Integer aprobadorId) {
        transferenciaSpRepository.aprobarTransferencia(id, aprobadorId);
    }

    @Override
    public void recibirTransferencia(Integer id, Integer receptorId) {
        transferenciaSpRepository.recibirTransferencia(id, receptorId);
    }

    @Override
    public void cancelarTransferencia(Integer id) {
        transferenciaSpRepository.cancelarTransferencia(id);
    }

    /* =========================
     * HELPERS PRIVADOS
     * ========================= */

    private List<TransferenciaDto> safeListar(Integer bodegaOrigenId, Integer bodegaDestinoId, String estadoNorm) {
        try {
            List<Map<String, Object>> result = transferenciaSpRepository
                    .listarTransferencias(bodegaOrigenId, bodegaDestinoId, estadoNorm);
            List<TransferenciaDto> out = new ArrayList<>();
            if (result != null) {
                for (Map<String, Object> row : result) out.add(mapearTransferencia(row));
            }
            return out;
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    private String normalizeEstado(String estado) {
        if (estado == null) return null;
        String s = estado.trim();
        if (s.isEmpty()) return null;
        s = s.toUpperCase(Locale.ROOT);
        // solo aceptamos P,E,R,C; si llega algo distinto lo ignoramos (null)
        return (s.equals("P") || s.equals("E") || s.equals("R") || s.equals("C")) ? s : null;
    }

    private List<TransferenciaDto> ordenarDescPorFecha(List<TransferenciaDto> list) {
        list.sort((a, b) -> {
            LocalDate fa = a.getFechaTransferencia();
            LocalDate fb = b.getFechaTransferencia();
            if (fa == null && fb == null) return 0;
            if (fa == null) return 1;
            if (fb == null) return -1;
            int cmp = fb.compareTo(fa); // desc
            if (cmp != 0) return cmp;
            return compareNullable(b.getNumeroTransferencia(), a.getNumeroTransferencia()); // desc lexicográfico
        });
        return list;
    }

    private int compareNullable(String x, String y) {
        if (x == null && y == null) return 0;
        if (x == null) return -1;
        if (y == null) return 1;
        return x.compareToIgnoreCase(y);
    }

    private TransferenciaDto mapearTransferencia(Map<String, Object> row) {
        TransferenciaDto dto = new TransferenciaDto();

        dto.setId(nzInt(row.get("id")));
        dto.setNumeroTransferencia(nzStr(row.get("numero_transferencia")));

        dto.setFechaTransferencia(toLocalDate(row.get("fecha_transferencia")));

        dto.setBodegaOrigenId(nzInt(row.get("bodega_origen_id")));
        dto.setBodegaOrigenNombre(nzStr(row.get("bodega_origen_nombre")));
        dto.setBodegaDestinoId(nzInt(row.get("bodega_destino_id")));
        dto.setBodegaDestinoNombre(nzStr(row.get("bodega_destino_nombre")));

        String estado = nzStr(row.get("estado"));
        dto.setEstado(estado);
        // descripción si viene; si no, derive
        String desc = nzStr(row.get("estado_descripcion"));
        if (desc.isBlank()) {
            desc = switch (estado != null ? estado.toUpperCase(Locale.ROOT) : "") {
                case "P" -> "Pendiente";
                case "E" -> "Enviada";
                case "R" -> "Recibida";
                case "C" -> "Cancelada";
                default -> "";
            };
        }
        dto.setEstadoDescripcion(desc);

        dto.setSolicitanteId(nzInt(row.get("solicitante_id")));
        dto.setAprobadorId(nzInt(row.get("aprobador_id")));
        dto.setReceptorId(nzInt(row.get("receptor_id")));
        dto.setObservaciones(nzStr(row.get("observaciones")));

        // soporta distintos nombres y formatos de fecha/hora
        dto.setFechaAprobacion(toLocalDateTime(first(row.get("fecha_aprobacion"), row.get("fecha_envio"))));
        dto.setFechaRecepcion(toLocalDateTime(first(row.get("fecha_recepcion"), row.get("fecha_recibido"))));
        dto.setFechaCreacion(toLocalDateTime(first(row.get("fecha_creacion"), row.get("creado_en"))));

        return dto;
    }

    /* ---------- util parser ---------- */

    private Integer firstInt(Object... vals) {
        for (Object v : vals) {
            Integer i = toIntOrNull(v);
            if (i != null) return i;
        }
        return 0;
    }

    private Object first(Object... vals) {
        for (Object v : vals) if (v != null) return v;
        return null;
    }

    private Integer nzInt(Object o) {
        Integer i = toIntOrNull(o);
        return i == null ? 0 : i;
    }

    private Integer toInt(Object o) {
        Integer i = toIntOrNull(o);
        return i == null ? 0 : i;
    }

    private Integer toIntOrNull(Object o) {
        if (o == null) return null;
        if (o instanceof Integer) return (Integer) o;
        if (o instanceof Number) return ((Number) o).intValue();
        try {
            String s = String.valueOf(o).trim();
            if (s.isEmpty()) return null;
            return Integer.parseInt(s);
        } catch (Exception e) { return null; }
    }

    private String nzStr(Object o) {
        if (o == null) return "";
        String s = String.valueOf(o);
        return s == null ? "" : s.trim();
    }

    private LocalDate toLocalDate(Object v) {
        if (v == null) return null;
        String s = String.valueOf(v).trim();
        if (s.isEmpty()) return null;
        try {
            // ISO (yyyy-MM-dd)
            if (s.length() == 10 && s.charAt(4) == '-' && s.charAt(7) == '-') {
                return LocalDate.parse(s);
            }
            // yyyy-MM-dd HH:mm:ss
            if (s.length() >= 19 && s.charAt(4) == '-' && s.charAt(7) == '-') {
                return LocalDate.parse(s.substring(0, 10));
            }
            return LocalDate.parse(s);
        } catch (Exception ignore) {
            return null;
        }
    }

    private LocalDateTime toLocalDateTime(Object v) {
        if (v == null) return null;
        String s = String.valueOf(v).trim();
        if (s.isEmpty()) return null;
        try {
            // Soporta "yyyy-MM-dd HH:mm:ss" y "yyyy-MM-dd'T'HH:mm:ss"
            if (s.contains("T")) {
                return LocalDateTime.parse(s);
            } else if (s.length() >= 19) {
                DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                return LocalDateTime.parse(s.substring(0, 19), f);
            }
            // Fallback: si vino solo fecha
            return LocalDate.parse(s).atStartOfDay();
        } catch (Exception ignore) {
            return null;
        }
    }
}

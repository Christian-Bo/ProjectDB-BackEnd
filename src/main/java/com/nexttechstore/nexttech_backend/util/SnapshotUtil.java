package com.nexttechstore.nexttech_backend.util;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public final class SnapshotUtil {

    private SnapshotUtil() {}

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true)
            .configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);

    /** Construye JSON canónico (header + detalle ordenado) a partir del map devuelto por VentasSpRepository.getVentaById */
    @SuppressWarnings("unchecked")
    public static String buildCanonicalJson(Map<String, Object> venta) {
        Map<String, Object> h = castMap(venta.get("header"));
        List<Map<String, Object>> det = (List<Map<String, Object>>) venta.get("detalle");
        if (det == null) det = Collections.emptyList();

        ObjectNode root = MAPPER.createObjectNode();

        // Header mínimo que define montos/condición y cliente
        ObjectNode header = MAPPER.createObjectNode();
        header.put("cliente_id", asInt(h.get("cliente_id")));
        header.put("tipo_pago", nvl(h.get("tipo_pago")));
        header.put("subtotal", asDecimalString(h.get("subtotal"), 4));
        header.put("descuento_general", asDecimalString(h.get("descuento_general"), 4));
        header.put("iva", asDecimalString(h.get("iva"), 4));
        header.put("total", asDecimalString(h.get("total"), 4));
        root.set("header", header);

        // Orden estable para comparar detalle
        det.sort(Comparator
                .comparing((Map<String, Object> m) -> asInt(m.get("producto_id")))
                .thenComparing(m -> asDecimalString(m.get("precio_unitario"), 4))
                .thenComparing(m -> asDecimalString(m.get("descuento_linea"), 4))
                .thenComparing(m -> nvl(m.get("lote")))
                .thenComparing(m -> isoDate(m.get("fecha_vencimiento"))));

        ArrayNode detalle = MAPPER.createArrayNode();
        for (Map<String, Object> r : det) {
            ObjectNode row = MAPPER.createObjectNode();
            row.put("producto_id", asInt(r.get("producto_id")));
            row.put("cantidad", asInt(r.get("cantidad"))); // tu SP usa INT
            row.put("precio_unitario", asDecimalString(r.get("precio_unitario"), 4));
            row.put("descuento_linea", asDecimalString(r.get("descuento_linea"), 4));
            row.put("lote", nvl(r.get("lote")));
            row.put("fecha_vencimiento", isoDate(r.get("fecha_vencimiento")));
            detalle.add(row);
        }
        root.set("detalle", detalle);

        try {
            return MAPPER.writeValueAsString(root);
        } catch (Exception e) {
            throw new RuntimeException("Error construyendo snapshot", e);
        }
    }

    /** SHA-256 en hex */
    public static String sha256Hex(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] d = md.digest(s.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(d.length * 2);
            for (byte b : d) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /** Guarda {v, hash, snapshot} como JSON en fel_acuse */
    public static String pack(String hash, String snapshotJson) {
        return "{\"v\":1,\"hash\":\"" + hash + "\",\"snapshot\":" + snapshotJson + "}";
    }

    /** Extrae hash desde fel_acuse si viene empaquetado como JSON {hash: "..."} */
    public static String tryExtractHash(String packed) {
        if (packed == null || packed.isBlank()) return null;
        try {
            var node = MAPPER.readTree(packed);
            var h = node.get("hash");
            return h != null ? h.asText() : null;
        } catch (Exception ignore) {
            // fallback simple si no es JSON
            int i = packed.indexOf("\"hash\":\"");
            if (i >= 0) {
                int j = packed.indexOf('"', i + 8);
                if (j > i) return packed.substring(i + 8, j);
            }
            return null;
        }
    }

    // ===== Helpers =====
    private static Map<String, Object> castMap(Object o) {
        if (o instanceof Map<?, ?> m) {
            @SuppressWarnings("unchecked")
            Map<String, Object> mm = (Map<String, Object>) m;
            return mm;
        }
        return Collections.emptyMap();
    }

    private static String nvl(Object o) { return o == null ? "" : String.valueOf(o).trim(); }

    private static int asInt(Object o) {
        if (o == null) return 0;
        if (o instanceof Number n) return n.intValue();
        try { return Integer.parseInt(String.valueOf(o).trim()); } catch (Exception e) { return 0; }
    }

    private static String asDecimalString(Object o, int scale) {
        if (o == null) return "0" + (scale > 0 ? "." + "0".repeat(scale) : "");
        BigDecimal bd;
        if (o instanceof BigDecimal b) bd = b;
        else if (o instanceof Number n) bd = new BigDecimal(n.toString());
        else bd = new BigDecimal(String.valueOf(o));
        bd = bd.setScale(scale, BigDecimal.ROUND_HALF_UP);
        return bd.toPlainString();
    }

    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_LOCAL_DATE;
    private static String isoDate(Object o) {
        if (o == null) return "";
        if (o instanceof java.sql.Date d) return d.toLocalDate().format(ISO);
        if (o instanceof java.util.Date d) return new java.sql.Date(d.getTime()).toLocalDate().format(ISO);
        if (o instanceof Timestamp ts) return ts.toLocalDateTime().toLocalDate().format(ISO);
        if (o instanceof LocalDate ld) return ld.format(ISO);
        return String.valueOf(o).trim();
    }
}

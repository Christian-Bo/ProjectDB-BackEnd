package com.nexttechstore.nexttech_backend.service.impl;

import com.nexttechstore.nexttech_backend.dto.facturas.FacturaDetalleDto;
import com.nexttechstore.nexttech_backend.dto.facturas.FacturaHeaderDto;
import com.nexttechstore.nexttech_backend.repository.orm.FacturasCommandRepository;
import com.nexttechstore.nexttech_backend.repository.orm.FacturasQueryRepository;
import com.nexttechstore.nexttech_backend.repository.sp.VentasSpRepository;
import com.nexttechstore.nexttech_backend.service.api.FacturasService;
import com.nexttechstore.nexttech_backend.util.SnapshotUtil;
import com.nexttechstore.nexttech_backend.util.pdf.FacturaPdfRenderer;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FacturasServiceImpl implements FacturasService {

    private final FacturasCommandRepository cmd;
    private final FacturasQueryRepository qry;
    private final FacturaPdfRenderer pdf;
    private final VentasSpRepository ventasSp; // NUEVO

    public FacturasServiceImpl(FacturasCommandRepository cmd,
                               FacturasQueryRepository qry,
                               FacturaPdfRenderer pdf,
                               VentasSpRepository ventasSp) { // NUEVO
        this.cmd = cmd;
        this.qry = qry;
        this.pdf = pdf;
        this.ventasSp = ventasSp; // NUEVO
    }

    @Override
    public int emitir(int ventaId, int serieId, int emitidaPor) {
        try {
            // 1) Snapshot/hash del estado actual de la venta
            Map<String,Object> venta = ventasSp.getVentaById(ventaId);
            String snapJson = SnapshotUtil.buildCanonicalJson(venta);
            String snapHash = SnapshotUtil.sha256Hex(snapJson);

            // 2) Revisar última factura de esa venta; si el hash es igual, rechazar duplicado
            Map<String,Object> last = qry.findUltimaFacturaPorVenta(ventaId);
            if (last != null) {
                String prevPacked = (String) last.get("fel_acuse");
                String prevHash = SnapshotUtil.tryExtractHash(prevPacked);
                if (prevHash != null && prevHash.equalsIgnoreCase(snapHash)) {
                    throw new IllegalStateException("Ya existe una factura para este estado de la venta (factura id=" + last.get("id") + ").");
                }
            }

            // 3) Emitir
            var out = cmd.emitirFactura(ventaId, serieId, emitidaPor);
            if (out.code() != 0 || out.facturaId() == null) {
                throw new IllegalStateException(out.message() == null ? "Error al emitir" : out.message());
            }

            // 4) Guardar snapshot en fel_acuse (con fallback si el campo es corto)
            String packed = SnapshotUtil.pack(snapHash, snapJson);
            try {
                qry.updateFelAcuse(out.facturaId(), packed);
            } catch (DataAccessException ex) {
                // si hay truncamiento por tamaño, guardamos sólo el hash
                try {
                    qry.updateFelAcuse(out.facturaId(), "{\"v\":1,\"hash\":\"" + snapHash + "\"}");
                } catch (DataAccessException ignore) {
                    // último intento: ignora guardar snapshot, la factura ya quedó emitida
                }
            }

            return out.facturaId();
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    @Override
    public Map<String, Object> listar(LocalDate desde, LocalDate hasta, String serie, String numero, int page, int size) {
        var lista = qry.listar(desde, hasta, serie, numero, page, size);
        var resp = new HashMap<String,Object>();
        resp.put("items", lista);
        resp.put("page", page);
        resp.put("size", size);
        return resp;
    }

    @Override
    public Map<String, Object> obtenerPorId(int id) {
        FacturaHeaderDto h = qry.obtenerHeader(id);
        if (h == null) throw new IllegalArgumentException("Factura no encontrada");
        List<FacturaDetalleDto> d = qry.obtenerDetallePorFactura(id);

        Map<String,Object> m = new HashMap<>();
        m.put("header", h);
        m.put("detalle", d);
        return m;
    }

    @Override
    public byte[] generarPdf(int id) {
        var data = this.obtenerPorId(id);
        var h = (FacturaHeaderDto) data.get("header");
        @SuppressWarnings("unchecked")
        var detalle = (List<FacturaDetalleDto>) data.get("detalle");
        return pdf.render(h, detalle);
    }
}

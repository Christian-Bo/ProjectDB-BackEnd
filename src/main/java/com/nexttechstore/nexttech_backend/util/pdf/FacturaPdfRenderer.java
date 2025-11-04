package com.nexttechstore.nexttech_backend.util.pdf;

import com.nexttechstore.nexttech_backend.dto.facturas.FacturaDetalleDto;
import com.nexttechstore.nexttech_backend.dto.facturas.FacturaHeaderDto;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

@Component
public class FacturaPdfRenderer {

    private static final Logger log = LoggerFactory.getLogger(FacturaPdfRenderer.class);
    private static final DecimalFormat DF4 = new DecimalFormat("0.0000");
    private static final DecimalFormat DF2 = new DecimalFormat("0.##");

    public byte[] render(FacturaHeaderDto h, List<FacturaDetalleDto> d) {
        String html = buildHtml(h, d);
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PdfRendererBuilder b = new PdfRendererBuilder();
            b.useFastMode();
            b.withHtmlContent(html, null);
            b.toStream(out);
            b.run();
            return out.toByteArray();
        } catch (Exception e) {
            log.error("Error generando PDF (factura id={}, serie={}, corr={})", h.id(), h.serie(), h.correlativo(), e);
            throw new RuntimeException("Error generando PDF", e);
        }
    }

    private String buildHtml(FacturaHeaderDto h, List<FacturaDetalleDto> d) {
        final String docFull = esc(h.serie()) + "-" + esc(h.correlativo());
        final String condicion = "C".equalsIgnoreCase(nz(h.tipoPago())) ? "Contado" : "Crédito";

        StringBuilder rows = new StringBuilder();
        for (FacturaDetalleDto it : d) {
            rows.append("<tr>")
                    .append("<td>").append(esc(it.producto())).append("</td>")
                    .append("<td class=\"num\"><span class=\"pill\">").append(fmt2(it.cantidad())).append("</span></td>")
                    .append("<td class=\"num\"><span class=\"pill\">").append(fmt(it.precioUnitario())).append("</span></td>")
                    .append("<td class=\"num\"><span class=\"pill\">").append(fmt(it.descuentoLinea())).append("</span></td>")
                    .append("<td class=\"num\"><span class=\"pill\">").append(fmt(it.subtotal())).append("</span></td>")
                    .append("</tr>");
        }

        return """
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="utf-8"/>
  <title>Factura %s</title>
  <style>
    /* Paleta fija y propiedades compatibles con OpenHTMLToPDF */
    /* brand #1e78ff, brand-ink #0b3b8c, ink #0f172a, muted #64748b,
       line #e5e7eb, paper #ffffff, accent #f8fafc, card-bg #eef2ff,
       card-bd #c7d2fe, thead #e9eef8, row-alt #fbfdff */

    @page {
      size: A4;
      margin: 12mm;
      /* Pie por página con numeración */
      @bottom-right {
        content: "Página " counter(page) " de " counter(pages);
        font-size: 10px;
        color: #64748b;
      }
    }

    html, body { height: 100%%; }
    body {
      margin: 0;
      background: #f6f8fb;
      color: #0f172a;
      font: 13px/1.4 system-ui, -apple-system, "Segoe UI", Roboto, "Noto Sans", Helvetica, Arial;
    }

    .wrap { min-height: 100%%; padding: 10px 0; }
    .invoice {
      width: 100%%; max-width: 700px; margin: 0 auto;
      background: #ffffff;
      border: 1px solid #e5e7eb;
      border-radius: 5px;  /* según tu preferencia */
      overflow: hidden;
    }

    /* Header */
    .header {
      padding: 14px 18px;
      border-bottom: 1px solid #e5e7eb;
      background: linear-gradient(180deg, #eef6ff 0%%, #ffffff 60%%);
      page-break-inside: avoid;
    }
    .header table { width: 100%%; border-collapse: collapse; }
    .logo {
      width: 42px; height: 42px; background: #1e78ff; color: #fff;
      font-weight: 800; text-align: center; line-height: 42px; border-radius: 12px;
      display: inline-block; letter-spacing: .5px;
    }
    .company-title { font-size: 16px; font-weight: 700; margin: 0; }
    .company-sub   { color: #64748b; font-size: 11px; margin: 2px 0 0; }
    .badge {
      display: inline-block; padding: 6px 10px; border-radius: 12px;
      background: #0f172a; color: #fff; font-weight: 800; font-size: 11px;
      border: 1px solid #0b3b8c;
    }
    .meta { text-align: right; font-size: 11px; color: #64748b; }
    .meta strong { color: #111827; }
    .chip {
      margin-top: 6px; display: inline-block; padding: 6px 12px;
      border: 1px solid #e5e7eb; border-radius: 999px; background: #fff; font-size: 11px;
    }

    /* Cards */
    .cards { padding: 12px 18px 0; }
    .cards table { width: 100%%; border-collapse: separate; border-spacing: 14px; }
    .card {
      background: #eef2ff; border: 1px solid #c7d2fe;
      border-radius: 10px; padding: 12px;
      page-break-inside: avoid;
    }
    .card h3 {
      margin: 0 0 8px; font-size: 12px; text-transform: uppercase; letter-spacing: .5px; color: #334155;
    }
    .kv { width: 100%%; border-collapse: collapse; }
    .kv td { padding: 4px 5px; font-size: 12px; vertical-align: top; }
    .kv .lbl { width: 95px; color: #64748b; }

    /* Tabla (multi-página) */
    .table-wrap { padding: 10px 18px 12px; }
    table.items { width: 100%%; border-collapse: separate; border-spacing: 0; }

    thead { display: table-header-group; }  /* Repite encabezado en cada página */
    tfoot { display: table-footer-group; }  /* Si quisieras pie por tabla */

    table.items thead th {
      background: #e9eef8; color: #334155; font-weight: 700; text-transform: uppercase;
      letter-spacing: .3px; font-size: 11px; padding: 8px 10px;
      border-top: 1px solid #e5e7eb; border-bottom: 1px solid #e5e7eb;
    }
    table.items thead th:first-child { border-left: 1px solid #e5e7eb; border-top-left-radius: 8px; }
    table.items thead th:last-child  { border-right: 1px solid #e5e7eb; border-top-right-radius: 8px; }

    table.items tbody td {
      padding: 9px 10px; font-size: 12px;
      border-left: 1px solid #e5e7eb; border-right: 1px solid #e5e7eb; border-bottom: 1px dashed #e5e7eb;
    }
    table.items tbody tr:nth-child(even) { background: #fbfdff; }

    table.items tr { page-break-inside: avoid; }  /* No partir filas */

    .num { text-align: right; }
    .pill {
      background: #ffffff; border: 1px solid #e5e7eb;
      padding: 3px 6px; border-radius: 6px; display: inline-block; min-width: 62px; text-align: right;
    }

    /* Totales + notas (no cortar) */
    .totals { padding: 2px 18px 16px; }
    .layout { width: 100%%; border-collapse: separate; border-spacing: 14px; }
    .notes {
      background: #f8fafc; border: 1px solid #e5e7eb; border-radius: 10px;
      padding: 10px; color: #475569; font-size: 11px; line-height: 1.35;
      page-break-inside: avoid;
    }
    .sum {
      border: 1px solid #c7d2fe; border-radius: 12px; background: #fff;
      page-break-inside: avoid; page-break-before: auto; /* salta entero a la siguiente si no cabe */
    }
    .sum table { width: 100%%; border-collapse: collapse; font-size: 12px; }
    .sum td { padding: 9px 12px; border-bottom: 1px solid #e5e7eb; }
    .sum tr:last-child td { border-bottom: 0; }
    .grand { font-size: 16px; font-weight: 800; }

    /* Footer documento principal (contenido propio de la factura) */
    .footer {
      border-top: 1px solid #e5e7eb; color: #64748b; font-size: 11px; padding: 10px 18px;
      page-break-inside: avoid;
    }
    .footer table { width: 100%%; border-collapse: collapse; }
    .footer a { color: #1e78ff; text-decoration: none; }
  </style>
</head>
<body>
  <div class="wrap">
    <article class="invoice" role="document" aria-label="Factura %s">
      <section class="header">
        <table>
          <tr>
            <td style="width:48px;vertical-align:top;"><span class="logo">NXS</span></td>
            <td>
              <div class="company-title">NexTech Store</div>
              <div class="company-sub">Servicios de tecnología · Guatemala</div>
            </td>
            <td class="meta" style="width:230px;">
              <div class="badge">FACTURA · %s</div>
              <div>Fecha: <strong>%s</strong></div>
            </td>
          </tr>
        </table>
      </section>

      <section class="cards" aria-label="Datos de cliente y factura">
        <table>
          <tr>
            <td>
              <div class="card">
                <h3>Cliente</h3>
                <table class="kv">
                  <tr><td class="lbl">Nombre</td><td><strong>%s</strong></td></tr>
                  <tr><td class="lbl">NIT</td><td>%s</td></tr>
                </table>
              </div>
            </td>
            <td>
              <div class="card">
                <h3>Detalles</h3>
                <table class="kv">
                  <tr><td class="lbl">Condición</td><td>%s</td></tr>
                  <tr><td class="lbl">Moneda</td><td>GTQ (Q)</td></tr>
                </table>
              </div>
            </td>
          </tr>
        </table>
      </section>

      <div class="table-wrap" aria-label="Detalle de productos">
        <table class="items">
          <thead>
            <tr>
              <th style="width:44%%">Producto</th>
              <th class="num" style="width:10%%">Cant</th>
              <th class="num" style="width:16%%">Precio</th>
              <th class="num" style="width:14%%">Desc</th>
              <th class="num" style="width:16%%">Subtotal</th>
            </tr>
          </thead>
          <tbody>%s</tbody>
        </table>
      </div>

      <section class="totals" aria-label="Totales">
        <table class="layout"><tr>
          <td style="vertical-align:top;">
            <div class="notes">
              <p><strong>Observaciones</strong></p>
              <p>Gracias por su compra.<br/>
                 Conserve este documento como comprobante.<br/>
                 Para soporte: soporte@santaana.com.gt</p>
            </div>
          </td>
          <td style="width:250px;vertical-align:top;">
            <div class="sum">
              <table>
                <tr><td class="label">Subtotal</td><td class="num">%s</td></tr>
                <tr><td class="label">Desc. Gral.</td><td class="num">%s</td></tr>
                <tr><td class="label">IVA</td><td class="num">%s</td></tr>
                <tr><td class="grand">Total</td><td class="num grand">%s</td></tr>
              </table>
            </div>
          </td>
        </tr></table>
      </section>

      <section class="footer">
        <table><tr>
          <td>Factura generada por sistema · %s</td>
          <td style="text-align:right;">
            <a href="https://rodrigoguzmandiaz.github.io/NexTech_Solutions/">NexTech_Solutions</a>
          </td>
        </tr></table>
      </section>
    </article>
  </div>
</body>
</html>
""".formatted(
                docFull,                 // <title>
                docFull,                 // aria-label
                docFull,                 // badge
                esc(h.fechaEmision()),
                esc(h.cliente()),
                esc(h.nit()),
                condicion,
                rows,                    // tbody
                fmt(h.subtotal()),
                fmt(h.descuentoGeneral()),
                fmt(h.iva()),
                fmt(h.total()),
                docFull
        );
    }

    /* ==== helpers ==== */
    private static String nz(Object o) { return o == null ? "" : String.valueOf(o); }

    private static String esc(Object s) {
        if (s == null) return "";
        String str = String.valueOf(s).replace("\\u00A0", " ").replace("&nbsp;", " ");
        return str.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;");
    }

    private static String fmt(BigDecimal n){
        if(n == null) return "0.0000";
        DecimalFormat df = (DecimalFormat) DF4.clone();
        df.setDecimalFormatSymbols(DecimalFormatSymbolsUS.get());
        return df.format(n);
    }

    private static String fmt2(BigDecimal n){
        if(n == null) return "0";
        DecimalFormat df = (DecimalFormat) DF2.clone();
        df.setDecimalFormatSymbols(DecimalFormatSymbolsUS.get());
        return df.format(n);
    }

    /** Símbolos US para separador decimal punto. */
    private static class DecimalFormatSymbolsUS {
        private static java.text.DecimalFormatSymbols symbols;
        static java.text.DecimalFormatSymbols get() {
            if (symbols == null) {
                symbols = new java.text.DecimalFormatSymbols(Locale.US);
            }
            return symbols;
        }
    }
}

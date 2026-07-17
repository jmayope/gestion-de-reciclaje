package util;

import dao.PeopleDAO;
import modelo.People;
import modelo.ProcessFlow;
import modelo.Waste;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.awt.Color;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Genera el PDF del manifiesto de un residuo, con las operaciones
 * (Recolección / Tratamiento / Valorización / Disposición final) que
 * REALMENTE existan para ese residuo en process_flows — por eso el
 * número de "cajas" de operación cambia de un manifiesto a otro.
 *
 * Requiere en el pom.xml / build.gradle:
 *   org.apache.pdfbox:pdfbox:2.0.29  (o cualquier 2.0.x)
 */
public class ManifiestoPdfExporter {

    private static final float MARGIN = 50;
    private static final float PAGE_WIDTH = PDRectangle.A4.getWidth();
    private static final float PAGE_HEIGHT = PDRectangle.A4.getHeight();
    private static final float CONTENT_WIDTH = PAGE_WIDTH - 2 * MARGIN;

    private static final Color AZUL_BARRA   = new Color(191, 214, 244);
    private static final Color AZUL_CAJA    = new Color(207, 224, 245);
    private static final Color AZUL_BORDE   = new Color(120, 155, 205);
    private static final Color AMARILLO_CAJA  = new Color(252, 233, 190);
    private static final Color AMARILLO_BORDE = new Color(210, 165, 70);
    private static final Color GRIS_CAJA    = new Color(238, 238, 238);
    private static final Color GRIS_BORDE   = new Color(160, 160, 160);
    private static final Color NEGRO        = new Color(30, 30, 30);

    private static final DateTimeFormatter FECHA_OFFSET = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FECHA_LOCAL = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // PDFBox 3.x ya no trae HELVETICA / HELVETICA_BOLD como constantes
    // estáticas: hay que instanciarlas a partir de Standard14Fonts.
    private static final PDFont HELVETICA = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
    private static final PDFont HELVETICA_BOLD = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);

    private final PeopleDAO peopleDAO = new PeopleDAO();

    private PDDocument doc;
    private PDPage page;
    private PDPageContentStream content;
    private float y;

    public void generar(Waste waste, List<ProcessFlow> cadena,
                         Map<String, String> mapaOperaciones, String rutaSalida) throws IOException {

        doc = new PDDocument();
        nuevaPagina();

        // ── Título ───────────────────────────────────────────────────────
        String titulo = "MANIFIESTO DE RESIDUO - "
                + waste.getTypeName() + " "
                + formatearCantidad(waste.getQuantity()) + " " + waste.getUnitMeasurementName();

        dibujarTextoCentrado(titulo, HELVETICA_BOLD, 16, NEGRO);
        y -= 30;

        // ── Texto inicial ────────────────────────────────────────────────
       
        String introduccion = "El presente documento certifica el seguimiento del residuo descrito a "
                + "continuación a lo largo de las distintas etapas de su proceso de gestión, conforme "
                + "a la normativa vigente sobre manejo de residuos sólidos.";
        y = dibujarParrafo(introduccion, HELVETICA, 10, MARGIN, y, CONTENT_WIDTH, 13);
        y -= 20;

        // ── Información general del residuo ─────────────────────────────
        asegurarEspacio(70);
        dibujarBarraSeccion("INFORMACIÓN GENERAL DEL RESIDUO");

        List<String> datos = new ArrayList<>();
        datos.add("Empresa generadora:  " + safe(waste.getEntityName()));
        datos.add("Tipo de residuo:  " + safe(waste.getTypeName()));
        datos.add("Cantidad:  " + formatearCantidad(waste.getQuantity()) + " " + safe(waste.getUnitMeasurementName()));
        datos.add("Peligrosidad:  " + (waste.isDangerousness() ? "PELIGROSO" : "NO PELIGROSO"));
        datos.add("Fecha de generación:  "
                + (waste.getWasteGenerationDate() != null ? waste.getWasteGenerationDate().format(FECHA_LOCAL) : "-"));
        datos.add("Fecha de registro:  "
                + (waste.getCreatedAt() != null ? waste.getCreatedAt().format(FECHA_OFFSET) : "-"));
        datos.add("Estado del residuo:  " + safe(waste.getStateName()));
        datos.add("Estado del registro:  " + (waste.isStatus() ? "ACTIVO" : "INACTIVO"));

        dibujarCajaInfo(datos);
        y -= 20;

        // ── Operaciones del residuo (dinámico) ──────────────────────────
        asegurarEspacio(40);
        dibujarBarraSeccion("OPERACIONES DEL RESIDUO");

        for (ProcessFlow pf : cadena) {
            asegurarEspacio(85);
            dibujarCajaOperacion(pf, mapaOperaciones);
            y -= 12;
        }

        y -= 15;

        // ── Texto de cierre ──────────────────────────────────────────────
        asegurarEspacio(60);
        String cierre = "Este manifiesto refleja el estado de las operaciones registradas a la fecha de "
                + "generación del documento. Cualquier actualización posterior generará un nuevo manifiesto.";
        y = dibujarParrafo(cierre, HELVETICA, 10, MARGIN, y, CONTENT_WIDTH, 13);
        y -= 25;

        // ── Fecha y lugar ────────────────────────────────────────────────
        asegurarEspacio(90);
        String fechaTexto = formatearFechaLarga(new java.util.Date());
        dibujarTextoAlineadoDerecha(fechaTexto, HELVETICA, 11, y);
        y -= 60;

        // ── Firmas ───────────────────────────────────────────────────────
        dibujarFirmas(waste, cadena);

        content.close();
        doc.save(rutaSalida);
        doc.close();
    }

    // ════════════════════════════════════════════════════════════════════
    //  BLOQUES
    // ════════════════════════════════════════════════════════════════════

    private void dibujarBarraSeccion(String texto) throws IOException {
        float alto = 26;

        content.setNonStrokingColor(AZUL_BARRA);
        content.addRect(MARGIN, y - alto, CONTENT_WIDTH, alto);
        content.fill();

        content.beginText();
        content.setFont(HELVETICA_BOLD, 12);
        content.setNonStrokingColor(NEGRO);
        content.newLineAtOffset(MARGIN + 10, y - alto + 8);
        content.showText(texto);
        content.endText();

        y -= (alto + 12);
    }

    private void dibujarCajaInfo(List<String> lineas) throws IOException {
        float lineHeight = 16;
        float alto = 20 + lineas.size() * lineHeight;

        content.setNonStrokingColor(Color.WHITE);
        content.addRect(MARGIN, y - alto, CONTENT_WIDTH, alto);
        content.fill();

        content.setStrokingColor(GRIS_BORDE);
        content.setLineWidth(1);
        content.addRect(MARGIN, y - alto, CONTENT_WIDTH, alto);
        content.stroke();

        float ly = y - 20;
        for (String linea : lineas) {
            content.beginText();
            content.setFont(HELVETICA, 11);
            content.setNonStrokingColor(NEGRO);
            content.newLineAtOffset(MARGIN + 12, ly);
            content.showText(linea);
            content.endText();
            ly -= lineHeight;
        }

        y -= (alto + 6);
    }

    private void dibujarCajaOperacion(ProcessFlow pf, Map<String, String> mapaOperaciones) throws IOException {

        String nombreOperacion = mapaOperaciones.getOrDefault(
                pf.getCurrentProcessId(), pf.getCurrentProcessId());

        String estado = pf.estadoLegible();

        Color fill; Color borde;
        switch (estado) {
            case "Completado" -> { fill = AZUL_CAJA; borde = AZUL_BORDE; }
            case "En Proceso" -> { fill = AMARILLO_CAJA; borde = AMARILLO_BORDE; }
            default           -> { fill = GRIS_CAJA; borde = GRIS_BORDE; }
        }

        String responsable = "-";
        if (pf.getResponsibleId() != null) {
            People p = peopleDAO.findById(pf.getResponsibleId());
            if (p != null && p.getFullName() != null) {
                responsable = p.getFullName();
            }
        }

        String fechaInicio = pf.getStartedAt() != null ? pf.getStartedAt().format(FECHA_OFFSET) : "-";
        String fechaFin = pf.getCompletedAt() != null ? pf.getCompletedAt().format(FECHA_OFFSET) : "-";

        float alto = 70;

        content.setNonStrokingColor(fill);
        content.addRect(MARGIN, y - alto, CONTENT_WIDTH, alto);
        content.fill();

        content.setStrokingColor(borde);
        content.setLineWidth(1);
        content.addRect(MARGIN, y - alto, CONTENT_WIDTH, alto);
        content.stroke();

        float tx = MARGIN + 12;
        float ty = y - 18;
        float leading = 16;

        escribirLinea("Operación: " + nombreOperacion, HELVETICA_BOLD, 11, tx, ty);
        ty -= leading;
        escribirLinea("Fecha Inicio: " + fechaInicio + "   Fecha Fin: " + fechaFin, HELVETICA, 10, tx, ty);
        ty -= leading;
        escribirLinea("Responsable: " + responsable, HELVETICA, 10, tx, ty);
        ty -= leading;
        escribirLinea("Estado: " + estado, HELVETICA_BOLD, 10, tx, ty);

        y -= alto;
    }

    private void dibujarFirmas(Waste waste, List<ProcessFlow> cadena) throws IOException {
        float lineaY = y;
        float anchoFirma = (CONTENT_WIDTH - 40) / 2f;

        float x1 = MARGIN;
        float x2 = MARGIN + anchoFirma + 40;

        content.setStrokingColor(NEGRO);
        content.setLineWidth(1);

        content.moveTo(x1, lineaY);
        content.lineTo(x1 + anchoFirma, lineaY);
        content.stroke();

        content.moveTo(x2, lineaY);
        content.lineTo(x2 + anchoFirma, lineaY);
        content.stroke();

        escribirLinea("Firma del Responsable", HELVETICA, 10, x1, lineaY - 15);
        escribirLinea("Firma de la Empresa: " + safe(waste.getEntityName()),
                HELVETICA, 10, x2, lineaY - 15);
    }

    // ════════════════════════════════════════════════════════════════════
    //  HELPERS DE TEXTO / PAGINACIÓN
    // ════════════════════════════════════════════════════════════════════

    private void nuevaPagina() throws IOException {
        page = new PDPage(PDRectangle.A4);
        doc.addPage(page);

        if (content != null) {
            content.close();
        }

        content = new PDPageContentStream(doc, page);
        y = PAGE_HEIGHT - MARGIN;
    }

    private void asegurarEspacio(float alturaNecesaria) throws IOException {
        if (y - alturaNecesaria < MARGIN) {
            nuevaPagina();
        }
    }

    private void dibujarTexto(String texto, PDFont font, float size, float x, Color color) throws IOException {
        content.beginText();
        content.setFont(font, size);
        content.setNonStrokingColor(color);
        content.newLineAtOffset(x, y);
        content.showText(texto);
        content.endText();
    }

    private void escribirLinea(String texto, PDFont font, float size, float x, float yPos) throws IOException {
        content.beginText();
        content.setFont(font, size);
        content.setNonStrokingColor(NEGRO);
        content.newLineAtOffset(x, yPos);
        content.showText(texto);
        content.endText();
    }

    private void dibujarTextoCentrado(String texto, PDFont font, float size, Color color) throws IOException {
        float ancho = font.getStringWidth(texto) / 1000 * size;
        float x = MARGIN + (CONTENT_WIDTH - ancho) / 2f;

        content.beginText();
        content.setFont(font, size);
        content.setNonStrokingColor(color);
        content.newLineAtOffset(x, y);
        content.showText(texto);
        content.endText();
    }

    private void dibujarTextoAlineadoDerecha(String texto, PDFont font, float size, float yPos) throws IOException {
        float ancho = font.getStringWidth(texto) / 1000 * size;
        float x = MARGIN + CONTENT_WIDTH - ancho;

        content.beginText();
        content.setFont(font, size);
        content.setNonStrokingColor(NEGRO);
        content.newLineAtOffset(x, yPos);
        content.showText(texto);
        content.endText();
    }

    /** Dibuja un párrafo con salto de línea automático y devuelve la nueva posición Y. */
    private float dibujarParrafo(String texto, PDFont font, float size,
                                  float x, float startY, float maxWidth, float leading) throws IOException {

        String[] palabras = texto.split(" ");
        StringBuilder linea = new StringBuilder();
        float curY = startY;

        for (String palabra : palabras) {
            String prueba = linea.isEmpty() ? palabra : linea + " " + palabra;
            float ancho = font.getStringWidth(prueba) / 1000 * size;

            if (ancho > maxWidth) {
                asegurarEspacioParrafo(leading);
                curY = y;
                escribirLinea(linea.toString(), font, size, x, curY);
                curY -= leading;
                y = curY;
                linea = new StringBuilder(palabra);
            } else {
                linea = new StringBuilder(prueba);
            }
        }

        if (!linea.isEmpty()) {
            asegurarEspacioParrafo(leading);
            curY = y;
            escribirLinea(linea.toString(), font, size, x, curY);
            curY -= leading;
            y = curY;
        }

        return y;
    }

    private void asegurarEspacioParrafo(float leading) throws IOException {
        if (y - leading < MARGIN) {
            nuevaPagina();
        }
    }

    private String formatearCantidad(double cantidad) {
        if (cantidad == Math.floor(cantidad)) {
            return String.valueOf((long) cantidad);
        }
        return String.valueOf(cantidad);
    }

    private String formatearFechaLarga(java.util.Date fecha) {
        SimpleDateFormat sdf = new SimpleDateFormat("'Huancayo,' d 'de' MMMM 'del' yyyy", new Locale("es", "ES"));
        return sdf.format(fecha);
    }

    private String safe(String valor) {
        return valor != null ? valor : "-";
    }
}
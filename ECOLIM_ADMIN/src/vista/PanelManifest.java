package vista;

import dao.EntityDAO;
import dao.ProcessFlowDAO;
import dao.TypeDAO;
import dao.WasteDAO;

import modelo.Entity;
import modelo.ProcessFlow;
import modelo.Waste;

import util.AuditoriaManager;
import util.ManifiestoPdfExporter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class PanelManifest extends JPanel {

    private JTable tabla;
    private DefaultTableModel modelo;

    private JComboBox<ItemComboEmpresa> cbEmpresas;
    private JComboBox<ItemComboResiduo> cbResiduos;

    private final WasteDAO wasteDAO = new WasteDAO();
    private final ProcessFlowDAO processFlowDAO = new ProcessFlowDAO();
    private final TypeDAO typeDAO = new TypeDAO();
    private final EntityDAO entityDAO = new EntityDAO();

    // code -> nombre, ej: "R" -> "RECOLECCIÓN". Se carga una sola vez.
    private Map<String, String> mapaOperaciones;

    // Pila para poder deshacer el último cambio de Activar/Desactivar.
    private final Stack<CambioEstado> pilaCambios = new Stack<>();

    // Id del usuario logueado, requerido por WasteDAO.setStatus(id, status, userId)
    // para llenar la columna updated_by. AJUSTA cómo lo obtienes en tu app real
    // (sesión estática, parámetro del login, etc.) — aquí lo recibo por constructor.
    private final long usuarioActualId;

    private static final DateTimeFormatter FDT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public PanelManifest(long usuarioActualId) {
        this.usuarioActualId = usuarioActualId;
        initComponents();
        cargarCombos();
        cargarManifiestosGeneral();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));

        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(new Color(245, 247, 250));
        panelPrincipal.setBorder(new EmptyBorder(20, 20, 20, 20));
        add(panelPrincipal, BorderLayout.CENTER);

        // ── Panel superior (TÍTULO + FILTROS) ───────────────────────────────
        JPanel panelSuperior = new JPanel();
        panelSuperior.setLayout(new BoxLayout(panelSuperior, BoxLayout.Y_AXIS));
        panelSuperior.setBackground(new Color(245, 247, 250));

        JLabel titulo = new JLabel("Manifiestos de Residuos");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 30));
        titulo.setForeground(new Color(30, 30, 30));

        JLabel subtitulo = new JLabel("Seguimiento y generación de manifiestos por empresa y tipo de residuo");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitulo.setForeground(Color.GRAY);

        panelSuperior.add(titulo);
        panelSuperior.add(Box.createVerticalStrut(4));
        panelSuperior.add(subtitulo);
        panelSuperior.add(Box.createVerticalStrut(16));

        // ── Filtros ──────────────────────────────────────────────────────────
        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        panelFiltros.setBackground(new Color(245, 247, 250));

        cbEmpresas = new JComboBox<>();
        cbResiduos = new JComboBox<>();

        cbEmpresas.setPreferredSize(new Dimension(200, 38));
        cbResiduos.setPreferredSize(new Dimension(200, 38));

        cbEmpresas.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbResiduos.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JButton btnMostrarTodo      = crearBoton("Mostrar todo", new Color(97, 97, 97));
        JButton btnFiltrarEmpresa   = crearBoton("Filtrar empresa", new Color(25, 118, 210));
        JButton btnFiltrarResiduo   = crearBoton("Filtrar residuo", new Color(25, 118, 210));
        JButton btnActivarDesact    = crearBoton("Activar/Desactivar", new Color(255, 143, 0));
        JButton btnDeshacer         = crearBoton("Deshacer", new Color(97, 97, 97));
        JButton btnGenerarPdf       = crearBoton("Generar PDF", new Color(46, 125, 50));

        panelFiltros.add(new JLabel("Empresa:"));
        panelFiltros.add(cbEmpresas);
        panelFiltros.add(btnFiltrarEmpresa);

        panelFiltros.add(new JLabel("Residuo:"));
        panelFiltros.add(cbResiduos);
        panelFiltros.add(btnFiltrarResiduo);

        panelFiltros.add(btnMostrarTodo);
        // panelFiltros.add(btnActivarDesact);
        // panelFiltros.add(btnDeshacer);
        panelFiltros.add(btnGenerarPdf);

        panelSuperior.add(panelFiltros);
        panelPrincipal.add(panelSuperior, BorderLayout.NORTH);

        // ── TABLA ────────────────────────────────────────────────────────────
        modelo = new DefaultTableModel(
                new Object[]{
                    "ID", "Empresa", "Residuo", "Cantidad", "Unidad", "Peligroso",
                    "Operación Actual", "Estado Operación", "Estado Residuo", "Registro", "Fecha"
                }, 0
        ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tabla = new JTable(modelo);
        tabla.setRowHeight(36);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabla.setGridColor(new Color(235, 235, 235));
        tabla.setShowVerticalLines(false);
        tabla.setSelectionBackground(new Color(52, 120, 246));
        tabla.setSelectionForeground(Color.WHITE);

        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabla.getTableHeader().setBackground(new Color(18, 33, 61));
        tabla.getTableHeader().setForeground(Color.WHITE);
        tabla.getTableHeader().setReorderingAllowed(false);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        JPanel cardTabla = new JPanel(new BorderLayout());
        cardTabla.setBackground(Color.WHITE);
        cardTabla.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(225, 225, 225), 1, true),
                new EmptyBorder(15, 15, 15, 15)
        ));
        cardTabla.add(scroll, BorderLayout.CENTER);

        panelPrincipal.add(cardTabla, BorderLayout.CENTER);

        // ── Eventos ──────────────────────────────────────────────────────────
        btnMostrarTodo.addActionListener(e -> cargarManifiestosGeneral());
        btnFiltrarEmpresa.addActionListener(e -> filtrarPorEmpresa());
        btnFiltrarResiduo.addActionListener(e -> filtrarPorResiduo());
        btnActivarDesact.addActionListener(e -> cambiarEstadoManifiesto());
        btnDeshacer.addActionListener(e -> deshacerCambioEstado());
        btnGenerarPdf.addActionListener(e -> generarPdfSeleccionado());
    }

    private void cargarCombos() {
        cbEmpresas.removeAllItems();
        cbResiduos.removeAllItems();

        cbEmpresas.addItem(new ItemComboEmpresa(0, "Seleccione"));
        cbResiduos.addItem(new ItemComboResiduo(null, "Seleccione"));

        for (Entity e : entityDAO.listEntities()) {
            cbEmpresas.addItem(new ItemComboEmpresa(e.getId(), e.getName()));
        }

        // WasteDAO.listTiposResiduo() ya devuelve solo types.category='TIPO_RESIDUO'
        for (String[] tipo : wasteDAO.listTiposResiduo()) {
            cbResiduos.addItem(new ItemComboResiduo(tipo[0], tipo[1]));
        }

        // Traduce códigos de operación (R/T/V/D) a nombre legible, una sola vez.
        mapaOperaciones = typeDAO.mapaCodeNombre("OPERACIONES");
    }

    private void cargarManifiestosGeneral() {
        llenarTabla(wasteDAO.listWastes());
    }

    private void filtrarPorEmpresa() {
        ItemComboEmpresa item = (ItemComboEmpresa) cbEmpresas.getSelectedItem();

        if (item == null || item.getId() == 0) {
            JOptionPane.showMessageDialog(this, "Seleccione una empresa.");
            return;
        }

        // NOTA: este método no existe todavía en tu WasteDAO, hay que agregarlo
        // (te paso el código abajo en la respuesta). Filtra por w.entity_id = ?.
        llenarTabla(wasteDAO.listByEntity(item.getId()));
    }

    private void filtrarPorResiduo() {
        ItemComboResiduo item = (ItemComboResiduo) cbResiduos.getSelectedItem();

        if (item == null || item.getCode() == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un residuo.");
            return;
        }

        // NOTA: también hay que agregarlo a tu WasteDAO. A diferencia de
        // findByType() (que hace LIKE y puede confundirse con códigos de una
        // sola letra), este filtra por w.type = ? exacto.
        llenarTabla(wasteDAO.listByTypeCode(item.getCode()));
    }

    private void llenarTabla(List<Waste> lista) {
        modelo.setRowCount(0);

        for (Waste w : lista) {

            ProcessFlow ultimo = processFlowDAO.obtenerUltimoProceso(w.getId());

            String operacionActual = "-";
            String estadoOperacion = "-";

            if (ultimo != null) {
                operacionActual = mapaOperaciones.getOrDefault(
                        ultimo.getCurrentProcessId(), ultimo.getCurrentProcessId());
                estadoOperacion = ultimo.estadoLegible();
            }

            modelo.addRow(new Object[]{
                w.getId(),
                w.getEntityName(),
                w.getTypeName(),
                w.getQuantity(),
                w.getUnitMeasurementName(),
                w.isDangerousness() ? "SÍ" : "NO",
                operacionActual,
                estadoOperacion,
                w.getStateName(),
                w.isStatus() ? "ACTIVO" : "INACTIVO",
                w.getCreatedAt() != null ? w.getCreatedAt().format(FDT) : "-"
            });
        }
    }

    // ════════════════════════════════════════════════════════════════════
    //  ACTIVAR / DESACTIVAR  +  DESHACER  (tomado de PanelUsers)
    // ════════════════════════════════════════════════════════════════════

    private void cambiarEstadoManifiesto() {

        int fila = tabla.getSelectedRow();

        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un manifiesto.");
            return;
        }

        long id = ((Number) modelo.getValueAt(fila, 0)).longValue();

        Waste w = wasteDAO.findById(id);

        if (w == null) {
            return;
        }

        String accion = w.isStatus() ? "desactivar" : "activar";

        int op = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro de " + accion + " este manifiesto?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION
        );

        if (op == JOptionPane.YES_OPTION) {

            boolean estadoAnterior = w.isStatus();
            boolean nuevoEstado = !estadoAnterior;

            if (wasteDAO.setStatus(id, nuevoEstado, usuarioActualId)) {

                // Guardamos el estado anterior para poder deshacer.
                pilaCambios.push(new CambioEstado(id, estadoAnterior));

                AuditoriaManager.registrar(
                        "Manifiesto " + id + " cambiado a "
                        + (nuevoEstado ? "ACTIVO" : "INACTIVO")
                );

                JOptionPane.showMessageDialog(this, "Estado actualizado correctamente.");
                cargarManifiestosGeneral();

            } else {
                JOptionPane.showMessageDialog(this, "No se pudo actualizar el estado.");
            }
        }
    }

    private void deshacerCambioEstado() {

        if (pilaCambios.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay cambios de estado para deshacer.");
            return;
        }

        CambioEstado cambio = pilaCambios.pop();

        if (wasteDAO.setStatus(cambio.wasteId, cambio.estadoAnterior, usuarioActualId)) {

            AuditoriaManager.registrar(
                    "Deshecho cambio de estado del manifiesto " + cambio.wasteId
            );

            JOptionPane.showMessageDialog(this, "Cambio deshecho correctamente.");
            cargarManifiestosGeneral();

        } else {
            JOptionPane.showMessageDialog(this, "No se pudo deshacer el cambio.");
        }
    }

    // ════════════════════════════════════════════════════════════════════
    //  EXPORTAR PDF
    // ════════════════════════════════════════════════════════════════════

    private void generarPdfSeleccionado() {

        int fila = tabla.getSelectedRow();

        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un manifiesto para generar el PDF.");
            return;
        }

        long id = ((Number) modelo.getValueAt(fila, 0)).longValue();

        Waste w = wasteDAO.findById(id);

        if (w == null) {
            JOptionPane.showMessageDialog(this, "No se encontró el manifiesto seleccionado.");
            return;
        }

        List<ProcessFlow> cadena = processFlowDAO.listarCadenaPorResiduo(w.getId());

        if (cadena.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Este residuo aún no tiene operaciones registradas.");
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new java.io.File(
                "manifiesto_" + w.getTypeName().replace(" ", "_") + "_" + w.getId() + ".pdf"));

        int opcion = chooser.showSaveDialog(this);

        if (opcion == JFileChooser.APPROVE_OPTION) {

            String ruta = chooser.getSelectedFile().getAbsolutePath();
            if (!ruta.toLowerCase().endsWith(".pdf")) {
                ruta += ".pdf";
            }

            try {
                new ManifiestoPdfExporter().generar(w, cadena, mapaOperaciones, ruta);
                JOptionPane.showMessageDialog(this, "PDF generado correctamente.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al generar PDF: " + ex.getMessage());
                System.out.println("Error generarPdfSeleccionado: " + ex.getMessage());
            }
        }
    }

    // ════════════════════════════════════════════════════════════════════
    //  UTIL
    // ════════════════════════════════════════════════════════════════════

    private JButton crearBoton(String texto, Color color) {
        JButton btn = new JButton(texto);
        btn.setPreferredSize(new Dimension(150, 40));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private static class ItemComboEmpresa {
        private final long id;
        private final String texto;

        ItemComboEmpresa(long id, String texto) {
            this.id = id;
            this.texto = texto;
        }

        long getId() { return id; }

        @Override
        public String toString() { return texto; }
    }

    private static class ItemComboResiduo {
        private final String code;
        private final String texto;

        ItemComboResiduo(String code, String texto) {
            this.code = code;
            this.texto = texto;
        }

        String getCode() { return code; }

        @Override
        public String toString() { return texto; }
    }

    private static class CambioEstado {
        final long wasteId;
        final boolean estadoAnterior;

        CambioEstado(long wasteId, boolean estadoAnterior) {
            this.wasteId = wasteId;
            this.estadoAnterior = estadoAnterior;
        }
    }
}
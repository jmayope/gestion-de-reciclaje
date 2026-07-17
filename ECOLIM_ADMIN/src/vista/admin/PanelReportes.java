package vista.admin;

import dao.ReporteDAO;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileOutputStream;
import java.sql.Timestamp;
import java.util.List;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class PanelReportes extends JPanel {

    private JTable tabla;
    private DefaultTableModel modelo;
    private JComboBox<ItemCombo> cbUsuarios;
    private JComboBox<ItemCombo> cbUbicaciones;

    private final ReporteDAO reporteDAO = new ReporteDAO();

    public PanelReportes() {
        initComponents();
        cargarCombos();
        cargarReporteGeneral();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));

        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(new Color(245, 247, 247));
        panelPrincipal.setBorder(new EmptyBorder(20, 20, 20, 20));
        add(panelPrincipal, BorderLayout.CENTER);

        // ── Panel superior (TÍTULO + CONTROLES) ─────────────────────────────
        JPanel panelSuperior = new JPanel();
        panelSuperior.setLayout(new BoxLayout(panelSuperior, BoxLayout.Y_AXIS));
        panelSuperior.setBackground(new Color(245, 247, 247));

        JLabel titulo = new JLabel("Reportes de Reciclaje");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 30));
        titulo.setForeground(new Color(30, 30, 30));

        JLabel subtitulo = new JLabel("Generación y filtrado de reportes");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitulo.setForeground(Color.GRAY);

        panelSuperior.add(titulo);
        panelSuperior.add(Box.createVerticalStrut(4));
        panelSuperior.add(subtitulo);
        panelSuperior.add(Box.createVerticalStrut(16));

        // ── Panel filtros ────────────────────────────────────────────────────
        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        panelFiltros.setBackground(new Color(245, 247, 247));

        cbUsuarios = new JComboBox<>();
        cbUbicaciones = new JComboBox<>();

        cbUsuarios.setPreferredSize(new Dimension(200, 38));
        cbUbicaciones.setPreferredSize(new Dimension(200, 38));

        cbUsuarios.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbUbicaciones.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JButton btnMostrarTodo       = crearBoton("Mostrar todo", new Color(97, 97, 97));
        JButton btnFiltrarUsuario    = crearBoton("Filtrar usuario", new Color(25, 118, 210));
        JButton btnFiltrarUbicacion  = crearBoton("Filtrar ubicación", new Color(25, 118, 210));
        JButton btnExcelUsuario      = crearBoton("Excel usuario", new Color(46, 125, 50));
        JButton btnExcelUbicacion    = crearBoton("Excel ubicación", new Color(46, 125, 50));

        panelFiltros.add(new JLabel("Usuario:"));
        panelFiltros.add(cbUsuarios);
        panelFiltros.add(btnFiltrarUsuario);
        panelFiltros.add(btnExcelUsuario);

        panelFiltros.add(new JLabel("Ubicación:"));
        panelFiltros.add(cbUbicaciones);
        panelFiltros.add(btnFiltrarUbicacion);
        panelFiltros.add(btnExcelUbicacion);

        panelFiltros.add(btnMostrarTodo);

        panelSuperior.add(panelFiltros);
        panelPrincipal.add(panelSuperior, BorderLayout.NORTH);

        // ── TABLA ────────────────────────────────────────────────────────────
        modelo = new DefaultTableModel(
                new Object[]{
                    "ID Registro", "ID Usuario", "Trabajador",
                    "ID Ubicación", "Ubicación", "Residuo",
                    "Cantidad", "Unidad", "Fecha", "Observaciones"
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
        btnMostrarTodo.addActionListener(e -> cargarReporteGeneral());
        btnFiltrarUsuario.addActionListener(e -> filtrarPorUsuario());
        btnFiltrarUbicacion.addActionListener(e -> filtrarPorUbicacion());
        btnExcelUsuario.addActionListener(e -> exportarExcelPorUsuario());
        btnExcelUbicacion.addActionListener(e -> exportarExcelPorUbicacion());
    }

    private void cargarCombos() {
        cbUsuarios.removeAllItems();
        cbUbicaciones.removeAllItems();

        cbUsuarios.addItem(new ItemCombo(0, "Seleccione"));
        cbUbicaciones.addItem(new ItemCombo(0, "Seleccione"));

        List<Object[]> usuarios = reporteDAO.listarUsuariosTrabajadores();
        for (Object[] fila : usuarios) {
            cbUsuarios.addItem(new ItemCombo((int) fila[0], fila[1].toString()));
        }

        List<Object[]> ubicaciones = reporteDAO.listarUbicaciones();
        for (Object[] fila : ubicaciones) {
            cbUbicaciones.addItem(new ItemCombo((int) fila[0], fila[1].toString()));
        }
    }

    private void cargarReporteGeneral() {
        llenarTabla(reporteDAO.listarReporteGeneral());
    }

    private void filtrarPorUsuario() {
        ItemCombo item = (ItemCombo) cbUsuarios.getSelectedItem();

        if (item == null || item.getId() == 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario.");
            return;
        }

        llenarTabla(reporteDAO.listarReportePorUsuario(item.getId()));
    }

    private void filtrarPorUbicacion() {
        ItemCombo item = (ItemCombo) cbUbicaciones.getSelectedItem();

        if (item == null || item.getId() == 0) {
            JOptionPane.showMessageDialog(this, "Seleccione una ubicación.");
            return;
        }

        llenarTabla(reporteDAO.listarReportePorUbicacion(item.getId()));
    }

    private void llenarTabla(List<Object[]> lista) {
        modelo.setRowCount(0);

        for (Object[] fila : lista) {
            modelo.addRow(fila);
        }
    }

    private void exportarExcelPorUsuario() {
        ItemCombo item = (ItemCombo) cbUsuarios.getSelectedItem();

        if (item == null || item.getId() == 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario para exportar.");
            return;
        }

        List<Object[]> datos = reporteDAO.listarReportePorUsuario(item.getId());

        if (datos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ese usuario no tiene registros.");
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new java.io.File("reporte_usuario_" + item.getTexto().replace(" ", "_") + ".xlsx"));

        int opcion = chooser.showSaveDialog(this);

        if (opcion == JFileChooser.APPROVE_OPTION) {
            exportarExcel(datos, chooser.getSelectedFile().getAbsolutePath(), "Reporte por Usuario");
        }
    }

    private void exportarExcelPorUbicacion() {
        ItemCombo item = (ItemCombo) cbUbicaciones.getSelectedItem();

        if (item == null || item.getId() == 0) {
            JOptionPane.showMessageDialog(this, "Seleccione una ubicación para exportar.");
            return;
        }

        List<Object[]> datos = reporteDAO.listarReportePorUbicacion(item.getId());

        if (datos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Esa ubicación no tiene registros.");
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new java.io.File("reporte_ubicacion_" + item.getTexto().replace(" ", "_") + ".xlsx"));

        int opcion = chooser.showSaveDialog(this);

        if (opcion == JFileChooser.APPROVE_OPTION) {
            exportarExcel(datos, chooser.getSelectedFile().getAbsolutePath(), "Reporte por Ubicación");
        }
    }

    private void exportarExcel(List<Object[]> datos, String ruta, String nombreHoja) {
        if (!ruta.toLowerCase().endsWith(".xlsx")) {
            ruta += ".xlsx";
        }

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(nombreHoja);

            String[] columnas = {
                    "ID Registro", "ID Usuario", "Trabajador", "ID Ubicación",
                    "Ubicación", "Residuo", "Cantidad", "Unidad", "Fecha", "Observaciones"
            };

            Row header = sheet.createRow(0);
            for (int i = 0; i < columnas.length; i++) {
                header.createCell(i).setCellValue(columnas[i]);
            }

            int filaNum = 1;
            for (Object[] filaDatos : datos) {
                Row fila = sheet.createRow(filaNum++);

                fila.createCell(0).setCellValue(Integer.parseInt(filaDatos[0].toString()));
                fila.createCell(1).setCellValue(Integer.parseInt(filaDatos[1].toString()));
                fila.createCell(2).setCellValue(filaDatos[2] != null ? filaDatos[2].toString() : "");
                fila.createCell(3).setCellValue(Integer.parseInt(filaDatos[3].toString()));
                fila.createCell(4).setCellValue(filaDatos[4] != null ? filaDatos[4].toString() : "");
                fila.createCell(5).setCellValue(filaDatos[5] != null ? filaDatos[5].toString() : "");
                fila.createCell(6).setCellValue(Double.parseDouble(filaDatos[6].toString()));
                fila.createCell(7).setCellValue(filaDatos[7] != null ? filaDatos[7].toString() : "");

                Object fechaObj = filaDatos[8];
                if (fechaObj instanceof Timestamp timestamp) {
                    fila.createCell(8).setCellValue(timestamp.toString());
                } else {
                    fila.createCell(8).setCellValue(fechaObj != null ? fechaObj.toString() : "");
                }

                fila.createCell(9).setCellValue(filaDatos[9] != null ? filaDatos[9].toString() : "");
            }

            for (int i = 0; i < columnas.length; i++) {
                sheet.autoSizeColumn(i);
            }

            try (FileOutputStream out = new FileOutputStream(ruta)) {
                workbook.write(out);
            }

            JOptionPane.showMessageDialog(this, "Excel generado correctamente.");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al generar Excel: " + e.getMessage());
            System.out.println("Error exportarExcel: " + e.getMessage());
        }
    }

    private static class ItemCombo {
        private final int id;
        private final String texto;

        public ItemCombo(int id, String texto) {
            this.id = id;
            this.texto = texto;
        }

        public int getId() {
            return id;
        }

        public String getTexto() {
            return texto;
        }

        @Override
        public String toString() {
            return texto;
        }
    }
    
    // ════════════════════════════════════════════════════════════════════════
    //  UTIL
    // ════════════════════════════════════════════════════════════════════════

    private JButton crearBoton(String texto, Color color) {
        JButton btn = new JButton(texto);
        btn.setPreferredSize(new Dimension(130, 40));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
package vista;

import dao.UbicacionDAO;
import modelo.Ubicacion;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class PanelUbicaciones extends JPanel {

    private JTable tabla;
    private DefaultTableModel modelo;
    private JTextField txtBuscar;
    private final UbicacionDAO ubicacionDAO = new UbicacionDAO();

    public PanelUbicaciones() {
        initComponents();
        cargarUbicaciones();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));

        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(new Color(245, 247, 250));
        panelPrincipal.setBorder(new EmptyBorder(20, 20, 20, 20));
        add(panelPrincipal, BorderLayout.CENTER);

        // ── Panel Superior ─────────────────────────────────────────────
        JPanel panelSuperior = new JPanel();
        panelSuperior.setLayout(new BoxLayout(panelSuperior, BoxLayout.Y_AXIS));
        panelSuperior.setBackground(new Color(245, 247, 250));

        JLabel titulo = new JLabel("Gestión de Ubicaciones");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 30));
        titulo.setForeground(new Color(30, 30, 30));

        JLabel subtitulo = new JLabel("Administración de ubicaciones registradas");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitulo.setForeground(Color.GRAY);

        panelSuperior.add(titulo);
        panelSuperior.add(Box.createVerticalStrut(4));
        panelSuperior.add(subtitulo);
        panelSuperior.add(Box.createVerticalStrut(16));

        // ── Barra de búsqueda ─────────────────────────────────────────
        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        panelBusqueda.setBackground(new Color(245, 247, 250));

        JLabel lblBuscar = new JLabel("Buscar:");
        lblBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        txtBuscar = new JTextField();
        txtBuscar.setPreferredSize(new Dimension(300, 38));
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        panelBusqueda.add(lblBuscar);
        panelBusqueda.add(txtBuscar);

        panelSuperior.add(panelBusqueda);

        panelPrincipal.add(panelSuperior, BorderLayout.NORTH);

        // ── Tabla ─────────────────────────────────────────────────────
        modelo = new DefaultTableModel(
                new Object[]{"ID", "Nombre del Lugar", "Dirección"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
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
                new EmptyBorder(15, 15, 15, 15)));

        cardTabla.add(scroll, BorderLayout.CENTER);

        panelPrincipal.add(cardTabla, BorderLayout.CENTER);

        // ── Botones ───────────────────────────────────────────────────
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15));
        panelBotones.setBackground(new Color(245, 247, 250));

        JButton btnAgregar = crearBoton("Agregar", new Color(46, 125, 50));
        JButton btnModificar = crearBoton("Modificar", new Color(25, 118, 210));
        JButton btnEliminar = crearBoton("Eliminar", new Color(198, 40, 40));

        btnAgregar.addActionListener(e -> agregarUbicacion());
        btnModificar.addActionListener(e -> modificarUbicacion());
        btnEliminar.addActionListener(e -> eliminarUbicacion());

        panelBotones.add(btnAgregar);
        panelBotones.add(btnModificar);
        panelBotones.add(btnEliminar);

        panelPrincipal.add(panelBotones, BorderLayout.SOUTH);

        // ── Búsqueda en tiempo real ──────────────────────────────────
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                buscarUbicaciones();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                buscarUbicaciones();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                buscarUbicaciones();
            }
        });
    }

    private void cargarUbicaciones() {
        modelo.setRowCount(0);
        List<Ubicacion> lista = ubicacionDAO.listarUbicaciones();

        for (Ubicacion u : lista) {
            modelo.addRow(new Object[]{
                    u.getIdUbicacion(),
                    u.getNombreLugar(),
                    u.getDireccion()
            });
        }
    }

    private void buscarUbicaciones() {
        String texto = txtBuscar.getText().trim();

        if (texto.isEmpty()) {
            cargarUbicaciones();
            return;
        }

        modelo.setRowCount(0);
        List<Ubicacion> lista = ubicacionDAO.buscarUbicaciones(texto);

        for (Ubicacion u : lista) {
            modelo.addRow(new Object[]{
                    u.getIdUbicacion(),
                    u.getNombreLugar(),
                    u.getDireccion()
            });
        }
    }

    private void agregarUbicacion() {
        JTextField txtNombre = new JTextField();
        JTextField txtDireccion = new JTextField();

        Object[] campos = {
                "Nombre del lugar:", txtNombre,
                "Dirección:", txtDireccion
        };

        int op = JOptionPane.showConfirmDialog(this, campos, "Agregar Ubicación", JOptionPane.OK_CANCEL_OPTION);
        if (op == JOptionPane.OK_OPTION) {
            Ubicacion u = new Ubicacion();
            u.setNombreLugar(txtNombre.getText().trim());
            u.setDireccion(txtDireccion.getText().trim());

            if (ubicacionDAO.insertarUbicacion(u)) {
                JOptionPane.showMessageDialog(this, "Ubicación agregada correctamente.");
                buscarUbicaciones();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo agregar la ubicación.");
            }
        }
    }

    private void modificarUbicacion() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una ubicación.");
            return;
        }

        int idUbicacion = (int) modelo.getValueAt(fila, 0);
        Ubicacion u = ubicacionDAO.buscarPorId(idUbicacion);

        if (u == null) {
            JOptionPane.showMessageDialog(this, "No se encontró la ubicación.");
            return;
        }

        JTextField txtNombre = new JTextField(u.getNombreLugar());
        JTextField txtDireccion = new JTextField(u.getDireccion());

        Object[] campos = {
                "Nombre del lugar:", txtNombre,
                "Dirección:", txtDireccion
        };

        int op = JOptionPane.showConfirmDialog(this, campos, "Modificar Ubicación", JOptionPane.OK_CANCEL_OPTION);
        if (op == JOptionPane.OK_OPTION) {
            u.setNombreLugar(txtNombre.getText().trim());
            u.setDireccion(txtDireccion.getText().trim());

            if (ubicacionDAO.actualizarUbicacion(u)) {
                JOptionPane.showMessageDialog(this, "Ubicación actualizada correctamente.");
                buscarUbicaciones();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo actualizar la ubicación.");
            }
        }
    }

    private void eliminarUbicacion() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una ubicación.");
            return;
        }

        int idUbicacion = (int) modelo.getValueAt(fila, 0);

        int op = JOptionPane.showConfirmDialog(this,
                "¿Eliminar la ubicación seleccionada?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION);

        if (op == JOptionPane.YES_OPTION) {
            if (ubicacionDAO.eliminarUbicacion(idUbicacion)) {
                JOptionPane.showMessageDialog(this, "Ubicación eliminada.");
                buscarUbicaciones();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo eliminar la ubicación.");
            }
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
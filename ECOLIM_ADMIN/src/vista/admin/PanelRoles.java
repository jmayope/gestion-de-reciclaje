package vista.admin;

import dao.RolDAO;
import modelo.Rol;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class PanelRoles extends JPanel {

    private JTable tabla;
    private DefaultTableModel modelo;
    private JTextField txtBuscar;

    private final RolDAO rolDAO = new RolDAO();

    public PanelRoles() {
        initComponents();
        cargarRoles();
    }

    private void initComponents() {

        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));

        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(new Color(245, 247, 250));
        panelPrincipal.setBorder(new EmptyBorder(20, 20, 20, 20));

        add(panelPrincipal, BorderLayout.CENTER);

        // ── Encabezado ───────────────────────────────────────────────
        JPanel panelSuperior = new JPanel();
        panelSuperior.setLayout(new BoxLayout(panelSuperior, BoxLayout.Y_AXIS));
        panelSuperior.setBackground(new Color(245, 247, 250));

        JLabel titulo = new JLabel("Gestión de Roles");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 30));
        titulo.setForeground(new Color(30, 30, 30));

        JLabel subtitulo = new JLabel("Administración de roles del sistema");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitulo.setForeground(Color.GRAY);

        panelSuperior.add(titulo);
        panelSuperior.add(Box.createVerticalStrut(4));
        panelSuperior.add(subtitulo);
        panelSuperior.add(Box.createVerticalStrut(16));

        // ── Búsqueda ────────────────────────────────────────────────
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

        // ── Tabla ───────────────────────────────────────────────────
        modelo = new DefaultTableModel(
                new Object[]{
                        "ID",
                        "Nombre"
                }, 0
        ) {
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

        // ── Botones ─────────────────────────────────────────────────
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15));
        panelBotones.setBackground(new Color(245, 247, 250));

        JButton btnAgregar = crearBoton("Agregar", new Color(46, 125, 50));
        JButton btnModificar = crearBoton("Modificar", new Color(25, 118, 210));
        JButton btnEliminar = crearBoton("Eliminar", new Color(198, 40, 40));

        btnAgregar.addActionListener(e -> agregarRol());
        btnModificar.addActionListener(e -> modificarRol());
        btnEliminar.addActionListener(e -> eliminarRol());

        panelBotones.add(btnAgregar);
        panelBotones.add(btnModificar);
        panelBotones.add(btnEliminar);

        panelPrincipal.add(panelBotones, BorderLayout.SOUTH);

        // ── Listener búsqueda ──────────────────────────────────────
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                buscarRoles();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                buscarRoles();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                buscarRoles();
            }
        });
    }

    private void cargarRoles() {

        modelo.setRowCount(0);

        List<Rol> lista = rolDAO.listarRoles();

        for (Rol r : lista) {

            modelo.addRow(new Object[]{
                    r.getId(),
                    r.getName()
            });
        }
    }

    private void buscarRoles() {

        String texto = txtBuscar.getText().trim().toLowerCase();

        modelo.setRowCount(0);

        List<Rol> lista = rolDAO.listarRoles();

        for (Rol r : lista) {

            if (
                    String.valueOf(r.getId()).contains(texto)
                            || (r.getName() != null
                            && r.getName().toLowerCase().contains(texto))
            ) {

                modelo.addRow(new Object[]{
                        r.getId(),
                        r.getName()
                });
            }
        }
    }

    private void agregarRol() {

        JTextField txtNombre = new JTextField();

        Object[] campos = {
                "Nombre:", txtNombre
        };

        int op = JOptionPane.showConfirmDialog(
                this,
                campos,
                "Agregar Rol",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (op == JOptionPane.OK_OPTION) {

            Rol r = new Rol();

            r.setName(txtNombre.getText().trim());

            if (rolDAO.insertarRol(r)) {

                JOptionPane.showMessageDialog(this,
                        "Rol agregado correctamente.");

                cargarRoles();

            } else {

                JOptionPane.showMessageDialog(this,
                        "No se pudo agregar el rol.");
            }
        }
    }

    private void modificarRol() {

        int fila = tabla.getSelectedRow();

        if (fila == -1) {

            JOptionPane.showMessageDialog(this,
                    "Seleccione un rol.");

            return;
        }

        long id = Long.parseLong(
                modelo.getValueAt(fila, 0).toString()
        );

        Rol r = rolDAO.buscarPorId(id);

        if (r == null) {

            JOptionPane.showMessageDialog(this,
                    "No se encontró el rol.");

            return;
        }

        JTextField txtNombre = new JTextField(r.getName());

        Object[] campos = {
                "Nombre:", txtNombre
        };

        int op = JOptionPane.showConfirmDialog(
                this,
                campos,
                "Modificar Rol",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (op == JOptionPane.OK_OPTION) {

            r.setName(txtNombre.getText().trim());

            if (rolDAO.actualizarRol(r)) {

                JOptionPane.showMessageDialog(this,
                        "Rol actualizado correctamente.");

                cargarRoles();

            } else {

                JOptionPane.showMessageDialog(this,
                        "No se pudo actualizar el rol.");
            }
        }
    }

    private void eliminarRol() {

        int fila = tabla.getSelectedRow();

        if (fila == -1) {

            JOptionPane.showMessageDialog(this,
                    "Seleccione un rol.");

            return;
        }

        long id = Long.parseLong(
                modelo.getValueAt(fila, 0).toString()
        );

        int op = JOptionPane.showConfirmDialog(
                this,
                "¿Eliminar el rol seleccionado?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION
        );

        if (op == JOptionPane.YES_OPTION) {

            if (rolDAO.eliminarRol(id)) {

                JOptionPane.showMessageDialog(this,
                        "Rol eliminado.");

                cargarRoles();

            } else {

                JOptionPane.showMessageDialog(this,
                        "No se pudo eliminar el rol.");
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

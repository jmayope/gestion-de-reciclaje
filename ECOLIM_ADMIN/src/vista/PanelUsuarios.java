package vista;

import dao.UsuarioDAO;
import modelo.Usuario;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class PanelUsuarios extends JPanel {

    private JTable tabla;
    private DefaultTableModel modelo;
    private JTextField txtBuscar;
    private JLabel lblEstado;
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    
    public PanelUsuarios() {
        initComponents();
        cargarUsuarios();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));

        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(new Color(245, 247, 250));
        panelPrincipal.setBorder(new EmptyBorder(20, 20, 20, 20));

        add(panelPrincipal, BorderLayout.CENTER);

        // ═══════════════════════════════════════════════════════════════
        // PANEL SUPERIOR
        // ═══════════════════════════════════════════════════════════════
        JPanel panelSuperior = new JPanel();
        panelSuperior.setLayout(new BoxLayout(panelSuperior, BoxLayout.Y_AXIS));
        panelSuperior.setBackground(new Color(245, 247, 250));

        JLabel titulo = new JLabel("Gestión de Usuarios");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 30));
        titulo.setForeground(new Color(30, 30, 30));

        JLabel subtitulo = new JLabel("Administración de usuarios del sistema");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitulo.setForeground(Color.GRAY);

        lblEstado = new JLabel(" ");
        lblEstado.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblEstado.setForeground(new Color(25, 118, 210));

        panelSuperior.add(titulo);
        panelSuperior.add(Box.createVerticalStrut(4));
        panelSuperior.add(subtitulo);
        panelSuperior.add(Box.createVerticalStrut(2));
        panelSuperior.add(lblEstado);
        panelSuperior.add(Box.createVerticalStrut(16));

        // ═══════════════════════════════════════════════════════════════
        // BÚSQUEDA
        // ═══════════════════════════════════════════════════════════════
        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        panelBusqueda.setBackground(new Color(245, 247, 250));

        JLabel lblBuscar = new JLabel("Buscar:");
        lblBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        txtBuscar = new JTextField();
        txtBuscar.setPreferredSize(new Dimension(320, 38));
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        panelBusqueda.add(lblBuscar);
        panelBusqueda.add(txtBuscar);

        panelSuperior.add(panelBusqueda);

        panelPrincipal.add(panelSuperior, BorderLayout.NORTH);

        // ═══════════════════════════════════════════════════════════════
        // TABLA
        // ═══════════════════════════════════════════════════════════════
        modelo = new DefaultTableModel(
                new Object[]{
                    "ID",
                    "Nombre",
                    "Apellido",
                    "DNI",
                    "Correo",
                    "Rol"
                }, 0) {
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

        tabla.getTableHeader().setFont(
                new Font("Segoe UI", Font.BOLD, 13));

        tabla.getTableHeader().setBackground(
                new Color(18, 33, 61));

        tabla.getTableHeader().setForeground(Color.WHITE);

        tabla.getTableHeader().setReorderingAllowed(false);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        JPanel cardTabla = new JPanel(new BorderLayout());
        cardTabla.setBackground(Color.WHITE);

        cardTabla.setBorder(
                BorderFactory.createCompoundBorder(
                        new LineBorder(
                                new Color(225, 225, 225),
                                1,
                                true),
                        new EmptyBorder(15, 15, 15, 15)
                )
        );

        cardTabla.add(scroll, BorderLayout.CENTER);

        panelPrincipal.add(cardTabla, BorderLayout.CENTER);

        // ═══════════════════════════════════════════════════════════════
        // BOTONES
        // ═══════════════════════════════════════════════════════════════
        JPanel panelBotones = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 10, 15));

        panelBotones.setBackground(new Color(245, 247, 250));

        JButton btnAgregar = crearBoton(
                "Agregar",
                new Color(46, 125, 50));

        JButton btnModificar = crearBoton(
                "Modificar",
                new Color(25, 118, 210));

        JButton btnEliminar = crearBoton(
                "Eliminar",
                new Color(198, 40, 40));

        btnAgregar.addActionListener(e -> agregarUsuario());
        btnModificar.addActionListener(e -> modificarUsuario());
        btnEliminar.addActionListener(e -> eliminarUsuario());

        panelBotones.add(btnAgregar);
        panelBotones.add(btnModificar);
        panelBotones.add(btnEliminar);

        panelPrincipal.add(panelBotones, BorderLayout.SOUTH);

        // ═══════════════════════════════════════════════════════════════
        // BÚSQUEDA EN TIEMPO REAL
        // ═══════════════════════════════════════════════════════════════
        txtBuscar.getDocument().addDocumentListener(
                new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                buscarUsuarios();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                buscarUsuarios();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                buscarUsuarios();
            }
        });
    }

    private void cargarUsuarios() {
        modelo.setRowCount(0);
        List<Usuario> lista = usuarioDAO.listarUsuarios();

        for (Usuario u : lista) {
            modelo.addRow(new Object[]{
                u.getIdUsuario(),
                u.getNombre(),
                u.getApellido(),
                u.getDni(),
                u.getCorreo(),
                u.getRol().toUpperCase()
            });
        }
    }

    private void buscarUsuarios() {
        String texto = txtBuscar.getText().trim();

        if (texto.isEmpty()) {
            cargarUsuarios();
            return;
        }

        modelo.setRowCount(0);
        List<Usuario> lista = usuarioDAO.buscarUsuarios(texto);

        for (Usuario u : lista) {
            modelo.addRow(new Object[]{
                u.getIdUsuario(),
                u.getNombre(),
                u.getApellido(),
                u.getDni(),
                u.getCorreo(),
                u.getRol()
            });
        }
    }

    private void agregarUsuario() {
        JTextField txtNombre = new JTextField();
        JTextField txtApellido = new JTextField();
        JTextField txtDni = new JTextField();
        JTextField txtCorreo = new JTextField();
        JTextField txtPassword = new JTextField();
        JComboBox<String> cbRol = new JComboBox<>(new String[]{"admin", "trabajador"});

        Object[] campos = {
            "Nombre:", txtNombre,
            "Apellido:", txtApellido,
            "DNI:", txtDni,
            "Correo:", txtCorreo,
            "Contraseña:", txtPassword,
            "Rol:", cbRol
        };

        int op = JOptionPane.showConfirmDialog(this, campos, "Agregar Usuario", JOptionPane.OK_CANCEL_OPTION);
        if (op == JOptionPane.OK_OPTION) {
            Usuario u = new Usuario();
            u.setNombre(txtNombre.getText().trim());
            u.setApellido(txtApellido.getText().trim());
            u.setDni(txtDni.getText().trim());
            u.setCorreo(txtCorreo.getText().trim());
            u.setPassword(txtPassword.getText().trim());
            u.setRol(cbRol.getSelectedItem().toString());

            if (usuarioDAO.insertarUsuario(u)) {
                JOptionPane.showMessageDialog(this, "Usuario agregado correctamente.");
                buscarUsuarios();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo agregar el usuario.");
            }
        }
    }

    private void modificarUsuario() {

        int fila = tabla.getSelectedRow();

        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario.");
            return;
        }

        int idUsuario = (int) modelo.getValueAt(fila, 0);

        Usuario u = usuarioDAO.buscarPorId(idUsuario);

        if (u == null) {
            JOptionPane.showMessageDialog(this, "No se encontró el usuario.");
            return;
        }

        JTextField txtNombre = new JTextField(u.getNombre());
        JTextField txtApellido = new JTextField(u.getApellido());
        JTextField txtDni = new JTextField(u.getDni());
        JTextField txtCorreo = new JTextField(u.getCorreo());
        JTextField txtPassword = new JTextField(u.getPassword());

        JComboBox<String> cbRol = new JComboBox<>(
                new String[]{"admin", "trabajador"}
        );

        cbRol.setSelectedItem(u.getRol());

        Object[] campos = {
            "Nombre:", txtNombre,
            "Apellido:", txtApellido,
            "DNI:", txtDni,
            "Correo:", txtCorreo,
            "Contraseña:", txtPassword,
            "Rol:", cbRol
        };

        int op = JOptionPane.showConfirmDialog(
                this,
                campos,
                "Modificar Usuario",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (op == JOptionPane.OK_OPTION) {

            String rolAnterior = u.getRol();
            String nuevoRol = cbRol.getSelectedItem().toString();

            // Confirmación especial si cambia de trabajador a admin
            if (rolAnterior.equals("trabajador")
                    && nuevoRol.equals("admin")) {

                int confirmacion = JOptionPane.showConfirmDialog(
                        this,
                        "¿Está seguro de cambiar este usuario a ADMIN?\n"
                        + "Tendrá acceso completo al sistema.",
                        "Confirmar cambio de rol",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

                if (confirmacion != JOptionPane.YES_OPTION) {
                    return;
                }
            }

            u.setNombre(txtNombre.getText().trim());
            u.setApellido(txtApellido.getText().trim());
            u.setDni(txtDni.getText().trim());
            u.setCorreo(txtCorreo.getText().trim());
            u.setPassword(txtPassword.getText().trim());
            u.setRol(nuevoRol);

            if (usuarioDAO.actualizarUsuario(u)) {

                JOptionPane.showMessageDialog(
                        this,
                        "Usuario actualizado correctamente."
                );

                buscarUsuarios();

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "No se pudo actualizar el usuario."
                );
            }
        }
    }

    private void eliminarUsuario() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario.");
            return;
        }

        int idUsuario = (int) modelo.getValueAt(fila, 0);

        int op = JOptionPane.showConfirmDialog(this,
                "¿Eliminar el usuario seleccionado?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION);

        if (op == JOptionPane.YES_OPTION) {
            if (usuarioDAO.eliminarUsuario(idUsuario)) {
                JOptionPane.showMessageDialog(this, "Usuario eliminado.");
                buscarUsuarios();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo eliminar el usuario.");
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

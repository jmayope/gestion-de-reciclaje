package vista;

import dao.UserDAO;
import dao.EntityDAO;
import dao.PeopleDAO;
import dao.RoleDAO;
import dao.EntityUserDAO;
import dao.UserRoleDAO;

import modelo.User;
import modelo.Entity;
import modelo.People;
import modelo.Role;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Stack;
import util.AuditoriaManager;
import util.EmailSender;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class PanelUsers extends JPanel {

    private JTable tabla;
    private DefaultTableModel modelo;

    private final UserDAO userDAO = new UserDAO();
    private final EntityDAO entityDAO = new EntityDAO();
    private final PeopleDAO peopleDAO = new PeopleDAO();
    private final RoleDAO roleDAO = new RoleDAO();
    private final EntityUserDAO entityUserDAO = new EntityUserDAO();
    private final UserRoleDAO userRoleDAO = new UserRoleDAO();

    private JTextField txtBuscar;
    private JComboBox<String> cbBuscarPor;

    private final Stack<User> pilaEliminados = new Stack<>();

    public PanelUsers() {

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

        // ── PANEL SUPERIOR ───────────────────────────────────────────────
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBackground(new Color(245, 247, 250));

        JLabel titulo = new JLabel("Gestión de Usuarios - Empresas", SwingConstants.LEFT);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 30));
        titulo.setForeground(new Color(30, 30, 30));

        panelSuperior.add(titulo, BorderLayout.NORTH);

        // ── BÚSQUEDA ─────────────────────────────────────────────────────
        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        panelBusqueda.setBackground(new Color(245, 247, 250));

        cbBuscarPor = new JComboBox<>(new String[]{
                "Código",
                "Usuario",
                "Rol",
                "Estado",
                "Empresa",
                "Persona"
        });

        cbBuscarPor.setPreferredSize(new Dimension(170, 38));
        cbBuscarPor.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        txtBuscar = new JTextField();
        txtBuscar.setPreferredSize(new Dimension(280, 38));
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JButton btnBuscar = new JButton("Buscar");
        JButton btnMostrarTodos = new JButton("Mostrar todos");

        btnBuscar.setBackground(new Color(25, 118, 210));
        btnBuscar.setForeground(Color.WHITE);

        btnMostrarTodos.setBackground(new Color(97, 97, 97));
        btnMostrarTodos.setForeground(Color.WHITE);

        btnBuscar.addActionListener(e -> buscarUsuarios());
        btnMostrarTodos.addActionListener(e -> cargarUsuarios());

        panelBusqueda.add(new JLabel("Buscar:"));
        panelBusqueda.add(cbBuscarPor);
        panelBusqueda.add(txtBuscar);
        panelBusqueda.add(btnBuscar);
        panelBusqueda.add(btnMostrarTodos);

        panelSuperior.add(panelBusqueda, BorderLayout.SOUTH);

        panelPrincipal.add(panelSuperior, BorderLayout.NORTH);

        // ── TABLA ────────────────────────────────────────────────────────
        modelo = new DefaultTableModel(
                new Object[]{"ID", "Código", "Empresa", "Persona", "Usuario", "Rol", "Estado"}, 0
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
                new EmptyBorder(15, 15, 15, 15)
        ));
        cardTabla.add(scroll, BorderLayout.CENTER);

        panelPrincipal.add(cardTabla, BorderLayout.CENTER);

        // ── BOTONES ──────────────────────────────────────────────────────
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15));
        panelBotones.setBackground(new Color(245, 247, 250));

        JButton btnAgregar = crearBoton("Agregar", new Color(46, 125, 50));
        JButton btnModificar = crearBoton("Modificar", new Color(25, 118, 210));
        JButton btnEliminar = crearBoton("Eliminar", new Color(198, 40, 40));
        JButton btnEstado = crearBoton("Activar/Desactivar", new Color(255, 143, 0));
        JButton btnDeshacer = crearBoton("Deshacer", new Color(97, 97, 97));

        btnAgregar.addActionListener(e -> agregarUsuario());
        btnModificar.addActionListener(e -> modificarUsuario());
        btnEliminar.addActionListener(e -> eliminarUsuario());
        btnEstado.addActionListener(e -> cambiarEstadoUsuario());
        btnDeshacer.addActionListener(e -> deshacerEliminacion());

        panelBotones.add(btnAgregar);
        panelBotones.add(btnModificar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnEstado);
        panelBotones.add(btnDeshacer);

        panelPrincipal.add(panelBotones, BorderLayout.SOUTH);
    }

    private void cargarUsuarios() {

        modelo.setRowCount(0);

        List<User> lista = userDAO.listUsers();

        for (User u : lista) {

            modelo.addRow(new Object[]{
                u.getId(),
                u.getCode(),
                u.getEntityName(),
                u.getPersonName(),
                u.getUsername(),
                u.isIsPrincipal()
                ? "PRINCIPAL"
                : "SECUNDARIO",
                u.isStatus()
                ? "ACTIVO"
                : "INACTIVO"
            });
        }
    }

    private void buscarUsuarios() {

        String criterio
                = cbBuscarPor.getSelectedItem().toString();

        String texto
                = txtBuscar.getText().trim().toLowerCase();

        modelo.setRowCount(0);

        for (User u : userDAO.listUsers()) {

            Entity entidad = null;
            People persona = null;

            boolean coincide = false;

            switch (criterio) {

                case "ID" ->
                    coincide
                            = String.valueOf(u.getId())
                                    .contains(texto);

                case "Código" ->
                    coincide
                            = u.getCode() != null
                            && u.getCode()
                                    .toLowerCase()
                                    .contains(texto);

                case "Usuario" ->
                    coincide
                            = u.getUsername() != null
                            && u.getUsername()
                                    .toLowerCase()
                                    .contains(texto);

                case "Rol" ->
                    coincide
                            = (u.isIsPrincipal()
                                    ? "principal"
                                    : "secundario")
                                    .contains(texto);

                case "Estado" ->
                    coincide
                            = (u.isStatus()
                                    ? "activo"
                                    : "inactivo")
                                    .equals(texto);
                case "Empresa" ->
                    coincide = entidad != null
                            && entidad.getName() != null
                            && entidad.getName()
                                    .toLowerCase()
                                    .contains(texto);

                case "Persona" ->
                    coincide = persona != null
                            && persona.getFullName() != null
                            && persona.getFullName()
                                    .toLowerCase()
                                    .contains(texto);
            }

            if (coincide) {

                if (u.getEntityId() != null) {
                    entidad = entityDAO.findById(u.getEntityId());
                }

                if (u.getPersonId() != null) {
                    persona = peopleDAO.findById(u.getPersonId());
                }

                modelo.addRow(new Object[]{
                    u.getId(),
                    u.getCode(),
                    entidad != null
                    ? entidad.getName()
                    : "Sin empresa",
                    persona != null
                    ? persona.getFullName()
                    : "Sin persona",
                    u.getUsername(),
                    u.isIsPrincipal()
                    ? "Principal"
                    : "Secundario",
                    u.isStatus()
                    ? "Activo"
                    : "Inactivo"
                });
            }
        }
    }

    private void agregarUsuario() {

        JComboBox<Entity> cbEntity = new JComboBox<>();
        JComboBox<People> cbPeople = new JComboBox<>();

        for (Entity e : entityDAO.listEntities()) {
            cbEntity.addItem(e);
        }

        for (People p : peopleDAO.listPeople()) {
            cbPeople.addItem(p);
        }

        JTextField txtUsername = new JTextField();
        JPasswordField txtPassword = new JPasswordField();
        JPasswordField txtConfirmPassword = new JPasswordField();

        JComboBox<String> cbRol
                = new JComboBox<>(new String[]{"Principal", "Secundario"});

        JCheckBox chkEnviarCorreo
                = new JCheckBox("Se enviarán las credenciales al correo");

        Object[] campos = {
            "Empresa:", cbEntity,
            "Persona:", cbPeople,
            "Usuario:", txtUsername,
            "Contraseña:", txtPassword,
            "Confirmar contraseña:", txtConfirmPassword,
            "Rol:", cbRol,
            chkEnviarCorreo
        };

        int op = JOptionPane.showConfirmDialog(
                this,
                campos,
                "Agregar Usuario",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (op == JOptionPane.OK_OPTION) {

            Entity entidad
                    = (Entity) cbEntity.getSelectedItem();

            People persona
                    = (People) cbPeople.getSelectedItem();

            String password
                    = new String(txtPassword.getPassword());

            String confirmar
                    = new String(txtConfirmPassword.getPassword());

            if (entidad == null || persona == null) {

                JOptionPane.showMessageDialog(
                        this,
                        "Debe seleccionar empresa y persona."
                );

                return;
            }

            if (password.trim().isEmpty()) {

                JOptionPane.showMessageDialog(
                        this,
                        "La contraseña no puede estar vacía."
                );

                return;
            }

            if (!password.equals(confirmar)) {

                JOptionPane.showMessageDialog(
                        this,
                        "Las contraseñas no coinciden."
                );

                return;
            }

            String rolSeleccionado
                    = cbRol.getSelectedItem().toString();

            User u = new User();

            u.setEntityId(entidad.getId());
            u.setPersonId(persona.getId());

            u.setUsername(txtUsername.getText());
            u.setPassword(password);
            u.setIsPrincipal("Principal".equals(rolSeleccionado));

            if (u.isIsPrincipal()) {

                if (userDAO.existePrincipalPorEmpresa(
                        entidad.getId())) {

                    JOptionPane.showMessageDialog(
                            this,
                            "La empresa ya tiene un usuario principal."
                    );

                    return;
                }
            }

            u.setStatus(true);

            if (userDAO.insertUser(u)) {

                // Vínculo empresa-usuario
                entityUserDAO.insertEntityUser(
                        entidad.getId(),
                        u.getId(),
                        true
                );

                // Vínculo usuario-rol
                Role rol = roleDAO.findByName(rolSeleccionado);

                if (rol != null) {

                    userRoleDAO.insertUserRole(
                            u.getId(),
                            rol.getId(),
                            entidad.getId(),
                            true
                    );
                }

                AuditoriaManager.registrar(
                        "Usuario agregado: "
                        + u.getUsername()
                );

                // Envío de credenciales por correo
                if (chkEnviarCorreo.isSelected()) {

                    boolean enviado = EmailSender.enviarCredenciales(
                            persona.getEmail(),
                            persona.getFullName(),
                            u.getUsername(),
                            password
                    );

                    if (!enviado) {

                        JOptionPane.showMessageDialog(
                                this,
                                "Usuario agregado, pero no se pudo enviar el correo con las credenciales."
                        );
                    }
                }

                JOptionPane.showMessageDialog(
                        this,
                        "Usuario agregado correctamente."
                );

                cargarUsuarios();

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "No se pudo agregar."
                );
            }
        }
    }

    private void modificarUsuario() {

        int fila = tabla.getSelectedRow();

        if (fila == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Seleccione un usuario."
            );

            return;
        }

        long id = (long) modelo.getValueAt(fila, 0);

        User u = userDAO.findById(id);

        if (u == null) {
            return;
        }

        JComboBox<Entity> cbEntity = new JComboBox<>();
        JComboBox<People> cbPeople = new JComboBox<>();

        for (Entity e : entityDAO.listEntities()) {

            cbEntity.addItem(e);

            if (u.getEntityId() != null
                    && e.getId() == u.getEntityId()) {

                cbEntity.setSelectedItem(e);
            }
        }

        for (People p : peopleDAO.listPeople()) {

            cbPeople.addItem(p);

            if (u.getPersonId() != null
                    && p.getId() == u.getPersonId()) {

                cbPeople.setSelectedItem(p);
            }
        }

        JTextField txtUsername
                = new JTextField(u.getUsername());

        JPasswordField txtPassword
                = new JPasswordField(u.getPassword());

        JComboBox<String> cbRol
                = new JComboBox<>(new String[]{"Principal", "Secundario"});

        cbRol.setSelectedItem(
                u.isIsPrincipal() ? "Principal" : "Secundario"
        );

        Object[] campos = {
            "Empresa:", cbEntity,
            "Persona:", cbPeople,
            "Usuario:", txtUsername,
            "Contraseña:", txtPassword,
            "Rol:", cbRol,};

        int op = JOptionPane.showConfirmDialog(
                this,
                campos,
                "Modificar Usuario",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (op == JOptionPane.OK_OPTION) {

            Entity entidad
                    = (Entity) cbEntity.getSelectedItem();

            People persona
                    = (People) cbPeople.getSelectedItem();

            u.setEntityId(entidad.getId());
            u.setPersonId(persona.getId());

            u.setUsername(txtUsername.getText());
            u.setPassword(
                    new String(txtPassword.getPassword())
            );

            u.setIsPrincipal(
                    "Principal".equals(cbRol.getSelectedItem().toString())
            );

            if (u.isIsPrincipal()) {

                if (userDAO.existeOtroPrincipal(
                        u.getEntityId(),
                        u.getId())) {

                    JOptionPane.showMessageDialog(
                            this,
                            "Ya existe otro usuario principal para esta empresa."
                    );

                    return;
                }
            }

            if (userDAO.updateUser(u)) {

                AuditoriaManager.registrar(
                        "Usuario modificado: "
                        + u.getUsername()
                );

                JOptionPane.showMessageDialog(
                        this,
                        "Usuario actualizado."
                );

                cargarUsuarios();

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "No se pudo actualizar."
                );
            }
        }
    }

    private void cambiarEstadoUsuario() {

        int fila = tabla.getSelectedRow();

        if (fila == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Seleccione un usuario."
            );

            return;
        }

        long id = (long) modelo.getValueAt(fila, 0);

        User u = userDAO.findById(id);

        if (u == null) {
            return;
        }

        String accion = u.isStatus()
                ? "desactivar"
                : "activar";

        int op = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro de " + accion + " este usuario?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION
        );

        if (op == JOptionPane.YES_OPTION) {

            u.setStatus(!u.isStatus());

            if (userDAO.updateUser(u)) {

                JOptionPane.showMessageDialog(
                        this,
                        "Estado actualizado correctamente."
                );

                cargarUsuarios();

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "No se pudo actualizar el estado."
                );
            }
        }
    }

    private void eliminarUsuario() {

        int fila = tabla.getSelectedRow();

        if (fila == -1) {

            JOptionPane.showMessageDialog(this,
                    "Seleccione un usuario.");

            return;
        }

        long id = (long) modelo.getValueAt(fila, 0);

        int op = JOptionPane.showConfirmDialog(
                this,
                "¿Eliminar usuario?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION
        );

        if (op == JOptionPane.YES_OPTION) {

            User usuarioEliminado = userDAO.findById(id);

            if (usuarioEliminado != null) {

                pilaEliminados.push(usuarioEliminado);

                AuditoriaManager.registrar(
                        "Usuario eliminado: "
                        + usuarioEliminado.getUsername()
                );
            }

            if (userDAO.deleteUser(id)) {

                JOptionPane.showMessageDialog(
                        this,
                        "Usuario eliminado."
                );

                cargarUsuarios();

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "No se pudo eliminar."
                );
            }
        }
    }

    private void deshacerEliminacion() {

        if (pilaEliminados.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "No hay usuarios eliminados para recuperar."
            );

            return;
        }

        User u = pilaEliminados.pop();

        if (userDAO.insertUser(u)) {

            JOptionPane.showMessageDialog(
                    this,
                    "Usuario recuperado correctamente."
            );

            cargarUsuarios();

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "No se pudo recuperar el usuario."
            );
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
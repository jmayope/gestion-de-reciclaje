package vista;

import dao.DevicesDAO;
import dao.EntityDAO;
import dao.UserDAO;

import modelo.Devices;
import modelo.Entity;
import modelo.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.util.List;
import java.util.Stack;

public class PanelDevices extends JPanel {

    //==========================
    // COMPONENTES
    //==========================
    private JTable tabla;
    private DefaultTableModel modelo;

    private JTextField txtBuscar;
    private JComboBox<String> cbBuscarPor;

    //==========================
    // DAO
    //==========================
    private final DevicesDAO devicesDAO = new DevicesDAO();
    private final UserDAO userDAO = new UserDAO();
    private final EntityDAO entityDAO = new EntityDAO();

    //==========================
    // PILA
    //==========================
    private final Stack<Devices> pilaEliminados = new Stack<>();

    private static class FormularioDevice {

        JTextField txtMac;
        JTextField txtSistema;

        JComboBox<User> cbUsuario;
        JComboBox<Entity> cbEmpresa;

        JCheckBox chkEstado;

    }

    public PanelDevices() {

        initComponents();
        cargarDispositivos();

    }

    private void initComponents() {

        setLayout(new BorderLayout(10, 10));

        setBorder(
                BorderFactory.createEmptyBorder(
                        15,
                        15,
                        15,
                        15));

        add(crearPanelBusqueda(), BorderLayout.NORTH);

        add(crearTabla(), BorderLayout.CENTER);

        add(crearPanelBotones(), BorderLayout.SOUTH);

    }

    private JPanel crearPanelBusqueda() {

        JPanel panel = new JPanel(
                new BorderLayout(10, 10));

        JLabel titulo
                = new JLabel("Gestión de Dispositivos");

        titulo.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        24));

        panel.add(
                titulo,
                BorderLayout.NORTH);

        JPanel filtros
                = new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT));

        filtros.add(
                new JLabel("Buscar por:"));

        cbBuscarPor
                = new JComboBox<>(new String[]{
            "MAC Address",
            "Sistema Operativo",
            "Usuario",
            "Empresa",
            "Estado"
        });

        filtros.add(cbBuscarPor);

        txtBuscar
                = new JTextField(20);

        filtros.add(txtBuscar);

        JButton btnBuscar
                = crearBoton(
                        "Buscar",
                        new Color(33, 150, 243));

        btnBuscar.addActionListener(
                e -> buscarDispositivos());

        filtros.add(btnBuscar);

        panel.add(
                filtros,
                BorderLayout.SOUTH);

        return panel;

    }

    private JScrollPane crearTabla() {

        modelo = new DefaultTableModel(
                new Object[]{
                    "ID",
                    "MAC Address",
                    "Sistema Operativo",
                    "Usuario",
                    "Empresa",
                    "Estado"

                },
                0
        ) {

            @Override
            public boolean isCellEditable(
                    int row,
                    int column) {

                return false;

            }

        };

        tabla = new JTable(modelo);

        tabla.setRowHeight(28);

        tabla.getTableHeader().setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        13));

        tabla.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);

        return new JScrollPane(tabla);

    }

    private JPanel crearPanelBotones() {

        JPanel panel
                = new JPanel(
                        new FlowLayout(
                                FlowLayout.CENTER,
                                10,
                                5));

        JButton btnAgregar
                = crearBoton(
                        "Agregar",
                        new Color(76, 175, 80));

        JButton btnModificar
                = crearBoton(
                        "Modificar",
                        new Color(33, 150, 243));

        JButton btnEstado
                = crearBoton(
                        "Estado",
                        new Color(255, 152, 0));

        JButton btnEliminar
                = crearBoton(
                        "Eliminar",
                        new Color(244, 67, 54));

        JButton btnDeshacer
                = crearBoton(
                        "Deshacer",
                        new Color(121, 85, 72));

        btnAgregar.addActionListener(
                e -> agregarDispositivo());

        btnModificar.addActionListener(
                e -> modificarDispositivo());

        btnEstado.addActionListener(
                e -> cambiarEstadoDispositivo());

        btnEliminar.addActionListener(
                e -> eliminarDispositivo());

        btnDeshacer.addActionListener(
                e -> deshacerEliminacion());

        panel.add(btnAgregar);
        panel.add(btnModificar);
        panel.add(btnEstado);
        panel.add(btnEliminar);
        panel.add(btnDeshacer);

        return panel;

    }

    private JButton crearBoton(
            String texto,
            Color color) {

        JButton btn
                = new JButton(texto);

        btn.setPreferredSize(
                new Dimension(
                        130,
                        40));

        btn.setBackground(color);

        btn.setForeground(
                Color.WHITE);

        btn.setFocusPainted(false);

        btn.setBorderPainted(false);

        btn.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        14));

        btn.setCursor(
                new Cursor(
                        Cursor.HAND_CURSOR));

        return btn;

    }

    private void cargarDispositivos() {

        modelo.setRowCount(0);

        List<Devices> lista = devicesDAO.listDevices();

        if (lista.isEmpty()) {
            return;
        }

        for (Devices d : lista) {

            modelo.addRow(new Object[]{
                d.getId(),
                d.getMacaddress(),
                d.getOperativeSystem(),
                d.getUserName(),
                d.getEntityName(),
                d.isStatus() ? "ACTIVO" : "INACTIVO"
            });

        }

    }

    private void buscarDispositivos() {

        String criterio = cbBuscarPor.getSelectedItem().toString();

        String texto = txtBuscar.getText().trim().toLowerCase();

        modelo.setRowCount(0);

        List<Devices> lista = devicesDAO.listDevices();

        for (Devices d : lista) {

            boolean coincide = false;

            switch (criterio) {

                case "MAC Address" ->
                    coincide = d.getMacaddress() != null
                            && d.getMacaddress()
                                    .toLowerCase()
                                    .contains(texto);

                case "Sistema Operativo" ->
                    coincide = d.getOperativeSystem() != null
                            && d.getOperativeSystem()
                                    .toLowerCase()
                                    .contains(texto);

                case "Usuario" ->
                    coincide = d.getUserName() != null
                            && d.getUserName()
                                    .toLowerCase()
                                    .contains(texto);

                case "Empresa" ->
                    coincide = d.getEntityName() != null
                            && d.getEntityName()
                                    .toLowerCase()
                                    .contains(texto);

                case "Estado" ->
                    coincide = (d.isStatus()
                            ? "ACTIVO"
                            : "INACTIVO")
                            .contains(texto);

            }

            if (coincide) {

                modelo.addRow(new Object[]{
                    d.getId(),
                    d.getMacaddress(),
                    d.getOperativeSystem(),
                    d.getUserName(),
                    d.getEntityName(),
                    d.isStatus() ? "ACTIVO" : "INACTIVO"
                });

            }

        }

    }

    private FormularioDevice crearFormulario() {

        FormularioDevice f = new FormularioDevice();

        f.txtMac = new JTextField(20);

        f.txtSistema = new JTextField(20);

        f.cbUsuario = new JComboBox<>();

        f.cbEmpresa = new JComboBox<>();

        f.chkEstado = new JCheckBox("Activo");

        for (User u : userDAO.listUsers()) {

            f.cbUsuario.addItem(u);

        }

        for (Entity e : entityDAO.listEntities()) {

            f.cbEmpresa.addItem(e);

        }

        return f;

    }

    private JPanel construirFormulario(FormularioDevice f) {

        JPanel panel = new JPanel(new GridLayout(5, 2, 8, 8));

        panel.add(new JLabel("MAC Address"));
        panel.add(f.txtMac);

        panel.add(new JLabel("Sistema Operativo"));
        panel.add(f.txtSistema);

        panel.add(new JLabel("Usuario"));
        panel.add(f.cbUsuario);

        panel.add(new JLabel("Empresa"));
        panel.add(f.cbEmpresa);

        panel.add(new JLabel("Estado"));
        panel.add(f.chkEstado);

        return panel;

    }

    private void agregarDispositivo() {

        FormularioDevice f = crearFormulario();

        int op = JOptionPane.showConfirmDialog(
                this,
                construirFormulario(f),
                "Agregar Dispositivo",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (op != JOptionPane.OK_OPTION) {
            return;
        }

        if (f.txtMac.getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Ingrese la MAC.");

            return;

        }

        if (devicesDAO.existeMac(
                f.txtMac.getText().trim())) {

            JOptionPane.showMessageDialog(
                    this,
                    "La MAC ya existe.");

            return;

        }

        Devices d = new Devices();

        d.setMacaddress(
                f.txtMac.getText().trim());

        d.setOperativeSystem(
                f.txtSistema.getText().trim());

        User usuario
                = (User) f.cbUsuario.getSelectedItem();

        Entity empresa
                = (Entity) f.cbEmpresa.getSelectedItem();

        d.setUserId(usuario.getId());

        d.setEntityId(empresa.getId());

        d.setStatus(
                f.chkEstado.isSelected());

        if (devicesDAO.insertDevice(d)) {

            cargarDispositivos();

            JOptionPane.showMessageDialog(
                    this,
                    "Dispositivo agregado correctamente.");

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "No se pudo agregar.");

        }

    }

    private void modificarDispositivo() {

        int fila = tabla.getSelectedRow();

        if (fila == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Seleccione un dispositivo.");

            return;

        }

        long id
                = (long) modelo.getValueAt(fila, 0);

        Devices d
                = devicesDAO.findById(id);

        if (d == null) {
            return;
        }

        FormularioDevice f
                = crearFormulario();

        f.txtMac.setText(
                d.getMacaddress());

        f.txtSistema.setText(
                d.getOperativeSystem());

        f.chkEstado.setSelected(
                d.isStatus());

        for (int i = 0; i < f.cbUsuario.getItemCount(); i++) {

            if (f.cbUsuario.getItemAt(i).getId() == d.getUserId()) {

                f.cbUsuario.setSelectedIndex(i);

                break;

            }

        }

        for (int i = 0; i < f.cbEmpresa.getItemCount(); i++) {

            if (f.cbEmpresa.getItemAt(i).getId() == d.getEntityId()) {

                f.cbEmpresa.setSelectedIndex(i);

                break;

            }

        }

        int op = JOptionPane.showConfirmDialog(
                this,
                construirFormulario(f),
                "Modificar Dispositivo",
                JOptionPane.OK_CANCEL_OPTION);

        if (op != JOptionPane.OK_OPTION) {
            return;
        }

        d.setMacaddress(
                f.txtMac.getText().trim());

        d.setOperativeSystem(
                f.txtSistema.getText().trim());

        d.setUserId(
                ((User) f.cbUsuario.getSelectedItem()).getId());

        d.setEntityId(
                ((Entity) f.cbEmpresa.getSelectedItem()).getId());

        d.setStatus(
                f.chkEstado.isSelected());

        if (devicesDAO.updateDevice(d)) {

            cargarDispositivos();

            JOptionPane.showMessageDialog(
                    this,
                    "Dispositivo actualizado.");

        }

    }

    private void eliminarDispositivo() {

        int fila = tabla.getSelectedRow();

        if (fila == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Seleccione un dispositivo.");

            return;

        }

        long id = (long) modelo.getValueAt(fila, 0);

        Devices d
                = devicesDAO.findById(id);

        if (d == null) {
            return;
        }

        int op = JOptionPane.showConfirmDialog(
                this,
                "¿Eliminar dispositivo?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION);

        if (op != JOptionPane.YES_OPTION) {
            return;
        }

        pilaEliminados.push(d);

        if (devicesDAO.deleteDevice(id)) {

            cargarDispositivos();

        }

    }

    private void cambiarEstadoDispositivo() {

        int fila = tabla.getSelectedRow();

        if (fila == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Seleccione un dispositivo.");

            return;

        }

        long id = (long) modelo.getValueAt(fila, 0);

        Devices d
                = devicesDAO.findById(id);

        if (d == null) {
            return;
        }

        d.setStatus(!d.isStatus());

        if (devicesDAO.updateDevice(d)) {

            cargarDispositivos();

        }

    }

    private void deshacerEliminacion() {

        if (pilaEliminados.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "No hay dispositivos para restaurar.");

            return;

        }

        Devices d = pilaEliminados.pop();

        if (devicesDAO.insertDevice(d)) {

            cargarDispositivos();

            JOptionPane.showMessageDialog(
                    this,
                    "Dispositivo restaurado correctamente.");

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "No se pudo restaurar el dispositivo.");

        }

    }
}

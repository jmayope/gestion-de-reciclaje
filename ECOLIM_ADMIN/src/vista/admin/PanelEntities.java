package vista.admin;

import dao.EntityDAO;
import modelo.Entity;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class PanelEntities extends JPanel {

    private JTable tabla;
    private DefaultTableModel modelo;

    private final EntityDAO entityDAO = new EntityDAO();

    public PanelEntities() {

        initComponents();
        cargarEntidades();
    }

    private void initComponents() {

        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));

        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(new Color(245, 247, 250));
        panelPrincipal.setBorder(new EmptyBorder(20, 20, 20, 20));

        add(panelPrincipal, BorderLayout.CENTER);

        // ── Encabezado ─────────────────────────────────────────────
        JPanel panelSuperior = new JPanel();
        panelSuperior.setLayout(new BoxLayout(panelSuperior, BoxLayout.Y_AXIS));
        panelSuperior.setBackground(new Color(245, 247, 250));

        JLabel titulo = new JLabel("Gestión de Empresas");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 30));
        titulo.setForeground(new Color(30, 30, 30));

        JLabel subtitulo = new JLabel("Administración de empresas registradas");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitulo.setForeground(Color.GRAY);

        panelSuperior.add(titulo);
        panelSuperior.add(Box.createVerticalStrut(4));
        panelSuperior.add(subtitulo);
        panelSuperior.add(Box.createVerticalStrut(20));

        panelPrincipal.add(panelSuperior, BorderLayout.NORTH);

        // ── Modelo Tabla ──────────────────────────────────────────
        modelo = new DefaultTableModel(
                new Object[]{
                    "ID",
                    "Código",
                    "Nombre",
                    "Dirección",
                    "Teléfono",
                    "Tipo",
                    "Estado"
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
                new Font("Segoe UI", Font.BOLD, 13)
        );

        tabla.getTableHeader().setBackground(
                new Color(18, 33, 61)
        );

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
                                true
                        ),
                        new EmptyBorder(15, 15, 15, 15)
                )
        );

        cardTabla.add(scroll, BorderLayout.CENTER);

        panelPrincipal.add(cardTabla, BorderLayout.CENTER);

        // ── Botones ───────────────────────────────────────────────
        JPanel panelBotones = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 10, 15)
        );

        panelBotones.setBackground(
                new Color(245, 247, 250)
        );

        JButton btnAgregar = crearBoton(
                "Agregar",
                new Color(46, 125, 50)
        );

        JButton btnModificar = crearBoton(
                "Modificar",
                new Color(25, 118, 210)
        );

        JButton btnEliminar = crearBoton(
                "Eliminar",
                new Color(198, 40, 40)
        );

        btnAgregar.addActionListener(e -> agregarEntidad());
        btnModificar.addActionListener(e -> modificarEntidad());
        btnEliminar.addActionListener(e -> eliminarEntidad());

        panelBotones.add(btnAgregar);
        panelBotones.add(btnModificar);
        panelBotones.add(btnEliminar);

        panelPrincipal.add(panelBotones, BorderLayout.SOUTH);
    }

    private void cargarEntidades() {

        modelo.setRowCount(0);

        List<Entity> lista = entityDAO.listEntities();

        for (Entity e : lista) {

            modelo.addRow(new Object[]{
                e.getId(),
                e.getCode(),
                e.getName(),
                e.getAddress(),
                e.getPhone(),
                e.getType(),
                e.isStatus() ? "Activo" : "Inactivo"
            });
        }
    }

    private void agregarEntidad() {

        JTextField txtCode = new JTextField();
        JTextField txtName = new JTextField();
        JTextField txtAddress = new JTextField();
        JTextField txtPhone = new JTextField();

        JComboBox<String> cbType
                = new JComboBox<>(
                        new String[]{
                            "generadora",
                            "operadora"
                        }
                );

        JCheckBox chkStatus
                = new JCheckBox("Activo");

        Object[] campos = {
            "Código:", txtCode,
            "Nombre:", txtName,
            "Dirección:", txtAddress,
            "Teléfono:", txtPhone,
            "Tipo:", cbType,
            "Estado:", chkStatus
        };

        int op = JOptionPane.showConfirmDialog(
                this,
                campos,
                "Agregar Empresa",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (op == JOptionPane.OK_OPTION) {

            Entity e = new Entity();

            e.setCode(txtCode.getText().trim());
            e.setName(txtName.getText().trim());
            e.setAddress(txtAddress.getText().trim());
            e.setPhone(txtPhone.getText().trim());

            e.setType(
                    cbType.getSelectedItem().toString()
            );

            e.setStatus(
                    chkStatus.isSelected()
            );

            if (entityDAO.insertEntity(e)) {

                JOptionPane.showMessageDialog(
                        this,
                        "Empresa agregada correctamente."
                );

                cargarEntidades();

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "No se pudo agregar la empresa."
                );
            }
        }
    }

    private void modificarEntidad() {

        int fila = tabla.getSelectedRow();

        if (fila == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Seleccione una empresa."
            );

            return;
        }

        long id = (long) modelo.getValueAt(fila, 0);

        Entity e = entityDAO.findById(id);

        if (e == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "No se encontró la empresa."
            );

            return;
        }

        JTextField txtCode
                = new JTextField(e.getCode());

        JTextField txtName
                = new JTextField(e.getName());

        JTextField txtAddress
                = new JTextField(e.getAddress());

        JTextField txtPhone
                = new JTextField(e.getPhone());

        JComboBox<String> cbType
                = new JComboBox<>(
                        new String[]{
                            "generadora",
                            "operadora"
                        }
                );

        cbType.setSelectedItem(e.getType());

        JCheckBox chkStatus
                = new JCheckBox(
                        "Activo",
                        e.isStatus()
                );

        Object[] campos = {
            "Código:", txtCode,
            "Nombre:", txtName,
            "Dirección:", txtAddress,
            "Teléfono:", txtPhone,
            "Tipo:", cbType,
            "Estado:", chkStatus
        };

        int op = JOptionPane.showConfirmDialog(
                this,
                campos,
                "Modificar Empresa",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (op == JOptionPane.OK_OPTION) {

            e.setCode(txtCode.getText().trim());
            e.setName(txtName.getText().trim());
            e.setAddress(txtAddress.getText().trim());
            e.setPhone(txtPhone.getText().trim());

            e.setType(
                    cbType.getSelectedItem().toString()
            );

            e.setStatus(
                    chkStatus.isSelected()
            );

            if (entityDAO.updateEntity(e)) {

                JOptionPane.showMessageDialog(
                        this,
                        "Empresa actualizada correctamente."
                );

                cargarEntidades();

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "No se pudo actualizar la Empresa."
                );
            }
        }
    }

    private void eliminarEntidad() {

        int fila = tabla.getSelectedRow();

        if (fila == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Seleccione una empresa."
            );

            return;
        }

        long id = (long) modelo.getValueAt(fila, 0);

        int op = JOptionPane.showConfirmDialog(
                this,
                "¿Eliminar empresa seleccionada?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION
        );

        if (op == JOptionPane.YES_OPTION) {

            if (entityDAO.deleteEntity(id)) {

                JOptionPane.showMessageDialog(
                        this,
                        "Empresa eliminada correctamente."
                );

                cargarEntidades();

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "No se pudo eliminar la empresa."
                );
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

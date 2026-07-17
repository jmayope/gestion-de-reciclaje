package vista.admin;

import dao.PeopleDAO;
import modelo.People;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.util.List;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class PanelPeople extends JPanel {

    private JTable tabla;
    private DefaultTableModel modelo;

    private final PeopleDAO peopleDAO = new PeopleDAO();

    public PanelPeople() {

        initComponents();
        cargarPersonas();
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

        JLabel titulo = new JLabel("Gestión de Personas");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 30));
        titulo.setForeground(new Color(30, 30, 30));

        JLabel subtitulo = new JLabel("Administración y mantenimiento de personas");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitulo.setForeground(Color.GRAY);

        panelSuperior.add(titulo);
        panelSuperior.add(Box.createVerticalStrut(4));
        panelSuperior.add(subtitulo);
        panelSuperior.add(Box.createVerticalStrut(16));

        panelPrincipal.add(panelSuperior, BorderLayout.NORTH);

        // ── Tabla ────────────────────────────────────────────────────
        modelo = new DefaultTableModel(
                new Object[]{
                        "ID",
                        "Código",
                        "Nombre Completo",
                        "Dirección",
                        "Teléfono",
                        "Correo",
                        "Género",
                        "Nacimiento",
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
                        new LineBorder(new Color(225, 225, 225), 1, true),
                        new EmptyBorder(15, 15, 15, 15)
                )
        );

        cardTabla.add(scroll, BorderLayout.CENTER);

        panelPrincipal.add(cardTabla, BorderLayout.CENTER);

        // ── Botones ──────────────────────────────────────────────────
        JPanel panelBotones = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 10, 15)
        );

        panelBotones.setBackground(new Color(245, 247, 250));

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

        btnAgregar.addActionListener(e -> agregarPersona());
        btnModificar.addActionListener(e -> modificarPersona());
        btnEliminar.addActionListener(e -> eliminarPersona());

        panelBotones.add(btnAgregar);
        panelBotones.add(btnModificar);
        panelBotones.add(btnEliminar);

        panelPrincipal.add(panelBotones, BorderLayout.SOUTH);
    }

    private void cargarPersonas() {

        modelo.setRowCount(0);

        List<People> lista = peopleDAO.listPeople();

        for (People p : lista) {

            modelo.addRow(new Object[]{
                    p.getId(),
                    p.getCode(),
                    p.getFullName(),
                    p.getAddress(),
                    p.getPhone(),
                    p.getEmail(),
                    p.isGender() ? "Masculino" : "Femenino",
                    p.getBirthDate(),
                    p.isStatus() ? "Activo" : "Inactivo"
            });
        }
    }

    private void agregarPersona() {

        JTextField txtCode = new JTextField();
        JTextField txtFullName = new JTextField();
        JTextField txtAddress = new JTextField();
        JTextField txtPhone = new JTextField();
        JTextField txtEmail = new JTextField();
        JTextField txtBirthDate = new JTextField();

        JComboBox<String> cbGender =
                new JComboBox<>(
                        new String[]{
                                "Masculino",
                                "Femenino"
                        }
                );

        JCheckBox chkStatus =
                new JCheckBox("Activo");

        Object[] campos = {
                "Código:", txtCode,
                "Nombre Completo:", txtFullName,
                "Dirección:", txtAddress,
                "Teléfono:", txtPhone,
                "Correo:", txtEmail,
                "Género:", cbGender,
                "Fecha Nacimiento (YYYY-MM-DD):", txtBirthDate,
                "Estado:", chkStatus
        };

        int op = JOptionPane.showConfirmDialog(
                this,
                campos,
                "Agregar Persona",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (op == JOptionPane.OK_OPTION) {

            People p = new People();

            p.setCode(txtCode.getText().trim());
            p.setFullName(txtFullName.getText().trim());
            p.setAddress(txtAddress.getText().trim());
            p.setPhone(txtPhone.getText().trim());
            p.setEmail(txtEmail.getText().trim());

            p.setGender(
                    cbGender.getSelectedItem()
                            .toString()
                            .equals("Masculino")
            );

            p.setBirthDate(
                    Date.valueOf(txtBirthDate.getText().trim())
            );

            p.setStatus(
                    chkStatus.isSelected()
            );

            if (peopleDAO.insertPeople(p)) {

                JOptionPane.showMessageDialog(
                        this,
                        "Persona agregada correctamente."
                );

                cargarPersonas();

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "No se pudo agregar."
                );
            }
        }
    }

    private void modificarPersona() {

        int fila = tabla.getSelectedRow();

        if (fila == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Seleccione una persona."
            );

            return;
        }

        long id = (long) modelo.getValueAt(fila, 0);

        People p = peopleDAO.findById(id);

        if (p == null) {
            return;
        }

        JTextField txtCode =
                new JTextField(p.getCode());

        JTextField txtFullName =
                new JTextField(p.getFullName());

        JTextField txtAddress =
                new JTextField(p.getAddress());

        JTextField txtPhone =
                new JTextField(p.getPhone());

        JTextField txtEmail =
                new JTextField(p.getEmail());

        JTextField txtBirthDate =
                new JTextField(
                        p.getBirthDate().toString()
                );

        JComboBox<String> cbGender =
                new JComboBox<>(
                        new String[]{
                                "Masculino",
                                "Femenino"
                        }
                );

        cbGender.setSelectedItem(
                p.isGender()
                        ? "Masculino"
                        : "Femenino"
        );

        JCheckBox chkStatus =
                new JCheckBox(
                        "Activo",
                        p.isStatus()
                );

        Object[] campos = {
                "Código:", txtCode,
                "Nombre Completo:", txtFullName,
                "Dirección:", txtAddress,
                "Teléfono:", txtPhone,
                "Correo:", txtEmail,
                "Género:", cbGender,
                "Fecha Nacimiento (YYYY-MM-DD):", txtBirthDate,
                "Estado:", chkStatus
        };

        int op = JOptionPane.showConfirmDialog(
                this,
                campos,
                "Modificar Persona",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (op == JOptionPane.OK_OPTION) {

            p.setCode(txtCode.getText().trim());
            p.setFullName(txtFullName.getText().trim());
            p.setAddress(txtAddress.getText().trim());
            p.setPhone(txtPhone.getText().trim());
            p.setEmail(txtEmail.getText().trim());

            p.setGender(
                    cbGender.getSelectedItem()
                            .toString()
                            .equals("Masculino")
            );

            p.setBirthDate(
                    Date.valueOf(txtBirthDate.getText().trim())
            );

            p.setStatus(
                    chkStatus.isSelected()
            );

            if (peopleDAO.updatePeople(p)) {

                JOptionPane.showMessageDialog(
                        this,
                        "Persona actualizada."
                );

                cargarPersonas();

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "No se pudo actualizar."
                );
            }
        }
    }

    private void eliminarPersona() {

        int fila = tabla.getSelectedRow();

        if (fila == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Seleccione una persona."
            );

            return;
        }

        long id = (long) modelo.getValueAt(fila, 0);

        int op = JOptionPane.showConfirmDialog(
                this,
                "¿Eliminar persona?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION
        );

        if (op == JOptionPane.YES_OPTION) {

            if (peopleDAO.deletePeople(id)) {

                JOptionPane.showMessageDialog(
                        this,
                        "Persona eliminada."
                );

                cargarPersonas();

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "No se pudo eliminar."
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
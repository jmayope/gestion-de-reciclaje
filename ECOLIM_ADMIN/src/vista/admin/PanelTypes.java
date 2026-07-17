package vista.admin;

import dao.TypeDAO;
import modelo.Type;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class PanelTypes extends JPanel {

    private JTable tabla;
    private DefaultTableModel modelo;

    private final TypeDAO typeDAO =
            new TypeDAO();

    public PanelTypes() {

        initComponents();
        cargarTipos();
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

        JLabel titulo = new JLabel("Gestión de Tipos");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 30));
        titulo.setForeground(new Color(30, 30, 30));

        JLabel subtitulo = new JLabel("Administración de tipos de residuos");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitulo.setForeground(Color.GRAY);

        panelSuperior.add(titulo);
        panelSuperior.add(Box.createVerticalStrut(4));
        panelSuperior.add(subtitulo);
        panelSuperior.add(Box.createVerticalStrut(16));

        panelPrincipal.add(panelSuperior, BorderLayout.NORTH);

        // ── Tabla ─────────────────────────────────────────────────────
        modelo = new DefaultTableModel(
                new Object[]{
                        "ID",
                        "Categoría",
                        "Código",
                        "Nombre",
                        "Descripción",
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
                                true
                        ),
                        new EmptyBorder(15, 15, 15, 15)
                )
        );

        cardTabla.add(scroll, BorderLayout.CENTER);

        panelPrincipal.add(cardTabla, BorderLayout.CENTER);

        // ── Botones ───────────────────────────────────────────────────
        JPanel panelBotones = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 10, 15));

        panelBotones.setBackground(
                new Color(245, 247, 250));

        JButton btnAgregar = crearBoton(
                "Agregar",
                new Color(46, 125, 50));

        JButton btnModificar = crearBoton(
                "Modificar",
                new Color(25, 118, 210));

        JButton btnEliminar = crearBoton(
                "Eliminar",
                new Color(198, 40, 40));

        btnAgregar.addActionListener(e -> agregarTipo());
        btnModificar.addActionListener(e -> modificarTipo());
        btnEliminar.addActionListener(e -> eliminarTipo());

        panelBotones.add(btnAgregar);
        panelBotones.add(btnModificar);
        panelBotones.add(btnEliminar);

        panelPrincipal.add(panelBotones, BorderLayout.SOUTH);
    }

    private void cargarTipos() {

        modelo.setRowCount(0);

        List<Type> lista =
                typeDAO.listTypes();

        for (Type t : lista) {

            modelo.addRow(new Object[]{
                    t.getId(),
                    t.getCategory(),
                    t.getCode(),
                    t.getName(),
                    t.getDescripcion(),
                    t.isStatus()
                            ? "Activo"
                            : "Inactivo"
            });
        }
    }

    private void agregarTipo() {

        JTextField txtCategory =
                new JTextField();

        JTextField txtCode =
                new JTextField();

        JTextField txtName =
                new JTextField();

        JTextField txtDescripcion =
                new JTextField();

        JTextArea txtAdditional =
                new JTextArea(5, 20);

        JCheckBox chkStatus =
                new JCheckBox("Activo");

        Object[] campos = {
                "Categoría:", txtCategory,
                "Código:", txtCode,
                "Nombre:", txtName,
                "Descripción:", txtDescripcion,
                "Additional Fields JSON:",
                new JScrollPane(txtAdditional),
                "Estado:", chkStatus
        };

        int op = JOptionPane.showConfirmDialog(
                this,
                campos,
                "Agregar Tipo",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (op == JOptionPane.OK_OPTION) {

            Type t = new Type();

            t.setCategory(
                    txtCategory.getText().trim()
            );

            t.setCode(
                    txtCode.getText().trim()
            );

            t.setName(
                    txtName.getText().trim()
            );

            t.setDescripcion(
                    txtDescripcion.getText().trim()
            );

            t.setAdditionalFields(
                    txtAdditional.getText().trim()
            );

            t.setStatus(
                    chkStatus.isSelected()
            );

            if (typeDAO.insertType(t)) {

                JOptionPane.showMessageDialog(
                        this,
                        "Tipo agregado correctamente."
                );

                cargarTipos();

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "No se pudo agregar."
                );
            }
        }
    }

    private void modificarTipo() {

        int fila = tabla.getSelectedRow();

        if (fila == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Seleccione un tipo."
            );

            return;
        }

        long id = (long)
                modelo.getValueAt(fila, 0);

        Type t = typeDAO.findById(id);

        if (t == null) {
            return;
        }

        JTextField txtCategory =
                new JTextField(
                        t.getCategory()
                );

        JTextField txtCode =
                new JTextField(
                        t.getCode()
                );

        JTextField txtName =
                new JTextField(
                        t.getName()
                );

        JTextField txtDescripcion =
                new JTextField(
                        t.getDescripcion()
                );

        JTextArea txtAdditional =
                new JTextArea(
                        t.getAdditionalFields()
                );

        JCheckBox chkStatus =
                new JCheckBox(
                        "Activo",
                        t.isStatus()
                );

        Object[] campos = {
                "Categoría:", txtCategory,
                "Código:", txtCode,
                "Nombre:", txtName,
                "Descripción:", txtDescripcion,
                "Additional Fields JSON:",
                new JScrollPane(txtAdditional),
                "Estado:", chkStatus
        };

        int op = JOptionPane.showConfirmDialog(
                this,
                campos,
                "Modificar Tipo",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (op == JOptionPane.OK_OPTION) {

            t.setCategory(
                    txtCategory.getText().trim()
            );

            t.setCode(
                    txtCode.getText().trim()
            );

            t.setName(
                    txtName.getText().trim()
            );

            t.setDescripcion(
                    txtDescripcion.getText().trim()
            );

            t.setAdditionalFields(
                    txtAdditional.getText().trim()
            );

            t.setStatus(
                    chkStatus.isSelected()
            );

            if (typeDAO.updateType(t)) {

                JOptionPane.showMessageDialog(
                        this,
                        "Tipo actualizado."
                );

                cargarTipos();

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "No se pudo actualizar."
                );
            }
        }
    }

    private void eliminarTipo() {

        int fila = tabla.getSelectedRow();

        if (fila == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Seleccione un tipo."
            );

            return;
        }

        long id = (long)
                modelo.getValueAt(fila, 0);

        int op = JOptionPane.showConfirmDialog(
                this,
                "¿Eliminar tipo?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION
        );

        if (op == JOptionPane.YES_OPTION) {

            if (typeDAO.deleteType(id)) {

                JOptionPane.showMessageDialog(
                        this,
                        "Tipo eliminado."
                );

                cargarTipos();

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
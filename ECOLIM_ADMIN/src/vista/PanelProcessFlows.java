package vista;

import dao.ProcessFlowDAO;
import modelo.ProcessFlow;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class PanelProcessFlows extends JPanel {

    private JTable tabla;
    private DefaultTableModel modelo;

    private final ProcessFlowDAO processFlowDAO =
            new ProcessFlowDAO();

    public PanelProcessFlows() {

        initComponents();
        cargarFlujos();
    }

    private void initComponents() {

        setLayout(new BorderLayout());

        JLabel titulo = new JLabel(
                "Gestión de Flujos",
                SwingConstants.CENTER
        );

        titulo.setFont(new Font("Segoe UI", Font.BOLD, 28));

        titulo.setBorder(
                BorderFactory.createEmptyBorder(20, 0, 20, 0)
        );

        add(titulo, BorderLayout.NORTH);

        modelo = new DefaultTableModel(
                new Object[]{
                        "ID",
                        "Residuo",
                        "Proceso Anterior",
                        "Proceso Actual",
                        "Cantidad",
                        "Longitud",
                        "Latitud",
                        "Completado",
                        "Estado"
                }, 0
        ) {
            @Override
            public boolean isCellEditable(
                    int row,
                    int column
            ) {
                return false;
            }
        };

        tabla = new JTable(modelo);

        tabla.setRowHeight(25);

        add(new JScrollPane(tabla), BorderLayout.CENTER);

        JPanel panelBotones = new JPanel();

        JButton btnAgregar = new JButton("Agregar");
        JButton btnModificar = new JButton("Modificar");
        JButton btnEliminar = new JButton("Eliminar");

        btnAgregar.addActionListener(e -> agregarFlujo());
        btnModificar.addActionListener(e -> modificarFlujo());
        btnEliminar.addActionListener(e -> eliminarFlujo());

        panelBotones.add(btnAgregar);
        panelBotones.add(btnModificar);
        panelBotones.add(btnEliminar);

        add(panelBotones, BorderLayout.SOUTH);
    }

    private void cargarFlujos() {

        modelo.setRowCount(0);

        List<ProcessFlow> lista =
                processFlowDAO.listProcessFlows();

        for (ProcessFlow p : lista) {

            modelo.addRow(new Object[]{
                    p.getId(),
                    p.getWasteId(),
                    p.getPreviousProcessId(),
                    p.getCurrentProcessId(),
                    p.getQuantity(),
                    p.getLongitude(),
                    p.getLatitude(),
                    p.isCompleted() ? "Sí" : "No",
                    p.isStatus() ? "Activo" : "Inactivo"
            });
        }
    }

    private void agregarFlujo() {

        JTextField txtWasteId = new JTextField();
        JTextField txtPrevious = new JTextField();
        JTextField txtCurrent = new JTextField();
        JTextField txtQuantity = new JTextField();
        JTextField txtLongitude = new JTextField();
        JTextField txtLatitude = new JTextField();
        JTextField txtGenerator = new JTextField();
        JTextField txtOperator = new JTextField();

        JCheckBox chkCompleted =
                new JCheckBox("Completado");

        JCheckBox chkStatus =
                new JCheckBox("Activo");

        Object[] campos = {
                "Waste ID:", txtWasteId,
                "Proceso Anterior:", txtPrevious,
                "Proceso Actual:", txtCurrent,
                "Cantidad:", txtQuantity,
                "Longitud:", txtLongitude,
                "Latitud:", txtLatitude,
                "Entidad Generadora:", txtGenerator,
                "Entidad Operadora:", txtOperator,
                "Completado:", chkCompleted,
                "Estado:", chkStatus
        };

        int op = JOptionPane.showConfirmDialog(
                this,
                campos,
                "Agregar Flujo",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (op == JOptionPane.OK_OPTION) {

            ProcessFlow p = new ProcessFlow();

            p.setWasteId(
                    Long.valueOf(txtWasteId.getText())
            );

            p.setPreviousProcessId(
                    txtPrevious.getText().trim()
            );

            p.setCurrentProcessId(
                    txtCurrent.getText().trim()
            );

            p.setQuantity(
                    new BigDecimal(txtQuantity.getText())
            );

            p.setLongitude(
                    new BigDecimal(txtLongitude.getText())
            );

            p.setLatitude(
                    new BigDecimal(txtLatitude.getText())
            );

            p.setEntityGeneratorId(
                    Long.valueOf(txtGenerator.getText())
            );

            p.setEntityOperatorId(
                    Long.valueOf(txtOperator.getText())
            );

            p.setCompleted(
                    chkCompleted.isSelected()
            );

            p.setStatus(
                    chkStatus.isSelected()
            );

            if (processFlowDAO.insertProcessFlow(p)) {

                JOptionPane.showMessageDialog(
                        this,
                        "Flujo agregado correctamente."
                );

                cargarFlujos();

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "No se pudo agregar."
                );
            }
        }
    }

    private void modificarFlujo() {

        JOptionPane.showMessageDialog(
                this,
                "Puedes reutilizar la misma lógica de modificar."
        );
    }

    private void eliminarFlujo() {

        int fila = tabla.getSelectedRow();

        if (fila == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Seleccione un flujo."
            );

            return;
        }

        long id = (long) modelo.getValueAt(fila, 0);

        int op = JOptionPane.showConfirmDialog(
                this,
                "¿Eliminar flujo?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION
        );

        if (op == JOptionPane.YES_OPTION) {

            if (processFlowDAO.deleteProcessFlow(id)) {

                JOptionPane.showMessageDialog(
                        this,
                        "Flujo eliminado."
                );

                cargarFlujos();

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "No se pudo eliminar."
                );
            }
        }
    }
}
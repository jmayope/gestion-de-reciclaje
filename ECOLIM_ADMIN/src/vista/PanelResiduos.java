package vista;

import dao.WasteDAO;
import modelo.Usuario;
import modelo.Waste;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Stack;

/**
 * PanelResiduos — gestión CRUD de la tabla `wastes` (Supabase).
 *
 * A diferencia de la versión anterior, aquí NO se mantiene un cache en memoria:
 * cada operación (listar, agregar, modificar, eliminar, restaurar) llama
 * directamente a WasteDAO, que a su vez consulta o actualiza la base de datos
 * en el momento (mismo criterio que UserDAO).
 *
 * `listaMostrada` solo guarda lo que YA se pintó en pantalla, para poder
 * filtrar la búsqueda en memoria sin volver a golpear la BD en cada tecla; no
 * reemplaza a la base de datos como fuente de verdad.
 */
public class PanelResiduos extends JPanel {

    // ── Componentes UI ───────────────────────────────────────────────────────
    private JTable tabla;
    private DefaultTableModel modelo;
    private JTextField txtBuscar;
    private JComboBox<String> cbFiltro;
    private JLabel lblEstado;

    // ── DAO ──────────────────────────────────────────────────────────────────
    private final WasteDAO wasteDAO = new WasteDAO();

    // ── Estado ───────────────────────────────────────────────────────────────
    private final Usuario usuarioActivo;
    private List<Waste> listaMostrada;   // solo para filtrar en pantalla, no es cache de BD
    private final Stack<Waste> historialEliminados = new Stack<>();
    private final DateTimeFormatter formatoFecha
            = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final String[] ESTADOS_RESIDUO
            = {"Sólido", "Líquido", "Gaseoso", "Metálico", "Orgánico", "Reciclable"};

    /**
     * Envuelve code/name de `types` para mostrarse en un JComboBox y recuperar
     * el code al guardar.
     */
    private static class TipoItem {

        final String code;
        final String name;

        TipoItem(String code, String name) {
            this.code = code;
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private JComboBox<TipoItem> crearComboTipos() {
        JComboBox<TipoItem> combo = new JComboBox<>();
        for (String[] tipo : wasteDAO.listTiposResiduo()) {
            combo.addItem(new TipoItem(tipo[0], tipo[1]));
        }
        return combo;
    }

    private void seleccionarTipoPorCodigo(JComboBox<TipoItem> combo, String code) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (combo.getItemAt(i).code.equals(code)) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    //  CONSTRUCTOR
    // ════════════════════════════════════════════════════════════════════════
    public PanelResiduos(Usuario usuarioActivo) {
        this.usuarioActivo = usuarioActivo;
        initComponents();
        cargarDatosAsync();
    }

    // ════════════════════════════════════════════════════════════════════════
    //  CARGA ASÍNCRONA (consulta directa a BD vía WasteDAO.listWastes())
    // ════════════════════════════════════════════════════════════════════════
    private void cargarDatosAsync() {
        lblEstado.setText("Cargando datos…");
        setEnabled(false);

        new SwingWorker<List<Waste>, Void>() {
            @Override
            protected List<Waste> doInBackground() {
                return wasteDAO.listWastes();
            }

            @Override
            protected void done() {
                setEnabled(true);
                lblEstado.setText("");
                try {
                    listaMostrada = get();
                } catch (Exception e) {
                    listaMostrada = List.of();
                    lblEstado.setText("Error al cargar residuos.");
                }
                pintarTabla(listaMostrada);
            }
        }.execute();
    }

    // ════════════════════════════════════════════════════════════════════════
    //  INIT COMPONENTS
    // ════════════════════════════════════════════════════════════════════════
    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));

        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(new Color(245, 247, 250));
        panelPrincipal.setBorder(new EmptyBorder(20, 20, 20, 20));
        add(panelPrincipal, BorderLayout.CENTER);

        // ── Panel superior ───────────────────────────────────────────────────
        JPanel panelSuperior = new JPanel();
        panelSuperior.setLayout(new BoxLayout(panelSuperior, BoxLayout.Y_AXIS));
        panelSuperior.setBackground(new Color(245, 247, 250));

        JLabel titulo = new JLabel("Gestión de Residuos");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 30));
        titulo.setForeground(new Color(30, 30, 30));

        JLabel subtitulo = new JLabel("Administración de residuos registrados");
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

        // ── Barra de búsqueda ────────────────────────────────────────────────
        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        panelBusqueda.setBackground(new Color(245, 247, 250));

        cbFiltro = new JComboBox<>(new String[]{"ID", "Tipo", "Estado físico", "Unidad", "Entidad"});
        cbFiltro.setPreferredSize(new Dimension(170, 38));
        cbFiltro.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        txtBuscar = new JTextField();
        txtBuscar.setPreferredSize(new Dimension(280, 38));
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        panelBusqueda.add(new JLabel("Buscar:"));
        panelBusqueda.add(cbFiltro);
        panelBusqueda.add(txtBuscar);

        panelSuperior.add(panelBusqueda);
        panelPrincipal.add(panelSuperior, BorderLayout.NORTH);

        // ── Tabla ────────────────────────────────────────────────────────────
        modelo = new DefaultTableModel(
                new Object[]{"ID", "Tipo", "Cantidad", "Unidad", "Fecha generación",
                    "¿Almacenado?", "Estado físico", "Peligroso", "Entidad"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
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

        // ── Botones ──────────────────────────────────────────────────────────
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15));
        panelBotones.setBackground(new Color(245, 247, 250));

        JButton btnAgregar = crearBoton("Agregar", new Color(46, 125, 50));
        JButton btnModificar = crearBoton("Modificar", new Color(25, 118, 210));
        JButton btnEliminar = crearBoton("Eliminar", new Color(198, 40, 40));
        JButton btnDeshacer = crearBoton("Deshacer", new Color(255, 143, 0));
        JButton btnRefrescar = crearBoton("Refrescar", new Color(97, 97, 97));

        btnAgregar.addActionListener(e -> agregarResiduo());
        btnModificar.addActionListener(e -> modificarResiduo());
        btnEliminar.addActionListener(e -> eliminarResiduo());
        btnDeshacer.addActionListener(e -> deshacerEliminacion());
        btnRefrescar.addActionListener(e -> cargarDatosAsync());

        panelBotones.add(btnAgregar);
        panelBotones.add(btnModificar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnDeshacer);
        panelBotones.add(btnRefrescar);
        panelPrincipal.add(panelBotones, BorderLayout.SOUTH);

        // ── Listener de búsqueda ─────────────────────────────────────────────
        DocumentListener dl = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                buscarResiduos();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                buscarResiduos();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                buscarResiduos();
            }
        };
        txtBuscar.getDocument().addDocumentListener(dl);
        cbFiltro.addActionListener(e -> buscarResiduos());
    }

    // ════════════════════════════════════════════════════════════════════════
    //  TABLA
    // ════════════════════════════════════════════════════════════════════════
    private void pintarTabla(List<Waste> lista) {

        modelo.setRowCount(0);

        for (Waste w : lista) {

            modelo.addRow(new Object[]{
                w.getId(),
                w.getTypeName(),
                w.getQuantity(),
                w.getUnitMeasurementName(),
                w.getWasteGenerationDate() != null
                ? w.getWasteGenerationDate().format(formatoFecha)
                : "",
                w.isHasStorageLocation() ? "SÍ" : "NO",
                w.getStateName(),
                w.isDangerousness() ? "SÍ" : "NO",
                w.getEntityName()
            });
        }
    }

    /**
     * Filtra en memoria sobre lo que ya está pintado (no vuelve a consultar la
     * BD).
     */
    private void buscarResiduos() {

        if (listaMostrada == null) {
            return;
        }

        String texto = txtBuscar.getText().trim();

        if (texto.isEmpty()) {
            pintarTabla(listaMostrada);
            return;
        }

        String campo = cbFiltro.getSelectedItem().toString();
        String textoLower = texto.toLowerCase(Locale.ROOT);

        List<Waste> filtrada = listaMostrada.stream().filter(w -> {

            return switch (campo) {
                case "ID" -> String.valueOf(w.getId()).contains(textoLower);
                case "Tipo" -> w.getTypeName() != null
                    && w.getTypeName()
                            .toLowerCase(Locale.ROOT)
                            .contains(textoLower);
                case "Estado físico" -> w.getStateName() != null
                    && w.getStateName()
                            .toLowerCase(Locale.ROOT)
                            .contains(textoLower);
                case "Unidad" -> w.getUnitMeasurementName() != null
                    && w.getUnitMeasurementName()
                            .toLowerCase(Locale.ROOT)
                            .contains(textoLower);
                case "Entidad" -> w.getEntityName() != null
                    && w.getEntityName()
                            .toLowerCase(Locale.ROOT)
                            .contains(textoLower);
                default -> true;
            };

        }).toList();

        pintarTabla(filtrada);
    }

    // ════════════════════════════════════════════════════════════════════════
    //  CRUD (cada acción llama directo al DAO y luego recarga desde BD)
    // ════════════════════════════════════════════════════════════════════════
    private void agregarResiduo() {
        JComboBox<TipoItem> cbTipo = crearComboTipos();
        JTextField txtCantidad = new JTextField();
        JTextField txtUnidad = new JTextField();
        JTextField txtFecha = new JTextField(LocalDate.now().format(formatoFecha));
        JTextField txtEntityId = new JTextField();
        JComboBox<String> cbEstadoFisico = new JComboBox<>(ESTADOS_RESIDUO);
        JCheckBox chkAlmacenado = new JCheckBox("Tiene ubicación de almacenamiento");
        JCheckBox chkPeligroso = new JCheckBox("Es peligroso");

        Object[] campos = {
            "Tipo de residuo:", cbTipo,
            "Cantidad:", txtCantidad,
            "Unidad:", txtUnidad,
            "Fecha generación (dd/MM/yyyy):", txtFecha,
            "ID Entidad:", txtEntityId,
            "Estado físico:", cbEstadoFisico,
            chkAlmacenado,
            chkPeligroso
        };

        if (cbTipo.getItemCount() == 0) {
            JOptionPane.showMessageDialog(this,
                    "No hay tipos de residuo configurados en la tabla 'types' (category = 'TIPO_RESIDUO').");
            return;
        }

        if (JOptionPane.showConfirmDialog(this, campos, "Agregar Residuo",
                JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            double cantidad = Double.parseDouble(txtCantidad.getText().trim().replace(",", "."));
            if (cantidad <= 0) {
                throw new NumberFormatException("Cantidad debe ser > 0");
            }

            TipoItem tipoSeleccionado = (TipoItem) cbTipo.getSelectedItem();

            Waste w = new Waste();
            w.setType(tipoSeleccionado.code);
            w.setQuantity(cantidad);
            w.setUnitMeasurement(txtUnidad.getText().trim());
            w.setWasteGenerationDate(parseFechaSegura(txtFecha.getText().trim()));
            w.setState(cbEstadoFisico.getSelectedItem().toString());
            w.setHasStorageLocation(chkAlmacenado.isSelected());
            w.setDangerousness(chkPeligroso.isSelected());

            String entidadTxt = txtEntityId.getText().trim();
            if (entidadTxt.isEmpty()) {
                JOptionPane.showMessageDialog(this, "El ID de entidad es obligatorio.");
                return;
            }
            w.setEntityId(Long.valueOf(entidadTxt));

            if (wasteDAO.insertWaste(w, usuarioActivo.getIdUsuario())) {
                JOptionPane.showMessageDialog(this, "Residuo agregado correctamente.");
                cargarDatosAsync();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo agregar el residuo.");
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Cantidad o ID de entidad inválidos.");
        }
    }

    private void modificarResiduo() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un residuo.");
            return;
        }

        long id = (long) modelo.getValueAt(fila, 0);
        Waste w = wasteDAO.findById(id);
        if (w == null) {
            JOptionPane.showMessageDialog(this, "No se encontró el residuo.");
            return;
        }

        JComboBox<TipoItem> cbTipo = crearComboTipos();
        seleccionarTipoPorCodigo(cbTipo, w.getType());
        JTextField txtCantidad = new JTextField(String.valueOf(w.getQuantity()));
        JTextField txtUnidad = new JTextField(w.getUnitMeasurement());
        JTextField txtFecha = new JTextField(
                w.getWasteGenerationDate() != null ? w.getWasteGenerationDate().format(formatoFecha) : "");
        JTextField txtEntityId = new JTextField(w.getEntityId() != null ? String.valueOf(w.getEntityId()) : "");
        JComboBox<String> cbEstadoFisico = new JComboBox<>(ESTADOS_RESIDUO);
        cbEstadoFisico.setSelectedItem(w.getState());
        JCheckBox chkAlmacenado = new JCheckBox("Tiene ubicación de almacenamiento", w.isHasStorageLocation());
        JCheckBox chkPeligroso = new JCheckBox("Es peligroso", w.isDangerousness());

        Object[] campos = {
            "Tipo de residuo:", cbTipo,
            "Cantidad:", txtCantidad,
            "Unidad:", txtUnidad,
            "Fecha generación (dd/MM/yyyy):", txtFecha,
            "ID Entidad:", txtEntityId,
            "Estado físico:", cbEstadoFisico,
            chkAlmacenado,
            chkPeligroso
        };

        if (JOptionPane.showConfirmDialog(this, campos, "Modificar Residuo",
                JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            double cantidad = Double.parseDouble(txtCantidad.getText().trim().replace(",", "."));
            if (cantidad <= 0) {
                throw new NumberFormatException();
            }

            TipoItem tipoSeleccionado = (TipoItem) cbTipo.getSelectedItem();
            if (tipoSeleccionado == null) {
                JOptionPane.showMessageDialog(this, "Seleccione un tipo de residuo.");
                return;
            }

            w.setType(tipoSeleccionado.code);
            w.setQuantity(cantidad);
            w.setUnitMeasurement(txtUnidad.getText().trim());
            w.setWasteGenerationDate(parseFechaSegura(txtFecha.getText().trim()));
            w.setState(cbEstadoFisico.getSelectedItem().toString());
            w.setHasStorageLocation(chkAlmacenado.isSelected());
            w.setDangerousness(chkPeligroso.isSelected());

            String entidadTxt = txtEntityId.getText().trim();
            if (entidadTxt.isEmpty()) {
                JOptionPane.showMessageDialog(this, "El ID de entidad es obligatorio.");
                return;
            }
            w.setEntityId(Long.valueOf(entidadTxt));

            if (wasteDAO.updateWaste(w, usuarioActivo.getIdUsuario())) {
                JOptionPane.showMessageDialog(this, "Residuo actualizado correctamente.");
                cargarDatosAsync();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo actualizar.");
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Cantidad o ID de entidad inválidos.");
        }
    }

    private void eliminarResiduo() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un residuo.");
            return;
        }

        long id = (long) modelo.getValueAt(fila, 0);

        if (JOptionPane.showConfirmDialog(this,
                "¿Eliminar el residuo seleccionado?", "Confirmar",
                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            return;
        }

        // Respaldo ANTES de eliminar, para poder deshacer.
        Waste respaldo = wasteDAO.findById(id);
        if (respaldo == null) {
            JOptionPane.showMessageDialog(this, "No se encontró el residuo.");
            return;
        }

        if (wasteDAO.deleteWaste(id)) {
            historialEliminados.push(respaldo);
            JOptionPane.showMessageDialog(this, "Residuo eliminado.");
            cargarDatosAsync();
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo eliminar.");
        }
    }

    private void deshacerEliminacion() {
        if (historialEliminados.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay residuos para restaurar.");
            return;
        }

        Waste w = historialEliminados.pop();

        if (wasteDAO.restoreWaste(w)) {
            JOptionPane.showMessageDialog(this, "Residuo restaurado correctamente.");
            cargarDatosAsync();
        } else {
            historialEliminados.push(w);
            JOptionPane.showMessageDialog(this, "No se pudo restaurar el residuo.");
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    //  UTIL
    // ════════════════════════════════════════════════════════════════════════
    private LocalDate parseFechaSegura(String texto) {
        try {
            return LocalDate.parse(texto, formatoFecha);
        } catch (Exception e) {
            return LocalDate.now();
        }
    }

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

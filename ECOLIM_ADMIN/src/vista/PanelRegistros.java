package vista;

import dao.RegistroDAO;
import dao.UsuarioDAO;
import dao.UbicacionDAO;
import estructura.CacheRegistros;
import modelo.RegistroRecoleccion;
import modelo.Usuario;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Stack;

/**
 * PanelRegistros optimizado.
 *
 * Mejoras principales:
 *  - Toda resolución ID→nombre usa el CacheRegistros (árboles TreeMap/BST)
 *    en O(log n) en lugar de iterar HashMap o hacer consultas repetidas.
 *  - buscarRegistros() filtra sobre la lista en memoria sin tocar la BD.
 *  - cargarRegistros() solo pinta la tabla; los datos ya están en el árbol.
 *  - La carga de usuarios/ubicaciones al inicio es única y no se repite.
 *  - SwingWorker para la carga inicial evita bloquear el EDT.
 */
public class PanelRegistros extends JPanel {

    // ── Componentes UI ───────────────────────────────────────────────────────
    private JTable tabla;
    private DefaultTableModel modelo;
    private JTextField txtBuscar;
    private JComboBox<String> cbFiltro;
    private JLabel lblEstado;

    // ── DAOs ─────────────────────────────────────────────────────────────────
    private final RegistroDAO   registroDAO  = new RegistroDAO();
    private final UsuarioDAO    usuarioDAO   = new UsuarioDAO();
    private final UbicacionDAO  ubicacionDAO = new UbicacionDAO();

    // ── Cache (árbol BST + TreeMaps) ─────────────────────────────────────────
    private final CacheRegistros cache = CacheRegistros.getInstance();

    // ── Estado ───────────────────────────────────────────────────────────────
    private final Usuario usuarioActivo;
    private final Stack<RegistroRecoleccion> historialEliminados = new Stack<>();
    private final DateTimeFormatter formatoFecha =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // ════════════════════════════════════════════════════════════════════════
    //  CONSTRUCTOR
    // ════════════════════════════════════════════════════════════════════════

    public PanelRegistros(Usuario usuarioActivo) {
        this.usuarioActivo = usuarioActivo;
        initComponents();
        cargarDatosAsync();   // carga en hilo aparte para no congelar la UI
    }

    // ════════════════════════════════════════════════════════════════════════
    //  CARGA ASÍNCRONA CON SwingWorker
    // ════════════════════════════════════════════════════════════════════════

    private void cargarDatosAsync() {
        lblEstado.setText("Cargando datos…");
        setEnabled(false);

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                // Llena los árboles/TreeMaps del cache desde la BD (una sola vez)
                cache.cargarUsuarios(usuarioDAO.listarUsuarios());
                cache.cargarUbicaciones(ubicacionDAO.listarUbicaciones());
                registroDAO.recargarCacheDesdeDB();
                return null;
            }

            @Override
            protected void done() {
                setEnabled(true);
                lblEstado.setText("");
                pintarTabla(cache.listarRegistros());
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

        JLabel subtitulo = new JLabel("Administración de registros de recolección");
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

        cbFiltro = new JComboBox<>(new String[]{"ID", "Usuario", "Ubicación", "Residuo", "Unidad"});
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
                new Object[]{"ID", "Usuario", "Ubicación", "Residuo",
                             "Cantidad", "Unidad", "Fecha", "Observaciones"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
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

        JButton btnAgregar   = crearBoton("Agregar",   new Color(46, 125, 50));
        JButton btnModificar = crearBoton("Modificar", new Color(25, 118, 210));
        JButton btnEliminar  = crearBoton("Eliminar",  new Color(198, 40, 40));
        JButton btnDeshacer  = crearBoton("Deshacer",  new Color(255, 143, 0));
        JButton btnRefrescar = crearBoton("Refrescar", new Color(97, 97, 97));

        btnAgregar  .addActionListener(e -> agregarRegistro());
        btnModificar.addActionListener(e -> modificarRegistro());
        btnEliminar .addActionListener(e -> eliminarRegistro());
        btnDeshacer .addActionListener(e -> deshacerEliminacion());
        btnRefrescar.addActionListener(e -> refrescarDesdeDB());

        panelBotones.add(btnAgregar);
        panelBotones.add(btnModificar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnDeshacer);
        panelBotones.add(btnRefrescar);
        panelPrincipal.add(panelBotones, BorderLayout.SOUTH);

        // ── Listener de búsqueda ─────────────────────────────────────────────
        DocumentListener dl = new DocumentListener() {
            @Override public void insertUpdate (DocumentEvent e) { buscarRegistros(); }
            @Override public void removeUpdate (DocumentEvent e) { buscarRegistros(); }
            @Override public void changedUpdate(DocumentEvent e) { buscarRegistros(); }
        };
        txtBuscar.getDocument().addDocumentListener(dl);
        cbFiltro.addActionListener(e -> buscarRegistros());
    }

    // ════════════════════════════════════════════════════════════════════════
    //  TABLA
    // ════════════════════════════════════════════════════════════════════════

    /**
     * Pinta la tabla con la lista recibida.
     * No hace ninguna consulta a la BD; solo resuelve IDs→nombres
     * desde el cache (O(log n) por fila via TreeMap).
     */
    private void pintarTabla(List<RegistroRecoleccion> lista) {
        modelo.setRowCount(0);
        for (RegistroRecoleccion r : lista) {
            modelo.addRow(new Object[]{
                r.getIdRegistro(),
                cache.nombreUsuario(r.getIdUsuario()),
                cache.nombreUbicacion(r.getIdUbicacion()),
                CacheRegistros.nombreResiduo(r.getIdResiduo()),
                r.getCantidad(),
                r.getUnidad(),
                r.getFecha() != null ? r.getFecha().format(formatoFecha) : "",
                r.getObservaciones()
            });
        }
    }

    /** Recarga la tabla desde el árbol BST (sin tocar la BD). */
    private void cargarRegistros() {
        pintarTabla(cache.listarRegistros());
    }

    /**
     * Filtra en memoria sobre el árbol BST → O(n) local, 0 consultas a BD.
     */
    private void buscarRegistros() {
        String texto = txtBuscar.getText().trim();
        if (texto.isEmpty()) {
            cargarRegistros();
            return;
        }
        String campo = cbFiltro.getSelectedItem().toString();
        pintarTabla(cache.filtrarPorTexto(campo, texto));
    }

    /** Fuerza recarga desde BD e invalida el cache (botón Refrescar). */
    private void refrescarDesdeDB() {
        cache.invalidar();
        cargarDatosAsync();
    }

    // ════════════════════════════════════════════════════════════════════════
    //  CRUD
    // ════════════════════════════════════════════════════════════════════════

    private void agregarRegistro() {
        JComboBox<String> cbUbicacion = new JComboBox<>(cache.nombresUbicaciones());
        JComboBox<String> cbResiduo   = new JComboBox<>(
                new String[]{"Sólido", "Líquido", "Gaseoso", "Metálico"});
        JTextField txtCantidad     = new JTextField();
        JTextField txtUnidad       = new JTextField();
        JTextField txtObservaciones = new JTextField();

        Object[] campos = {
            "Ubicación:", cbUbicacion,
            "Residuo:",   cbResiduo,
            "Cantidad:",  txtCantidad,
            "Unidad:",    txtUnidad,
            "Observaciones:", txtObservaciones
        };

        if (JOptionPane.showConfirmDialog(this, campos, "Agregar Registro",
                JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return;

        try {
            double cantidad = Double.parseDouble(txtCantidad.getText().trim());
            if (cantidad <= 0) throw new NumberFormatException("Cantidad debe ser > 0");

            RegistroRecoleccion r = new RegistroRecoleccion();
            r.setIdUsuario(usuarioActivo.getIdUsuario());
            r.setIdUbicacion(cache.idUbicacion(cbUbicacion.getSelectedItem().toString()));
            r.setIdResiduo(CacheRegistros.idResiduo(cbResiduo.getSelectedItem().toString()));
            r.setCantidad(cantidad);
            r.setUnidad(txtUnidad.getText().trim());
            r.setFecha(LocalDateTime.now());
            r.setObservaciones(txtObservaciones.getText().trim());

            if (registroDAO.insertarRegistro(r)) {
                JOptionPane.showMessageDialog(this, "Registro agregado correctamente.");
                cargarRegistros();   // refresca desde árbol (sin BD)
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo agregar el registro.");
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Cantidad inválida: ingrese un número positivo.");
        }
    }

    private void modificarRegistro() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) { JOptionPane.showMessageDialog(this, "Seleccione un registro."); return; }

        int id = (int) modelo.getValueAt(fila, 0);

        // Búsqueda O(log n) en árbol BST — sin consulta a BD
        RegistroRecoleccion r = registroDAO.buscarPorId(id);
        if (r == null) { JOptionPane.showMessageDialog(this, "No se encontró el registro."); return; }

        JComboBox<String> cbUbicacion = new JComboBox<>(cache.nombresUbicaciones());
        cbUbicacion.setSelectedItem(cache.nombreUbicacion(r.getIdUbicacion()));

        JComboBox<String> cbResiduo = new JComboBox<>(
                new String[]{"Sólido", "Líquido", "Gaseoso", "Metálico"});
        cbResiduo.setSelectedItem(CacheRegistros.nombreResiduo(r.getIdResiduo()));

        JTextField txtCantidad      = new JTextField(String.valueOf(r.getCantidad()));
        JTextField txtUnidad        = new JTextField(r.getUnidad());
        JTextField txtObservaciones = new JTextField(r.getObservaciones());

        Object[] campos = {
            "Ubicación:", cbUbicacion,
            "Residuo:",   cbResiduo,
            "Cantidad:",  txtCantidad,
            "Unidad:",    txtUnidad,
            "Observaciones:", txtObservaciones
        };

        if (JOptionPane.showConfirmDialog(this, campos, "Modificar Registro",
                JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return;

        try {
            double cantidad = Double.parseDouble(txtCantidad.getText().trim());
            if (cantidad <= 0) throw new NumberFormatException();

            r.setIdUbicacion(cache.idUbicacion(cbUbicacion.getSelectedItem().toString()));
            r.setIdResiduo(CacheRegistros.idResiduo(cbResiduo.getSelectedItem().toString()));
            r.setCantidad(cantidad);
            r.setUnidad(txtUnidad.getText().trim());
            r.setObservaciones(txtObservaciones.getText().trim());

            if (registroDAO.actualizarRegistro(r)) {
                JOptionPane.showMessageDialog(this, "Registro actualizado correctamente.");
                cargarRegistros();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo actualizar.");
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Cantidad inválida: ingrese un número positivo.");
        }
    }

    private void eliminarRegistro() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) { JOptionPane.showMessageDialog(this, "Seleccione un registro."); return; }

        int id = (int) modelo.getValueAt(fila, 0);

        if (JOptionPane.showConfirmDialog(this,
                "¿Eliminar el registro seleccionado?", "Confirmar",
                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;

        // Obtiene copia desde árbol BST antes de eliminar (para UNDO)
        RegistroRecoleccion respaldo = registroDAO.buscarPorId(id);
        if (respaldo == null) { JOptionPane.showMessageDialog(this, "No se encontró el registro."); return; }

        if (registroDAO.eliminarRegistro(id)) {
            historialEliminados.push(respaldo);
            JOptionPane.showMessageDialog(this, "Registro eliminado.");
            cargarRegistros();
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo eliminar.");
        }
    }

    private void deshacerEliminacion() {
        if (historialEliminados.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay registros para restaurar.");
            return;
        }

        RegistroRecoleccion registro = historialEliminados.pop();

        if (registroDAO.restaurarRegistro(registro)) {
            JOptionPane.showMessageDialog(this, "Registro restaurado correctamente.");
            cargarRegistros();
        } else {
            historialEliminados.push(registro);
            JOptionPane.showMessageDialog(this, "No se pudo restaurar el registro.");
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

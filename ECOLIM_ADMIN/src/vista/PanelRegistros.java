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

<<<<<<< HEAD
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
=======
import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Element;
import com.lowagie.text.pdf.PdfWriter;

import java.io.FileOutputStream;

/**
 * PanelRegistros optimizado.
 *
 * Mejoras principales: - Toda resolución ID→nombre usa el CacheRegistros
 * (árboles TreeMap/BST) en O(log n) en lugar de iterar HashMap o hacer
 * consultas repetidas. - buscarRegistros() filtra sobre la lista en memoria sin
 * tocar la BD. - cargarRegistros() solo pinta la tabla; los datos ya están en
 * el árbol. - La carga de usuarios/ubicaciones al inicio es única y no se
 * repite. - SwingWorker para la carga inicial evita bloquear el EDT.
>>>>>>> e2abc0d (Actualización de ECOLIM_ADMIN)
 */
public class PanelRegistros extends JPanel {

    // ── Componentes UI ───────────────────────────────────────────────────────
    private JTable tabla;
    private DefaultTableModel modelo;
    private JTextField txtBuscar;
    private JComboBox<String> cbFiltro;
    private JLabel lblEstado;

    // ── DAOs ─────────────────────────────────────────────────────────────────
<<<<<<< HEAD
    private final RegistroDAO   registroDAO  = new RegistroDAO();
    private final UsuarioDAO    usuarioDAO   = new UsuarioDAO();
    private final UbicacionDAO  ubicacionDAO = new UbicacionDAO();
=======
    private final RegistroDAO registroDAO = new RegistroDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final UbicacionDAO ubicacionDAO = new UbicacionDAO();
>>>>>>> e2abc0d (Actualización de ECOLIM_ADMIN)

    // ── Cache (árbol BST + TreeMaps) ─────────────────────────────────────────
    private final CacheRegistros cache = CacheRegistros.getInstance();

    // ── Estado ───────────────────────────────────────────────────────────────
    private final Usuario usuarioActivo;
    private final Stack<RegistroRecoleccion> historialEliminados = new Stack<>();
<<<<<<< HEAD
    private final DateTimeFormatter formatoFecha =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
=======
    private final DateTimeFormatter formatoFecha
            = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
>>>>>>> e2abc0d (Actualización de ECOLIM_ADMIN)

    // ════════════════════════════════════════════════════════════════════════
    //  CONSTRUCTOR
    // ════════════════════════════════════════════════════════════════════════
<<<<<<< HEAD

=======
>>>>>>> e2abc0d (Actualización de ECOLIM_ADMIN)
    public PanelRegistros(Usuario usuarioActivo) {
        this.usuarioActivo = usuarioActivo;
        initComponents();
        cargarDatosAsync();   // carga en hilo aparte para no congelar la UI
    }

    // ════════════════════════════════════════════════════════════════════════
    //  CARGA ASÍNCRONA CON SwingWorker
    // ════════════════════════════════════════════════════════════════════════
<<<<<<< HEAD

=======
>>>>>>> e2abc0d (Actualización de ECOLIM_ADMIN)
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
<<<<<<< HEAD

=======
>>>>>>> e2abc0d (Actualización de ECOLIM_ADMIN)
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
<<<<<<< HEAD
                             "Cantidad", "Unidad", "Fecha", "Observaciones"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
=======
                    "Cantidad", "Unidad", "Fecha", "Observaciones"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
>>>>>>> e2abc0d (Actualización de ECOLIM_ADMIN)
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

<<<<<<< HEAD
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
=======
        JButton btnAgregar = crearBoton("Agregar", new Color(46, 125, 50));
        JButton btnModificar = crearBoton("Modificar", new Color(25, 118, 210));
        JButton btnEliminar = crearBoton("Eliminar", new Color(198, 40, 40));
        JButton btnDeshacer = crearBoton("Deshacer", new Color(255, 143, 0));
        JButton btnRefrescar = crearBoton("Refrescar", new Color(97, 97, 97));
        JButton btnPDF = crearBoton("Generar PDF", new Color(156, 39, 176));

        btnAgregar.addActionListener(e -> agregarRegistro());
        btnModificar.addActionListener(e -> modificarRegistro());
        btnEliminar.addActionListener(e -> eliminarRegistro());
        btnDeshacer.addActionListener(e -> deshacerEliminacion());
        btnRefrescar.addActionListener(e -> refrescarDesdeDB());
        btnPDF.addActionListener(e -> generarPDFRegistro());
>>>>>>> e2abc0d (Actualización de ECOLIM_ADMIN)

        panelBotones.add(btnAgregar);
        panelBotones.add(btnModificar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnDeshacer);
        panelBotones.add(btnRefrescar);
        panelPrincipal.add(panelBotones, BorderLayout.SOUTH);
<<<<<<< HEAD

        // ── Listener de búsqueda ─────────────────────────────────────────────
        DocumentListener dl = new DocumentListener() {
            @Override public void insertUpdate (DocumentEvent e) { buscarRegistros(); }
            @Override public void removeUpdate (DocumentEvent e) { buscarRegistros(); }
            @Override public void changedUpdate(DocumentEvent e) { buscarRegistros(); }
=======
        panelBotones.add(btnPDF);

        // ── Listener de búsqueda ─────────────────────────────────────────────
        DocumentListener dl = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                buscarRegistros();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                buscarRegistros();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                buscarRegistros();
            }
>>>>>>> e2abc0d (Actualización de ECOLIM_ADMIN)
        };
        txtBuscar.getDocument().addDocumentListener(dl);
        cbFiltro.addActionListener(e -> buscarRegistros());
    }

    // ════════════════════════════════════════════════════════════════════════
    //  TABLA
    // ════════════════════════════════════════════════════════════════════════
<<<<<<< HEAD

    /**
     * Pinta la tabla con la lista recibida.
     * No hace ninguna consulta a la BD; solo resuelve IDs→nombres
     * desde el cache (O(log n) por fila via TreeMap).
=======
    /**
     * Pinta la tabla con la lista recibida. No hace ninguna consulta a la BD;
     * solo resuelve IDs→nombres desde el cache (O(log n) por fila via TreeMap).
>>>>>>> e2abc0d (Actualización de ECOLIM_ADMIN)
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

<<<<<<< HEAD
    /** Recarga la tabla desde el árbol BST (sin tocar la BD). */
=======
    /**
     * Recarga la tabla desde el árbol BST (sin tocar la BD).
     */
>>>>>>> e2abc0d (Actualización de ECOLIM_ADMIN)
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

<<<<<<< HEAD
    /** Fuerza recarga desde BD e invalida el cache (botón Refrescar). */
=======
    /**
     * Fuerza recarga desde BD e invalida el cache (botón Refrescar).
     */
>>>>>>> e2abc0d (Actualización de ECOLIM_ADMIN)
    private void refrescarDesdeDB() {
        cache.invalidar();
        cargarDatosAsync();
    }

    // ════════════════════════════════════════════════════════════════════════
    //  CRUD
    // ════════════════════════════════════════════════════════════════════════
<<<<<<< HEAD

    private void agregarRegistro() {
        JComboBox<String> cbUbicacion = new JComboBox<>(cache.nombresUbicaciones());
        JComboBox<String> cbResiduo   = new JComboBox<>(
                new String[]{"Sólido", "Líquido", "Gaseoso", "Metálico"});
        JTextField txtCantidad     = new JTextField();
        JTextField txtUnidad       = new JTextField();
=======
    private void agregarRegistro() {
        JComboBox<String> cbUbicacion = new JComboBox<>(cache.nombresUbicaciones());
        JComboBox<String> cbResiduo = new JComboBox<>(
                new String[]{"Sólido", "Líquido", "Gaseoso", "Metálico"});
        JTextField txtCantidad = new JTextField();
        JTextField txtUnidad = new JTextField();
>>>>>>> e2abc0d (Actualización de ECOLIM_ADMIN)
        JTextField txtObservaciones = new JTextField();

        Object[] campos = {
            "Ubicación:", cbUbicacion,
<<<<<<< HEAD
            "Residuo:",   cbResiduo,
            "Cantidad:",  txtCantidad,
            "Unidad:",    txtUnidad,
=======
            "Residuo:", cbResiduo,
            "Cantidad:", txtCantidad,
            "Unidad:", txtUnidad,
>>>>>>> e2abc0d (Actualización de ECOLIM_ADMIN)
            "Observaciones:", txtObservaciones
        };

        if (JOptionPane.showConfirmDialog(this, campos, "Agregar Registro",
<<<<<<< HEAD
                JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return;

        try {
            double cantidad = Double.parseDouble(txtCantidad.getText().trim());
            if (cantidad <= 0) throw new NumberFormatException("Cantidad debe ser > 0");
=======
                JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            double cantidad = Double.parseDouble(txtCantidad.getText().trim());
            if (cantidad <= 0) {
                throw new NumberFormatException("Cantidad debe ser > 0");
            }
>>>>>>> e2abc0d (Actualización de ECOLIM_ADMIN)

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
<<<<<<< HEAD
        if (fila == -1) { JOptionPane.showMessageDialog(this, "Seleccione un registro."); return; }
=======
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un registro.");
            return;
        }
>>>>>>> e2abc0d (Actualización de ECOLIM_ADMIN)

        int id = (int) modelo.getValueAt(fila, 0);

        // Búsqueda O(log n) en árbol BST — sin consulta a BD
        RegistroRecoleccion r = registroDAO.buscarPorId(id);
<<<<<<< HEAD
        if (r == null) { JOptionPane.showMessageDialog(this, "No se encontró el registro."); return; }
=======
        if (r == null) {
            JOptionPane.showMessageDialog(this, "No se encontró el registro.");
            return;
        }
>>>>>>> e2abc0d (Actualización de ECOLIM_ADMIN)

        JComboBox<String> cbUbicacion = new JComboBox<>(cache.nombresUbicaciones());
        cbUbicacion.setSelectedItem(cache.nombreUbicacion(r.getIdUbicacion()));

        JComboBox<String> cbResiduo = new JComboBox<>(
                new String[]{"Sólido", "Líquido", "Gaseoso", "Metálico"});
        cbResiduo.setSelectedItem(CacheRegistros.nombreResiduo(r.getIdResiduo()));

<<<<<<< HEAD
        JTextField txtCantidad      = new JTextField(String.valueOf(r.getCantidad()));
        JTextField txtUnidad        = new JTextField(r.getUnidad());
=======
        JTextField txtCantidad = new JTextField(String.valueOf(r.getCantidad()));
        JTextField txtUnidad = new JTextField(r.getUnidad());
>>>>>>> e2abc0d (Actualización de ECOLIM_ADMIN)
        JTextField txtObservaciones = new JTextField(r.getObservaciones());

        Object[] campos = {
            "Ubicación:", cbUbicacion,
<<<<<<< HEAD
            "Residuo:",   cbResiduo,
            "Cantidad:",  txtCantidad,
            "Unidad:",    txtUnidad,
=======
            "Residuo:", cbResiduo,
            "Cantidad:", txtCantidad,
            "Unidad:", txtUnidad,
>>>>>>> e2abc0d (Actualización de ECOLIM_ADMIN)
            "Observaciones:", txtObservaciones
        };

        if (JOptionPane.showConfirmDialog(this, campos, "Modificar Registro",
<<<<<<< HEAD
                JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return;

        try {
            double cantidad = Double.parseDouble(txtCantidad.getText().trim());
            if (cantidad <= 0) throw new NumberFormatException();
=======
                JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            double cantidad = Double.parseDouble(txtCantidad.getText().trim());
            if (cantidad <= 0) {
                throw new NumberFormatException();
            }
>>>>>>> e2abc0d (Actualización de ECOLIM_ADMIN)

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
<<<<<<< HEAD
        if (fila == -1) { JOptionPane.showMessageDialog(this, "Seleccione un registro."); return; }
=======
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un registro.");
            return;
        }
>>>>>>> e2abc0d (Actualización de ECOLIM_ADMIN)

        int id = (int) modelo.getValueAt(fila, 0);

        if (JOptionPane.showConfirmDialog(this,
                "¿Eliminar el registro seleccionado?", "Confirmar",
<<<<<<< HEAD
                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;

        // Obtiene copia desde árbol BST antes de eliminar (para UNDO)
        RegistroRecoleccion respaldo = registroDAO.buscarPorId(id);
        if (respaldo == null) { JOptionPane.showMessageDialog(this, "No se encontró el registro."); return; }
=======
                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            return;
        }

        // Obtiene copia desde árbol BST antes de eliminar (para UNDO)
        RegistroRecoleccion respaldo = registroDAO.buscarPorId(id);
        if (respaldo == null) {
            JOptionPane.showMessageDialog(this, "No se encontró el registro.");
            return;
        }
>>>>>>> e2abc0d (Actualización de ECOLIM_ADMIN)

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

<<<<<<< HEAD
    // ════════════════════════════════════════════════════════════════════════
    //  UTIL
    // ════════════════════════════════════════════════════════════════════════

=======
    // PDF
    private void generarPDFRegistro() {

        int fila = tabla.getSelectedRow();

        if (fila == -1) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione un registro.");
            return;
        }

        int idRegistro = Integer.parseInt(
                modelo.getValueAt(fila, 0).toString());

        RegistroRecoleccion registro
                = registroDAO.buscarPorId(idRegistro);

        generarPDF(registro);
    }

    private void generarPDF(RegistroRecoleccion r) {

        try {

            Document documento = new Document();

            String nombre = "Registro_" + r.getIdRegistro() + ".pdf";

            PdfWriter.getInstance(documento,
                    new FileOutputStream(nombre));

            documento.open();

            // Fuente para el título
            com.lowagie.text.Font titulo
                    = new com.lowagie.text.Font(
                            com.lowagie.text.Font.HELVETICA,
                            20,
                            com.lowagie.text.Font.BOLD);

            // Fuente para el contenido
            com.lowagie.text.Font texto
                    = new com.lowagie.text.Font(
                            com.lowagie.text.Font.HELVETICA,
                            12,
                            com.lowagie.text.Font.NORMAL);

            Paragraph p = new Paragraph("REPORTE DE REGISTRO", titulo);
            p.setAlignment(Element.ALIGN_CENTER);

            documento.add(p);
            documento.add(new Paragraph(" "));

            documento.add(new Paragraph("ID Registro: " + r.getIdRegistro(), texto));
            documento.add(new Paragraph("Usuario: " + cache.nombreUsuario(r.getIdUsuario()), texto));
            documento.add(new Paragraph("Ubicación: " + cache.nombreUbicacion(r.getIdUbicacion()), texto));
            documento.add(new Paragraph("Residuo: " + CacheRegistros.nombreResiduo(r.getIdResiduo()), texto));
            documento.add(new Paragraph("Cantidad: " + r.getCantidad(), texto));
            documento.add(new Paragraph("Unidad: " + r.getUnidad(), texto));
            documento.add(new Paragraph("Fecha: " + r.getFecha().format(formatoFecha), texto));
            documento.add(new Paragraph("Observaciones:", texto));
            documento.add(new Paragraph(r.getObservaciones(), texto));

            documento.close();

            JOptionPane.showMessageDialog(this,
                    "PDF generado correctamente:\n" + nombre);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error al generar el PDF:\n" + ex.getMessage());
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    //  UTIL
    // ════════════════════════════════════════════════════════════════════════
>>>>>>> e2abc0d (Actualización de ECOLIM_ADMIN)
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

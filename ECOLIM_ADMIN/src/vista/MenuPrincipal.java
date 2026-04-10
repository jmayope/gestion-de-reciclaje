package vista;

import modelo.Usuario;

import javax.swing.*;
import java.awt.*;

public class MenuPrincipal extends JFrame {

    private JPanel panelMenu;
    private JPanel panelContenido;
    private JLabel lblBienvenida;

    private JButton btnUsuarios;
    private JButton btnUbicaciones;
    private JButton btnReportes;
    private JButton btnEstadisticas;
    private JButton btnCerrarSesion;

    private final Usuario admin;

    public MenuPrincipal(Usuario admin) {
        this.admin = admin;
        initComponents();
        setTitle("ECOLIM ADMIN");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Icono de la ventana / barra superior / barra de tareas
        ImageIcon iconApp = new ImageIcon(getClass().getResource("/imagenes/logoprincipal.png"));
        setIconImage(iconApp.getImage());

        mostrarPanel(new PanelUsuarios());
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        panelMenu = new JPanel(null);
        panelMenu.setPreferredSize(new Dimension(250, 0));
        panelMenu.setBackground(new Color(29, 185, 84));
        add(panelMenu, BorderLayout.WEST);

        // Logo superior del menú
        ImageIcon iconoLogo = new ImageIcon(getClass().getResource("/imagenes/logoprincipal.png"));
        Image imagenEscalada = iconoLogo.getImage().getScaledInstance(90, 90, Image.SCALE_SMOOTH);
        JLabel lblLogo = new JLabel(new ImageIcon(imagenEscalada));
        lblLogo.setBounds(80, 15, 90, 90);
        panelMenu.add(lblLogo);

        JLabel lblTitulo = new JLabel("ECOLIM ADMIN");
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitulo.setBounds(20, 110, 210, 30);
        panelMenu.add(lblTitulo);

        String nombreCompleto = admin.getNombre() + " " + admin.getApellido();

        lblBienvenida = new JLabel("<html>Bienvenido<br><b>" + nombreCompleto + "</b></html>");
        lblBienvenida.setForeground(Color.WHITE);
        lblBienvenida.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblBienvenida.setBounds(25, 150, 200, 60);
        panelMenu.add(lblBienvenida);

        btnUsuarios = crearBoton("Usuarios", 240);
        btnUbicaciones = crearBoton("Ubicaciones", 300);
        btnReportes = crearBoton("Reportes", 360);
        btnEstadisticas = crearBoton("Estadísticas", 420);
        btnCerrarSesion = crearBoton("Cerrar sesión", 540);

        panelMenu.add(btnUsuarios);
        panelMenu.add(btnUbicaciones);
        panelMenu.add(btnReportes);
        panelMenu.add(btnEstadisticas);
        panelMenu.add(btnCerrarSesion);

        panelContenido = new JPanel(new BorderLayout());
        panelContenido.setBackground(new Color(245, 245, 245));
        add(panelContenido, BorderLayout.CENTER);

        btnUsuarios.addActionListener(e -> mostrarPanel(new PanelUsuarios()));
        btnUbicaciones.addActionListener(e -> mostrarPanel(new PanelUbicaciones()));
        btnReportes.addActionListener(e -> mostrarPanel(new PanelReportes()));
        btnEstadisticas.addActionListener(e -> mostrarPanel(new PanelEstadisticas()));

        btnCerrarSesion.addActionListener(e -> {
            new LoginAdmin().setVisible(true);
            dispose();
        });
    }

    private JButton crearBoton(String texto, int y) {
        JButton boton = new JButton(texto);
        boton.setBounds(20, y, 200, 42);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        boton.setBackground(Color.WHITE);
        boton.setForeground(new Color(29, 185, 84));
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return boton;
    }

    private void mostrarPanel(JPanel panelNuevo) {
        panelContenido.removeAll();
        panelContenido.add(panelNuevo, BorderLayout.CENTER);
        panelContenido.revalidate();
        panelContenido.repaint();
    }
}
package vista;

import modelo.Usuario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MenuPrincipal extends JFrame {

    private JPanel panelMenu;
    private JPanel panelContenido;

    private JButton btnUsuarios;
    private JButton btnRoles;
    private JButton btnPlantas;
    private JButton btnUbicaciones;
    private JButton btnReportes;
    private JButton btnEstadisticas;
    private JButton btnRegistros;
    private JButton btnDevices;
    private JButton btnUser;
    private JButton btnEntity;
    private JButton btnPeople;
    private JButton btnWastes;
    private JButton btnType;
    private JButton btnManifest;
    private JButton btnCerrarSesion;

    private final Usuario admin;

    private final Color COLOR_MENU = new Color(18, 33, 61);
    private final Color COLOR_HOVER = new Color(40, 70, 150);

    public MenuPrincipal(Usuario admin) {

        this.admin = admin;

        initComponents();

        setTitle("ECOLIM");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        ImageIcon iconApp = new ImageIcon(
                getClass().getResource("/imagenes/logoprincipal.png")
        );

        setIconImage(iconApp.getImage());

        mostrarPanel(new PanelUsuarios());
    }

    private void initComponents() {

        setLayout(new BorderLayout());

        crearMenuLateral();
        crearAreaPrincipal();
        configurarEventos();
    }

    private void crearMenuLateral() {
        panelMenu = new JPanel(null);
        panelMenu.setBackground(COLOR_MENU);

        // Altura grande para permitir más botones
        panelMenu.setPreferredSize(new Dimension(260, 1050));

        JScrollPane scrollMenu = new JScrollPane(panelMenu);

        scrollMenu.setPreferredSize(new Dimension(260, 0));
        scrollMenu.setBorder(null);

        scrollMenu.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );

        add(scrollMenu, BorderLayout.WEST);

        ImageIcon iconoLogo = new ImageIcon(
                getClass().getResource("/imagenes/logoprincipal.png")
        );

        Image img = iconoLogo.getImage().getScaledInstance(
                110,
                110,
                Image.SCALE_SMOOTH
        );

        JLabel lblLogo = new JLabel(new ImageIcon(img));
        lblLogo.setBounds(75, 20, 110, 110);

        panelMenu.add(lblLogo);

        JLabel lblTitulo = new JLabel("ECOLIM");
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitulo.setBounds(20, 135, 220, 35);

        panelMenu.add(lblTitulo);

        JLabel lblUsuario = new JLabel(
                "<html><center>"
                + admin.getNombre()
                + "<br>"
                + admin.getApellido()
                + "</center></html>"
        );

        lblUsuario.setForeground(new Color(210, 210, 210));
        lblUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblUsuario.setHorizontalAlignment(SwingConstants.CENTER);
        lblUsuario.setBounds(20, 175, 220, 50);

        panelMenu.add(lblUsuario);

        int y = 260;
        int espacio = 50;

        btnUsuarios     = crearBoton("Usuarios", y); y += espacio;
        btnRoles        = crearBoton("Roles", y); y += espacio;

        btnUser         = crearBoton("Usuario para empresa", y); y += espacio;
        btnEntity       = crearBoton("Empresas", y); y += espacio;
        btnPeople       = crearBoton("Personas", y); y += espacio;
        btnDevices      = crearBoton("Dispositivos", y); y += espacio;
        btnType         = crearBoton("Tipos", y); y += espacio;

        btnUbicaciones  = crearBoton("Ubicaciones", y); y += espacio;
        btnPlantas      = crearBoton("Plantas", y); y += espacio;
        btnRegistros    = crearBoton("Recolección de residuos", y); y += espacio;
        btnWastes       = crearBoton("Residuos", y); y += espacio;
        btnReportes     = crearBoton("Reportes", y); y += espacio;
        btnManifest     = crearBoton("Manifiesto", y); y += espacio;
        btnEstadisticas = crearBoton("Estadísticas", y); y += espacio;

        y += 20;

        btnCerrarSesion = crearBoton("Cerrar sesión", y);

        // Agregar TODOS los botones
        panelMenu.add(btnUsuarios);
        panelMenu.add(btnRoles);

        panelMenu.add(btnUser);
        panelMenu.add(btnEntity);
        panelMenu.add(btnPeople);
        panelMenu.add(btnDevices);
        panelMenu.add(btnType);

        panelMenu.add(btnUbicaciones);
        panelMenu.add(btnPlantas);
        panelMenu.add(btnRegistros);
        panelMenu.add(btnWastes);
        panelMenu.add(btnReportes);
        panelMenu.add(btnManifest);
        panelMenu.add(btnEstadisticas);

        panelMenu.add(btnCerrarSesion);
    }

    private void crearAreaPrincipal() {

        JPanel contenedor = new JPanel(new BorderLayout());
        contenedor.setBackground(new Color(245, 247, 250));

        add(contenedor, BorderLayout.CENTER);

        JLabel lblAdmin = new JLabel(
                admin.getNombre() + " " + admin.getApellido()
        );

        lblAdmin.setFont(
                new Font("Segoe UI", Font.PLAIN, 15)
        );

        lblAdmin.setBounds(
                1100,
                25,
                300,
                25
        );

        panelContenido = new JPanel(new BorderLayout());
        panelContenido.setBackground(new Color(245, 247, 250));

        contenedor.add(panelContenido, BorderLayout.CENTER);
    }

    private JButton crearBoton(String texto, int y) {

        JButton boton = new JButton(texto);

        boton.setBounds(15, y, 230, 45);

        boton.setBackground(COLOR_MENU);
        boton.setForeground(Color.WHITE);

        boton.setFont(
                new Font("Segoe UI", Font.PLAIN, 15)
        );

        boton.setHorizontalAlignment(
                SwingConstants.LEFT
        );

        boton.setFocusPainted(false);
        boton.setBorderPainted(false);

        boton.setCursor(
                new Cursor(Cursor.HAND_CURSOR)
        );

        boton.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent e) {
                boton.setBackground(COLOR_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                boton.setBackground(COLOR_MENU);
            }
        });

        return boton;
    }

    private void configurarEventos() {

        btnUsuarios.addActionListener(
                e -> mostrarPanel(new PanelUsuarios())
        );
        
        btnUser.addActionListener(
                e -> mostrarPanel(new PanelUsers())
        );

        btnRoles.addActionListener(
                e -> mostrarPanel(new PanelRoles())
        );

        btnUbicaciones.addActionListener(
                e -> mostrarPanel(new PanelUbicaciones())
        );

        btnPlantas.addActionListener(
                e -> mostrarPanel(new PanelPlantas())
        );

        btnReportes.addActionListener(
                e -> mostrarPanel(new PanelReportes())
        );

        btnEstadisticas.addActionListener(
                e -> mostrarPanel(new PanelEstadisticas())
        );

        btnRegistros.addActionListener(
                e -> mostrarPanel(new PanelRegistros(admin))
        );
        
        btnWastes.addActionListener(
                e -> mostrarPanel(new PanelResiduos(admin))
        );
        
        btnEntity.addActionListener(
                e -> mostrarPanel(new PanelEntities())
        );

        btnPeople.addActionListener(
                e -> mostrarPanel(new PanelPeople())
        );

        btnType.addActionListener(
                e -> mostrarPanel(new PanelTypes())
        );
        
        btnDevices.addActionListener(
                e -> mostrarPanel(new PanelDevices())
        );
        
        btnManifest.addActionListener(
                e -> mostrarPanel(new PanelManifest(admin.getIdUsuario())));

        btnCerrarSesion.addActionListener(e -> {

            new LoginAdmin().setVisible(true);

            dispose();
        });
    }

    private void mostrarPanel(JPanel panelNuevo) {

        JPanel wrapper = new JPanel(
                new BorderLayout()
        );

        wrapper.setBackground(
                new Color(245, 247, 250)
        );

        wrapper.setBorder(
                BorderFactory.createEmptyBorder(
                        20,
                        20,
                        20,
                        20
                )
        );

        wrapper.add(
                panelNuevo,
                BorderLayout.CENTER
        );

        panelContenido.removeAll();

        panelContenido.add(
                wrapper,
                BorderLayout.CENTER
        );

        panelContenido.revalidate();
        panelContenido.repaint();
    }
}
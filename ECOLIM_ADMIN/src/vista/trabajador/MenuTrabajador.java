/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vista.trabajador;

import modelo.Usuario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import vista.LoginAdmin;
import vista.PanelRegistros;

public class MenuTrabajador extends JFrame {

    private JPanel panelMenu;
    private JPanel panelContenido;

    private JButton btnRegistros;
    private JButton btnCerrarSesion;

    private final Usuario usuario;

    private final Color COLOR_MENU = new Color(18, 33, 61);
    private final Color COLOR_HOVER = new Color(40, 70, 150);

    public MenuTrabajador(Usuario usuario) {

        this.usuario = usuario;

        initComponents();

        setTitle("ECOLIM");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        ImageIcon iconApp = new ImageIcon(
                getClass().getResource("/imagenes/logoprincipal.png")
        );

        setIconImage(iconApp.getImage());

        mostrarPanel(new PanelRegistros(usuario));
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
        panelMenu.setPreferredSize(new Dimension(260, 900));

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
                + usuario.getNombre()
                + "<br>"
                + usuario.getApellido()
                + "</center></html>"
        );

        lblUsuario.setForeground(new Color(210, 210, 210));
        lblUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblUsuario.setHorizontalAlignment(SwingConstants.CENTER);
        lblUsuario.setBounds(20, 175, 220, 50);

        panelMenu.add(lblUsuario);

        int y = 260;

        btnRegistros = crearBoton("Residuos", y);

        y += 70;

        btnCerrarSesion = crearBoton("Cerrar sesión", y);

        panelMenu.add(btnRegistros);
        panelMenu.add(btnCerrarSesion);
    }

    private void crearAreaPrincipal() {

        JPanel contenedor = new JPanel(new BorderLayout());
        contenedor.setBackground(new Color(245, 247, 250));

        add(contenedor, BorderLayout.CENTER);

        JLabel lblAdmin = new JLabel(
                usuario.getNombre() + " " + usuario.getApellido()
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

        btnRegistros.addActionListener(
                e -> mostrarPanel(new PanelRegistros(usuario))
        );

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

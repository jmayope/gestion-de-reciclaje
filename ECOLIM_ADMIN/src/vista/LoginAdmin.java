package vista;

import dao.UsuarioDAO;
import modelo.Usuario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.net.URL;

public class LoginAdmin extends JFrame {

    private JTextField txtCorreo;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnSalir;

    public LoginAdmin() {
        initComponents();
        setTitle("Login Administrador - ECOLIM");
        setSize(420, 430);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        URL iconUrl = getClass().getResource("/imagenes/logoprincipal.png");
        if (iconUrl != null) {
            ImageIcon iconApp = new ImageIcon(iconUrl);
            setIconImage(iconApp.getImage());
        }
    }

    private void initComponents() {
        JPanel panel = new JPanel(null);
        panel.setBackground(new Color(242, 242, 242));
        setContentPane(panel);

        URL logoUrl = getClass().getResource("/imagenes/logoprincipal.png");
        if (logoUrl != null) {
            ImageIcon iconoLogo = new ImageIcon(logoUrl);
            Image imagenEscalada = iconoLogo.getImage().getScaledInstance(90, 90, Image.SCALE_SMOOTH);
            JLabel lblLogo = new JLabel(new ImageIcon(imagenEscalada));
            lblLogo.setBounds(155, 20, 90, 90);
            panel.add(lblLogo);
        }

        JLabel lblTitulo = new JLabel("Administrador ECOLIM");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitulo.setBounds(60, 120, 300, 30);
        panel.add(lblTitulo);

        JLabel lblCorreo = new JLabel("Correo");
        lblCorreo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblCorreo.setBounds(50, 175, 100, 20);
        panel.add(lblCorreo);

        txtCorreo = new JTextField();
        txtCorreo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtCorreo.setBounds(50, 195, 300, 35);
        panel.add(txtCorreo);

        JLabel lblPassword = new JLabel("Contraseña");
        lblPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblPassword.setBounds(50, 240, 100, 20);
        panel.add(lblPassword);

        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPassword.setBounds(50, 260, 300, 35);
        panel.add(txtPassword);

        btnLogin = new JButton("Iniciar sesión");
        btnLogin.setBounds(50, 320, 145, 38);
        btnLogin.setBackground(new Color(29, 185, 84));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setFocusPainted(false);
        btnLogin.setBorderPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.addActionListener(this::loginAdmin);
        panel.add(btnLogin);

        btnSalir = new JButton("Salir");
        btnSalir.setBounds(205, 320, 145, 38);
        btnSalir.setBackground(new Color(220, 53, 69));
        btnSalir.setForeground(Color.WHITE);
        btnSalir.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSalir.setFocusPainted(false);
        btnSalir.setBorderPainted(false);
        btnSalir.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSalir.addActionListener(e -> System.exit(0));
        panel.add(btnSalir);
    }

    private void loginAdmin(ActionEvent evt) {
        String correo = txtCorreo.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (correo.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese correo y contraseña.");
            return;
        }

        UsuarioDAO usuarioDAO = new UsuarioDAO();
        Usuario admin = usuarioDAO.loginAdmin(correo, password);

        if (admin != null) {
            JOptionPane.showMessageDialog(this, "Bienvenido " + admin.getNombre() + " " + admin.getApellido());
            new MenuPrincipal(admin).setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Credenciales incorrectas o no tiene rol admin.");
        }
    }
}
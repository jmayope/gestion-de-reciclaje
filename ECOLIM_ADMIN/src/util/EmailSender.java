package util;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailSender {

    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";

    // Se leen desde config.properties, nunca hardcodeadas
    private static final String REMITENTE = ConfigLoader.get("mail.user");
    private static final String CLAVE_APP = ConfigLoader.get("mail.app.password");

    public static boolean enviarCredenciales(
            String destinatario,
            String nombrePersona,
            String username,
            String password) {

        if (destinatario == null || destinatario.isBlank()) {
            return false;
        }
        
        System.out.println("REMITENTE " + REMITENTE);
        System.out.println("CLAVE APP " + CLAVE_APP);
        
        
        if (REMITENTE == null || CLAVE_APP == null) {

            System.out.println(
                    "Error enviarCredenciales: faltan las claves mail.user / mail.app.password "
                    + "en config.properties"
            );

            return false;
        }

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(REMITENTE, CLAVE_APP);
            }
        });

        try {
            Message mensaje = new MimeMessage(session);
            mensaje.setFrom(new InternetAddress(REMITENTE));
            mensaje.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(destinatario)
            );
            mensaje.setSubject("Credenciales de acceso al sistema");

            String cuerpo = "Hola " + nombrePersona + ",\n\n"
                    + "Se ha creado tu usuario en el sistema. Estas son tus credenciales:\n\n"
                    + "Usuario: " + username + "\n"
                    + "Contraseña: " + password + "\n\n"
                    + "Por seguridad, te recomendamos cambiarla luego de tu primer ingreso.\n\n"
                    + "Saludos.";

            mensaje.setText(cuerpo);

            Transport.send(mensaje);

            return true;

        } catch (MessagingException e) {
            System.out.println("Error enviarCredenciales: " + e.getMessage());
            return false;
        }
    }
}
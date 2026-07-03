
package conexion;

import conexion.ConexionSupabase;
import java.sql.Connection;
import java.sql.SQLException;

public class TestConexion {

    public static void main(String[] args) {

        try {

            Connection con = ConexionSupabase.conectar();

            if (con != null) {
                System.out.println("Conexion exitosa con Supabase");
            }

        } catch (SQLException e) {

            System.out.println("Error de conexion");
            e.printStackTrace();

        }

    }
}
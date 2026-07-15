package dao;

import conexion.ConexionSupabase;

import java.sql.*;

public class EntityUserDAO {

    public boolean insertEntityUser(long entityId, long userId, boolean status) {

        String sql = """
            INSERT INTO entity_users
            (entity_id, user_id, status)
            VALUES (?,?,?)
            """;

        try (Connection con = ConexionSupabase.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, entityId);
            ps.setLong(2, userId);
            ps.setBoolean(3, status);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Error insertEntityUser: " + e.getMessage());
            return false;
        }
    }
}
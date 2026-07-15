package dao;

import conexion.ConexionSupabase;

import java.sql.*;

public class UserRoleDAO {

    public boolean insertUserRole(long userId, long roleId, long entityId, boolean status) {

        String sql = """
            INSERT INTO user_roles
            (user_id, role_id, entity_id, status)
            VALUES (?,?,?,?)
            """;

        try (Connection con = ConexionSupabase.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, userId);
            ps.setLong(2, roleId);
            ps.setLong(3, entityId);
            ps.setBoolean(4, status);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Error insertUserRole: " + e.getMessage());
            return false;
        }
    }
}
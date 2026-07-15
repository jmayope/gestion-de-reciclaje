package dao;

import conexion.ConexionSupabase;
import modelo.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public List<User> listUsers() {

        List<User> list = new ArrayList<>();

        String sql
                = "SELECT u.*,"
                + " e.name AS entityName,"
                + " p.full_name AS personName"
                + " FROM users AS u"
                + " INNER JOIN entities AS e on e.id = u.entity_id"
                + " INNER JOIN people AS p on p.id = u.person_id"
                + " ORDER BY u.id";

        try (
                Connection con = ConexionSupabase.conectar(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                User u = new User();

                u.setId(rs.getLong("id"));
                u.setEntityName(rs.getString("entityName"));
                u.setPersonName(rs.getString("personName"));
                u.setCode(rs.getString("code"));
                u.setUsername(rs.getString("username"));
                u.setPassword(rs.getString("password"));
                u.setIsPrincipal(rs.getBoolean("is_principal"));
                u.setStatus(rs.getBoolean("status"));

                list.add(u);
            }

        } catch (Exception e) {
            System.out.println("Error listUsers: " + e.getMessage());
        }

        return list;
    }

    public boolean insertUser(User u) {

        String sql = """
        INSERT INTO users
        (entity_id, person_id, code, username, password, is_principal, status)
        VALUES (?,?,?,?,?,?,?)
        """;

        try (
                Connection con = ConexionSupabase.conectar(); PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            u.setCode(generarCodigo());

            ps.setObject(1, u.getEntityId());
            ps.setObject(2, u.getPersonId());
            ps.setString(3, u.getCode());
            ps.setString(4, u.getUsername());
            ps.setString(5, u.getPassword());
            ps.setBoolean(6, u.isIsPrincipal());
            ps.setBoolean(7, u.isStatus());

            int filas = ps.executeUpdate();

            if (filas > 0) {

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        u.setId(rs.getLong(1));
                    }
                }

                return true;
            }

            return false;

        } catch (Exception e) {

            System.out.println(
                    "Error insertUser: " + e.getMessage()
            );

            return false;
        }
    }

    public boolean updateUser(User u) {

        String sql = """
            UPDATE users
            SET entity_id=?,
                person_id=?,
                code=?,
                username=?,
                password=?,
                is_principal=?,
                status=?
            WHERE id=?
            """;

        try (
                Connection con = ConexionSupabase.conectar(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setObject(1, u.getEntityId());
            ps.setObject(2, u.getPersonId());
            ps.setString(3, u.getCode());
            ps.setString(4, u.getUsername());
            ps.setString(5, u.getPassword());
            ps.setBoolean(6, u.isIsPrincipal());
            ps.setBoolean(7, u.isStatus());
            ps.setLong(8, u.getId());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {

            System.out.println(
                    "Error updateUser: " + e.getMessage()
            );

            return false;
        }
    }

    public boolean deleteUser(long id) {

        String sql = "DELETE FROM users WHERE id=?";

        try (
                Connection con = ConexionSupabase.conectar(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Error deleteUser: " + e.getMessage());
            return false;
        }
    }

    public User findById(long id) {

        String sql = "SELECT * FROM users WHERE id=?";

        try (
                Connection con = ConexionSupabase.conectar(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    User u = new User();

                    u.setId(rs.getLong("id"));
                    u.setEntityId((Long) rs.getObject("entity_id"));
                    u.setPersonId((Long) rs.getObject("person_id"));
                    u.setCode(rs.getString("code"));
                    u.setUsername(rs.getString("username"));
                    u.setPassword(rs.getString("password"));
                    u.setIsPrincipal(rs.getBoolean("is_principal"));
                    u.setStatus(rs.getBoolean("status"));

                    return u;
                }
            }

        } catch (Exception e) {
            System.out.println("Error findById: " + e.getMessage());
        }

        return null;
    }

    public List<User> findByUsername(String username) {

        List<User> lista = new ArrayList<>();

        String sql = """
            SELECT *
            FROM users
            WHERE LOWER(username) LIKE ?
            ORDER BY id
            """;

        try (
                Connection con = ConexionSupabase.conectar(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(
                    1,
                    "%" + username.toLowerCase() + "%"
            );

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    User u = new User();

                    u.setId(rs.getLong("id"));
                    u.setEntityId((Long) rs.getObject("entity_id"));
                    u.setPersonId((Long) rs.getObject("person_id"));
                    u.setCode(rs.getString("code"));
                    u.setUsername(rs.getString("username"));
                    u.setPassword(rs.getString("password"));
                    u.setIsPrincipal(rs.getBoolean("is_principal"));
                    u.setStatus(rs.getBoolean("status"));

                    lista.add(u);
                }
            }

        } catch (Exception e) {

            System.out.println(
                    "Error findByUsername: " + e.getMessage()
            );
        }

        return lista;
    }

    public List<User> findByCode(String code) {

        List<User> lista = new ArrayList<>();

        String sql = """
            SELECT *
            FROM users
            WHERE LOWER(code) LIKE ?
            ORDER BY id
            """;

        try (
                Connection con = ConexionSupabase.conectar(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(
                    1,
                    "%" + code.toLowerCase() + "%"
            );

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    User u = new User();

                    u.setId(rs.getLong("id"));
                    u.setEntityId((Long) rs.getObject("entity_id"));
                    u.setPersonId((Long) rs.getObject("person_id"));
                    u.setCode(rs.getString("code"));
                    u.setUsername(rs.getString("username"));
                    u.setPassword(rs.getString("password"));
                    u.setIsPrincipal(rs.getBoolean("is_principal"));
                    u.setStatus(rs.getBoolean("status"));

                    lista.add(u);
                }
            }

        } catch (Exception e) {

            System.out.println(
                    "Error findByCode: " + e.getMessage()
            );
        }

        return lista;
    }

    private String generarCodigo() {

        String sql = """
            SELECT COALESCE(MAX(id),0)+1 AS siguiente
            FROM users
            """;

        try (
                Connection con = ConexionSupabase.conectar(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {

                int numero = rs.getInt("siguiente");

                return String.format("USR%03d", numero);
            }

        } catch (Exception e) {

            System.out.println(
                    "Error generarCodigo: " + e.getMessage()
            );
        }

        return "USR001";
    }

    public boolean existePrincipalPorEmpresa(Long entityId) {

        String sql = """
        SELECT COUNT(*) total
        FROM users
        WHERE entity_id = ?
        AND is_principal = true
        """;

        try (
                Connection con = ConexionSupabase.conectar(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, entityId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return rs.getInt("total") > 0;
                }
            }

        } catch (Exception e) {

            System.out.println(
                    "Error existePrincipalPorEmpresa: "
                    + e.getMessage()
            );
        }

        return false;
    }

    public boolean existeOtroPrincipal(
            Long entityId,
            Long userId) {

        String sql = """
        SELECT COUNT(*)
        FROM users
        WHERE entity_id = ?
        AND is_principal = true
        AND id <> ?
        """;

        try (
                Connection con = ConexionSupabase.conectar(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, entityId);
            ps.setLong(2, userId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}

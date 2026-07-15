package dao;

import conexion.ConexionSupabase;
import modelo.Entity;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EntityDAO {

    public List<Entity> listEntities() {

        List<Entity> list = new ArrayList<>();

        String sql = "SELECT e.*, t.name AS typeName"
                + " FROM entities AS e"
                + " INNER JOIN types AS t on t.code = e.type AND t.category = 'TIPO_ENTIDAD' ORDER BY id";

        try (
                Connection con = ConexionSupabase.conectar();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {

                Entity e = new Entity();

                e.setId(rs.getLong("id"));
                e.setCode(rs.getString("code"));
                e.setName(rs.getString("name"));
                e.setAddress(rs.getString("address"));
                e.setPhone(rs.getString("phone"));
                e.setType(rs.getString("type"));
                e.setTypeName(rs.getString("typeName"));
                e.setStatus(rs.getBoolean("status"));

                list.add(e);
            }

        } catch (Exception ex) {
            System.out.println("Error listEntities: " + ex.getMessage());
        }

        return list;
    }

    public boolean insertEntity(Entity e) {

        String sql = """
                INSERT INTO entities
                (code, name, address, phone, type, status)
                VALUES (?,?,?,?,?,?)
                """;

        try (
                Connection con = ConexionSupabase.conectar();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setString(1, e.getCode());
            ps.setString(2, e.getName());
            ps.setString(3, e.getAddress());
            ps.setString(4, e.getPhone());
            ps.setString(5, e.getType());
            ps.setBoolean(6, e.isStatus());

            return ps.executeUpdate() > 0;

        } catch (Exception ex) {
            System.out.println("Error insertEntity: " + ex.getMessage());
            return false;
        }
    }

    public boolean updateEntity(Entity e) {

        String sql = """
                UPDATE entities
                SET code=?,
                    name=?,
                    address=?,
                    phone=?,
                    type=?,
                    status=?
                WHERE id=?
                """;

        try (
                Connection con = ConexionSupabase.conectar();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setString(1, e.getCode());
            ps.setString(2, e.getName());
            ps.setString(3, e.getAddress());
            ps.setString(4, e.getPhone());
            ps.setString(5, e.getType());
            ps.setBoolean(6, e.isStatus());
            ps.setLong(7, e.getId());

            return ps.executeUpdate() > 0;

        } catch (Exception ex) {
            System.out.println("Error updateEntity: " + ex.getMessage());
            return false;
        }
    }

    public boolean deleteEntity(long id) {

        String sql = "DELETE FROM entities WHERE id=?";

        try (
                Connection con = ConexionSupabase.conectar();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setLong(1, id);

            return ps.executeUpdate() > 0;

        } catch (Exception ex) {
            System.out.println("Error deleteEntity: " + ex.getMessage());
            return false;
        }
    }

    public Entity findById(long id) {

        String sql = "SELECT * FROM entities WHERE id=?";

        try (
                Connection con = ConexionSupabase.conectar();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    Entity e = new Entity();

                    e.setId(rs.getLong("id"));
                    e.setCode(rs.getString("code"));
                    e.setName(rs.getString("name"));
                    e.setAddress(rs.getString("address"));
                    e.setPhone(rs.getString("phone"));
                    e.setType(rs.getString("type"));
                    e.setStatus(rs.getBoolean("status"));

                    return e;
                }
            }

        } catch (Exception ex) {
            System.out.println("Error findById: " + ex.getMessage());
        }

        return null;
    }
}
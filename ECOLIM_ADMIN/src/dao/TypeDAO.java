package dao;

import conexion.ConexionSupabase;
import modelo.Type;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TypeDAO {

    public List<Type> listTypes() {

        List<Type> list = new ArrayList<>();

        String sql = "SELECT * FROM types ORDER BY id";

        try (
                Connection con = ConexionSupabase.conectar();
                PreparedStatement ps =
                        con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {

                Type t = new Type();

                t.setId(rs.getLong("id"));
                t.setCategory(rs.getString("category"));
                t.setCode(rs.getString("code"));
                t.setName(rs.getString("name"));
                t.setDescripcion(rs.getString("descripcion"));

                t.setAdditionalFields(
                        rs.getString("additional_fields")
                );

                t.setStatus(rs.getBoolean("status"));

                list.add(t);
            }

        } catch (Exception e) {

            System.out.println(
                    "Error listTypes: " + e.getMessage()
            );
        }

        return list;
    }

    public boolean insertType(Type t) {

        String sql = """
                INSERT INTO types
                (
                    category,
                    code,
                    name,
                    descripcion,
                    additional_fields,
                    status
                )
                VALUES (?,?,?,?,?,?)
                """;

        try (
                Connection con = ConexionSupabase.conectar();
                PreparedStatement ps =
                        con.prepareStatement(sql)
        ) {

            ps.setString(1, t.getCategory());
            ps.setString(2, t.getCode());
            ps.setString(3, t.getName());
            ps.setString(4, t.getDescripcion());

            ps.setString(
                    5,
                    t.getAdditionalFields()
            );

            ps.setBoolean(6, t.isStatus());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {

            System.out.println(
                    "Error insertType: "
                            + e.getMessage()
            );

            return false;
        }
    }

    public boolean updateType(Type t) {

        String sql = """
                UPDATE types
                SET
                    category=?,
                    code=?,
                    name=?,
                    descripcion=?,
                    additional_fields=?,
                    status=?
                WHERE id=?
                """;

        try (
                Connection con = ConexionSupabase.conectar();
                PreparedStatement ps =
                        con.prepareStatement(sql)
        ) {

            ps.setString(1, t.getCategory());
            ps.setString(2, t.getCode());
            ps.setString(3, t.getName());
            ps.setString(4, t.getDescripcion());

            ps.setString(
                    5,
                    t.getAdditionalFields()
            );

            ps.setBoolean(6, t.isStatus());

            ps.setLong(7, t.getId());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {

            System.out.println(
                    "Error updateType: "
                            + e.getMessage()
            );

            return false;
        }
    }

    public boolean deleteType(long id) {

        String sql = "DELETE FROM types WHERE id=?";

        try (
                Connection con = ConexionSupabase.conectar();
                PreparedStatement ps =
                        con.prepareStatement(sql)
        ) {

            ps.setLong(1, id);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {

            System.out.println(
                    "Error deleteType: "
                            + e.getMessage()
            );

            return false;
        }
    }

    public Type findById(long id) {

        String sql = "SELECT * FROM types WHERE id=?";

        try (
                Connection con = ConexionSupabase.conectar();
                PreparedStatement ps =
                        con.prepareStatement(sql)
        ) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    Type t = new Type();

                    t.setId(rs.getLong("id"));
                    t.setCategory(rs.getString("category"));
                    t.setCode(rs.getString("code"));
                    t.setName(rs.getString("name"));

                    t.setDescripcion(
                            rs.getString("descripcion")
                    );

                    t.setAdditionalFields(
                            rs.getString(
                                    "additional_fields"
                            )
                    );

                    t.setStatus(
                            rs.getBoolean("status")
                    );

                    return t;
                }
            }

        } catch (Exception e) {

            System.out.println(
                    "Error findById: "
                            + e.getMessage()
            );
        }

        return null;
    }
}
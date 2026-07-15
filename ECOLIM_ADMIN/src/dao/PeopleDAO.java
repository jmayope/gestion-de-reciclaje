package dao;

import conexion.ConexionSupabase;
import modelo.People;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PeopleDAO {

    public List<People> listPeople() {

        List<People> list = new ArrayList<>();

        String sql = "SELECT * FROM people ORDER BY id";

        try (
                Connection con = ConexionSupabase.conectar();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {

                People p = new People();

                p.setId(rs.getLong("id"));
                p.setCode(rs.getString("code"));
                p.setFullName(rs.getString("full_name"));
                p.setAddress(rs.getString("address"));
                p.setPhone(rs.getString("phone"));
                p.setEmail(rs.getString("email"));
                p.setGender(rs.getBoolean("gender"));
                p.setBirthDate(rs.getDate("birth_date"));
                p.setStatus(rs.getBoolean("status"));

                list.add(p);
            }

        } catch (Exception e) {

            System.out.println(
                    "Error listPeople: " + e.getMessage()
            );
        }

        return list;
    }

    public boolean insertPeople(People p) {

        String sql = """
                INSERT INTO people
                (
                    code,
                    full_name,
                    address,
                    phone,
                    email,
                    gender,
                    birth_date,
                    status
                )
                VALUES (?,?,?,?,?,?,?,?)
                """;

        try (
                Connection con = ConexionSupabase.conectar();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setString(1, p.getCode());
            ps.setString(2, p.getFullName());
            ps.setString(3, p.getAddress());
            ps.setString(4, p.getPhone());
            ps.setString(5, p.getEmail());
            ps.setBoolean(6, p.isGender());
            ps.setDate(7, p.getBirthDate());
            ps.setBoolean(8, p.isStatus());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {

            System.out.println(
                    "Error insertPeople: " + e.getMessage()
            );

            return false;
        }
    }

    public boolean updatePeople(People p) {

        String sql = """
                UPDATE people
                SET
                    code=?,
                    full_name=?,
                    address=?,
                    phone=?,
                    email=?,
                    gender=?,
                    birth_date=?,
                    status=?
                WHERE id=?
                """;

        try (
                Connection con = ConexionSupabase.conectar();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setString(1, p.getCode());
            ps.setString(2, p.getFullName());
            ps.setString(3, p.getAddress());
            ps.setString(4, p.getPhone());
            ps.setString(5, p.getEmail());
            ps.setBoolean(6, p.isGender());
            ps.setDate(7, p.getBirthDate());
            ps.setBoolean(8, p.isStatus());
            ps.setLong(9, p.getId());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {

            System.out.println(
                    "Error updatePeople: " + e.getMessage()
            );

            return false;
        }
    }

    public boolean deletePeople(long id) {

        String sql = "DELETE FROM people WHERE id=?";

        try (
                Connection con = ConexionSupabase.conectar();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setLong(1, id);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {

            System.out.println(
                    "Error deletePeople: " + e.getMessage()
            );

            return false;
        }
    }

    public People findById(long id) {

        String sql = "SELECT * FROM people WHERE id=?";

        try (
                Connection con = ConexionSupabase.conectar();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    People p = new People();

                    p.setId(rs.getLong("id"));
                    p.setCode(rs.getString("code"));
                    p.setFullName(rs.getString("full_name"));
                    p.setAddress(rs.getString("address"));
                    p.setPhone(rs.getString("phone"));
                    p.setEmail(rs.getString("email"));
                    p.setGender(rs.getBoolean("gender"));
                    p.setBirthDate(rs.getDate("birth_date"));
                    p.setStatus(rs.getBoolean("status"));

                    return p;
                }
            }

        } catch (Exception e) {

            System.out.println(
                    "Error findById: " + e.getMessage()
            );
        }

        return null;
    }
}
package dao;

import conexion.ConexionSupabase;
import modelo.Devices;

import java.sql.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class DevicesDAO {

    public List<Devices> listDevices() {

        List<Devices> list = new ArrayList<>();

        String sql = """
                SELECT d.*,
                       e.name AS entityName,
                       u.username AS userName
                FROM devices d
                INNER JOIN entities e ON e.id = d.entity_id
                INNER JOIN users u ON u.id = d.user_id
                ORDER BY d.id
                """;

        try (
                Connection con = ConexionSupabase.conectar(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Devices d = new Devices();

                d.setId(rs.getLong("id"));
                d.setCreatedAt(rs.getObject("created_at", OffsetDateTime.class));
                d.setMacaddress(rs.getString("macaddress"));
                d.setOperativeSystem(rs.getString("operative_system"));
                d.setUserId(rs.getLong("user_id"));
                d.setEntityId(rs.getLong("entity_id"));
                d.setUserName(rs.getString("userName"));
                d.setEntityName(rs.getString("entityName"));
                d.setStatus(rs.getBoolean("status"));

                list.add(d);
            }

        } catch (Exception e) {
            System.out.println("Error listDevices: " + e.getMessage());
        }

        return list;
    }

    public boolean insertDevice(Devices d) {

        String sql = """
                INSERT INTO devices
                (macaddress, operative_system, user_id, entity_id, status)
                VALUES (?,?,?,?,?)
                """;

        try (
                Connection con = ConexionSupabase.conectar(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, d.getMacaddress());
            ps.setString(2, d.getOperativeSystem());
            ps.setLong(3, d.getUserId());
            ps.setLong(4, d.getEntityId());
            ps.setBoolean(5, d.isStatus());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {

            System.out.println("Error insertDevice: " + e.getMessage());

            return false;
        }
    }

    public boolean updateDevice(Devices d) {

        String sql = """
                UPDATE devices
                SET macaddress=?,
                    operative_system=?,
                    user_id=?,
                    entity_id=?,
                    status=?
                WHERE id=?
                """;

        try (
                Connection con = ConexionSupabase.conectar(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, d.getMacaddress());
            ps.setString(2, d.getOperativeSystem());
            ps.setLong(3, d.getUserId());
            ps.setLong(4, d.getEntityId());
            ps.setBoolean(5, d.isStatus());
            ps.setLong(6, d.getId());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {

            System.out.println("Error updateDevice: " + e.getMessage());

            return false;
        }
    }

    public boolean deleteDevice(long id) {

        String sql = "DELETE FROM devices WHERE id=?";

        try (
                Connection con = ConexionSupabase.conectar(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {

            System.out.println("Error deleteDevice: " + e.getMessage());

            return false;
        }
    }

    public Devices findById(long id) {

        String sql = """
            SELECT d.*,
                   e.name AS entityName,
                   u.username AS userName
            FROM devices d
            INNER JOIN entities e ON e.id = d.entity_id
            INNER JOIN users u ON u.id = d.user_id
            WHERE d.id = ?
            """;

        try (
                Connection con = ConexionSupabase.conectar(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                Devices d = new Devices();

                d.setId(rs.getLong("id"));
                d.setCreatedAt(rs.getObject("created_at", OffsetDateTime.class));
                d.setMacaddress(rs.getString("macaddress"));
                d.setOperativeSystem(rs.getString("operative_system"));
                d.setUserId(rs.getLong("user_id"));
                d.setEntityId(rs.getLong("entity_id"));
                d.setUserName(rs.getString("userName"));
                d.setEntityName(rs.getString("entityName"));
                d.setStatus(rs.getBoolean("status"));

                return d;
            }

        } catch (Exception e) {

            System.out.println("Error findById: " + e.getMessage());

        }

        return null;
    }

    public boolean existeMac(String mac) {

        String sql = "SELECT COUNT(*) FROM devices WHERE macaddress = ?";

        try (
                Connection con = ConexionSupabase.conectar(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, mac);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                return rs.getInt(1) > 0;

            }

        } catch (Exception e) {

            System.out.println("Error existeMac: " + e.getMessage());

        }

        return false;
    }
}

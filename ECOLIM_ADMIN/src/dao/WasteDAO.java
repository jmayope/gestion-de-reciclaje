package dao;

import conexion.ConexionSupabase;
import modelo.Waste;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WasteDAO {

    private static final String CATEGORIA_TIPO_RESIDUO = "TIPO_RESIDUO";
    private static final String CATEGORIA_UNIDAD_MEDIDA = "UNIDAD_MEDIDA";
    private static final String CATEGORIA_ESTADO_RESIDUO = "ESTADO_RESIDUO";

    public List<Waste> listWastes() {

        List<Waste> list = new ArrayList<>();

        String sql
                = "SELECT w.*, "
                + "e.name AS entityName, "
                + "tt.name AS typeName, "
                + "tu.name AS unitMeasurementName, "
                + "ts.name AS stateName "
                + "FROM wastes w "
                + "INNER JOIN entities e ON e.id = w.entity_id "
                + "LEFT JOIN types tt ON tt.category='" + CATEGORIA_TIPO_RESIDUO + "' "
                + "AND tt.code = w.type "
                + "LEFT JOIN types tu ON tu.category='" + CATEGORIA_UNIDAD_MEDIDA + "' "
                + "AND tu.code = w.unit_measurement "
                + "LEFT JOIN types ts ON ts.category='" + CATEGORIA_ESTADO_RESIDUO + "' "
                + "AND ts.code = w.state "
                + "ORDER BY w.id DESC";

        try (
                Connection con = ConexionSupabase.conectar(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (Exception e) {
            System.out.println("Error listWastes: " + e.getMessage());
        }

        return list;
    }

    public boolean insertWaste(Waste w, long userId) {

        String sql = """
            INSERT INTO wastes
            (type, quantity, unit_measurement, waste_generation_date,
             has_storage_location, state, status, created_at, created_by,
             entity_id, dangerousness)
            VALUES (?,?,?,?,?,?,true,now(),?,?,?)
            RETURNING id
            """;

        try (
                Connection con = ConexionSupabase.conectar(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, w.getType());
            ps.setDouble(2, w.getQuantity());
            ps.setString(3, w.getUnitMeasurement());

            if (w.getWasteGenerationDate() != null) {
                ps.setDate(4, Date.valueOf(w.getWasteGenerationDate()));
            } else {
                ps.setNull(4, Types.DATE);
            }

            ps.setBoolean(5, w.isHasStorageLocation());
            ps.setString(6, w.getState());
            ps.setLong(7, userId);
            ps.setObject(8, w.getEntityId());
            ps.setBoolean(9, w.isDangerousness());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    w.setId(rs.getLong("id"));
                    w.setCreatedBy(userId);
                    w.setStatus(true);
                    return true;
                }
            }

        } catch (Exception e) {
            System.out.println("Error insertWaste: " + e.getMessage());
        }

        return false;
    }

    public boolean updateWaste(Waste w, long userId) {

        String sql = """
            UPDATE wastes
            SET type=?,
                quantity=?,
                unit_measurement=?,
                waste_generation_date=?,
                has_storage_location=?,
                state=?,
                entity_id=?,
                dangerousness=?,
                updated_at=now(),
                updated_by=?
            WHERE id=?
            """;

        try (
                Connection con = ConexionSupabase.conectar(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, w.getType());
            ps.setDouble(2, w.getQuantity());
            ps.setString(3, w.getUnitMeasurement());

            if (w.getWasteGenerationDate() != null) {
                ps.setDate(4, Date.valueOf(w.getWasteGenerationDate()));
            } else {
                ps.setNull(4, Types.DATE);
            }

            ps.setBoolean(5, w.isHasStorageLocation());
            ps.setString(6, w.getState());
            ps.setObject(7, w.getEntityId());
            ps.setBoolean(8, w.isDangerousness());
            ps.setLong(9, userId);
            ps.setLong(10, w.getId());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Error updateWaste: " + e.getMessage());
            return false;
        }
    }

    /**
     * Activa/desactiva un residuo (columna status) sin borrar la fila.
     */
    public boolean setStatus(long id, boolean status, long userId) {

        String sql = """
            UPDATE wastes
            SET status=?, updated_at=now(), updated_by=?
            WHERE id=?
            """;

        try (
                Connection con = ConexionSupabase.conectar(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setBoolean(1, status);
            ps.setLong(2, userId);
            ps.setLong(3, id);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Error setStatus: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteWaste(long id) {

        String sql = "DELETE FROM wastes WHERE id=?";

        try (
                Connection con = ConexionSupabase.conectar(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Error deleteWaste: " + e.getMessage());
            return false;
        }
    }

    /**
     * Reinserta un residuo previamente eliminado, conservando su id original.
     * Se usa para el botón "Deshacer" tras un deleteWaste(). Nota: si la
     * columna id es GENERATED ALWAYS AS IDENTITY en Postgres, este INSERT
     * necesitará OVERRIDING SYSTEM VALUE; si es GENERATED BY DEFAULT (lo más
     * común), funciona tal cual.
     */
    public boolean restoreWaste(Waste w) {

        String sql = """
            INSERT INTO wastes
            (id, type, quantity, unit_measurement, waste_generation_date,
             has_storage_location, state, status, created_at, created_by,
             updated_at, updated_by, entity_id, dangerousness, publish_at)
            VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)
            """;

        try (
                Connection con = ConexionSupabase.conectar(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, w.getId());
            ps.setString(2, w.getType());
            ps.setDouble(3, w.getQuantity());
            ps.setString(4, w.getUnitMeasurement());

            if (w.getWasteGenerationDate() != null) {
                ps.setDate(5, Date.valueOf(w.getWasteGenerationDate()));
            } else {
                ps.setNull(5, Types.DATE);
            }

            ps.setBoolean(6, w.isHasStorageLocation());
            ps.setString(7, w.getState());
            ps.setBoolean(8, w.isStatus());

            if (w.getCreatedAt() != null) {
                ps.setTimestamp(9, Timestamp.from(w.getCreatedAt().toInstant()));
            } else {
                ps.setNull(9, Types.TIMESTAMP_WITH_TIMEZONE);
            }

            ps.setObject(10, w.getCreatedBy());

            if (w.getUpdatedAt() != null) {
                ps.setTimestamp(11, Timestamp.from(w.getUpdatedAt().toInstant()));
            } else {
                ps.setNull(11, Types.TIMESTAMP_WITH_TIMEZONE);
            }

            ps.setObject(12, w.getUpdatedBy());
            ps.setObject(13, w.getEntityId());
            ps.setBoolean(14, w.isDangerousness());

            if (w.getPublishAt() != null) {
                ps.setTimestamp(15, Timestamp.from(w.getPublishAt().toInstant()));
            } else {
                ps.setNull(15, Types.TIMESTAMP_WITH_TIMEZONE);
            }

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Error restoreWaste: " + e.getMessage());
            return false;
        }
    }

    public Waste findById(long id) {

        String sql
                = "SELECT w.*, "
                + "e.name AS entityName, "
                + "tt.name AS typeName, "
                + "tu.name AS unitMeasurementName, "
                + "ts.name AS stateName "
                + "FROM wastes w "
                + "INNER JOIN entities e ON e.id = w.entity_id "
                + "LEFT JOIN types tt ON tt.category='" + CATEGORIA_TIPO_RESIDUO + "' "
                + "AND tt.code = w.type "
                + "LEFT JOIN types tu ON tu.category='" + CATEGORIA_UNIDAD_MEDIDA + "' "
                + "AND tu.code = w.unit_measurement "
                + "LEFT JOIN types ts ON ts.category='" + CATEGORIA_ESTADO_RESIDUO + "' "
                + "AND ts.code = w.state "
                + "WHERE w.id = ?";

        try (
                Connection con = ConexionSupabase.conectar(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }

        } catch (Exception e) {
            System.out.println("Error findById: " + e.getMessage());
        }

        return null;
    }

    public List<Waste> findByType(String typeCode) {

        List<Waste> list = new ArrayList<>();

        String sql
                = "SELECT w.*, "
                + "e.name AS entityName, "
                + "tt.name AS typeName, "
                + "tu.name AS unitMeasurementName, "
                + "ts.name AS stateName "
                + "FROM wastes w "
                + "INNER JOIN entities e ON e.id = w.entity_id "
                + "LEFT JOIN types tt ON tt.category='" + CATEGORIA_TIPO_RESIDUO + "' "
                + "AND tt.code = w.type "
                + "LEFT JOIN types tu ON tu.category='" + CATEGORIA_UNIDAD_MEDIDA + "' "
                + "AND tu.code = w.unit_measurement "
                + "LEFT JOIN types ts ON ts.category='" + CATEGORIA_ESTADO_RESIDUO + "' "
                + "AND ts.code = w.state "
                + "WHERE LOWER(tt.name) LIKE ? "
                + "OR LOWER(w.type) LIKE ? "
                + "ORDER BY w.id DESC";

        try (
                Connection con = ConexionSupabase.conectar(); PreparedStatement ps = con.prepareStatement(sql)) {

            String comodin = "%" + typeCode.toLowerCase() + "%";
            ps.setString(1, comodin);
            ps.setString(2, comodin);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }

        } catch (Exception e) {
            System.out.println("Error findByType: " + e.getMessage());
        }

        return list;
    }

    /**
     * Lista los tipos de residuo disponibles (types.category = 'TIPO_RESIDUO'),
     * para llenar combos.
     */
    public List<String[]> listTiposResiduo() {

        List<String[]> list = new ArrayList<>();

        String sql = """
            SELECT code, name
            FROM types
            WHERE category = ?
              AND status = true
            ORDER BY "order"
            """;

        try (
                Connection con = ConexionSupabase.conectar(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, CATEGORIA_TIPO_RESIDUO);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new String[]{rs.getString("code"), rs.getString("name")});
                }
            }

        } catch (Exception e) {
            System.out.println("Error listTiposResiduo: " + e.getMessage());
        }

        return list;
    }

    private Waste mapRow(ResultSet rs) throws SQLException {

        Waste w = new Waste();

        w.setId(rs.getLong("id"));
        w.setType(rs.getString("type"));
        w.setQuantity(rs.getDouble("quantity"));
        w.setUnitMeasurement(rs.getString("unit_measurement"));

        Date fecha = rs.getDate("waste_generation_date");
        if (fecha != null) {
            w.setWasteGenerationDate(fecha.toLocalDate());
        }

        w.setHasStorageLocation(rs.getBoolean("has_storage_location"));
        w.setState(rs.getString("state"));
        w.setStatus(rs.getBoolean("status"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            w.setCreatedAt(createdAt.toInstant().atOffset(java.time.ZoneOffset.UTC));
        }

        w.setCreatedBy((Long) rs.getObject("created_by"));

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            w.setUpdatedAt(updatedAt.toInstant().atOffset(java.time.ZoneOffset.UTC));
        }

        w.setUpdatedBy((Long) rs.getObject("updated_by"));
        w.setEntityId((Long) rs.getObject("entity_id"));
        w.setDangerousness(rs.getBoolean("dangerousness"));

        Timestamp publishAt = rs.getTimestamp("publish_at");
        if (publishAt != null) {
            w.setPublishAt(publishAt.toInstant().atOffset(java.time.ZoneOffset.UTC));
        }

        w.setEntityName(rs.getString("entityName"));

        String typeName = rs.getString("typeName");
        w.setTypeName(typeName != null ? typeName.toUpperCase() : "");

        String unitMeasurementName = rs.getString("unitMeasurementName");
        w.setUnitMeasurementName(unitMeasurementName != null
                ? unitMeasurementName.toUpperCase()
                : "");

        String stateName = rs.getString("stateName");
        w.setStateName(stateName != null
                ? stateName.toUpperCase()
                : "");

        return w;
    }
}

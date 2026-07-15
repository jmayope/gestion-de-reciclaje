package dao;

import conexion.ConexionSupabase;
import modelo.ProcessFlow;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProcessFlowDAO {

    public List<ProcessFlow> listProcessFlows() {

        List<ProcessFlow> list =
                new ArrayList<>();

        String sql =
                "SELECT * FROM process_flows ORDER BY id";

        try (
                Connection con =
                        ConexionSupabase.conectar();

                PreparedStatement ps =
                        con.prepareStatement(sql);

                ResultSet rs =
                        ps.executeQuery()
        ) {

            while (rs.next()) {

                ProcessFlow p =
                        new ProcessFlow();

                p.setId(
                        rs.getLong("id")
                );

                p.setWasteId(
                        (Long) rs.getObject("waste_id")
                );

                p.setPreviousProcessId(
                        rs.getString(
                                "previous_process_id"
                        )
                );

                p.setCurrentProcessId(
                        rs.getString(
                                "current_process_id"
                        )
                );

                p.setQuantity(
                        rs.getBigDecimal("quantity")
                );

                p.setLongitude(
                        rs.getBigDecimal("longitude")
                );

                p.setLatitude(
                        rs.getBigDecimal("latitude")
                );

                p.setCompleted(
                        rs.getBoolean("completed")
                );

                p.setStatus(
                        rs.getBoolean("status")
                );

                p.setEntityGeneratorId(
                        (Long) rs.getObject(
                                "entity_generator_id"
                        )
                );

                p.setEntityOperatorId(
                        (Long) rs.getObject(
                                "entity_operator_id"
                        )
                );

                list.add(p);
            }

        } catch (Exception e) {

            System.out.println(
                    "Error listProcessFlows: "
                            + e.getMessage()
            );
        }

        return list;
    }

    public boolean insertProcessFlow(ProcessFlow p) {

        String sql = """
                INSERT INTO process_flows
                (
                    waste_id,
                    previous_process_id,
                    current_process_id,
                    quantity,
                    longitude,
                    latitude,
                    completed,
                    status,
                    entity_generator_id,
                    entity_operator_id
                )
                VALUES (?,?,?,?,?,?,?,?,?,?)
                """;

        try (
                Connection con =
                        ConexionSupabase.conectar();

                PreparedStatement ps =
                        con.prepareStatement(sql)
        ) {

            ps.setObject(
                    1,
                    p.getWasteId()
            );

            ps.setString(
                    2,
                    p.getPreviousProcessId()
            );

            ps.setString(
                    3,
                    p.getCurrentProcessId()
            );

            ps.setBigDecimal(
                    4,
                    p.getQuantity()
            );

            ps.setBigDecimal(
                    5,
                    p.getLongitude()
            );

            ps.setBigDecimal(
                    6,
                    p.getLatitude()
            );

            ps.setBoolean(
                    7,
                    p.isCompleted()
            );

            ps.setBoolean(
                    8,
                    p.isStatus()
            );

            ps.setObject(
                    9,
                    p.getEntityGeneratorId()
            );

            ps.setObject(
                    10,
                    p.getEntityOperatorId()
            );

            return ps.executeUpdate() > 0;

        } catch (Exception e) {

            System.out.println(
                    "Error insertProcessFlow: "
                            + e.getMessage()
            );

            return false;
        }
    }

    public boolean updateProcessFlow(ProcessFlow p) {

        String sql = """
                UPDATE process_flows
                SET
                    waste_id=?,
                    previous_process_id=?,
                    current_process_id=?,
                    quantity=?,
                    longitude=?,
                    latitude=?,
                    completed=?,
                    status=?,
                    entity_generator_id=?,
                    entity_operator_id=?
                WHERE id=?
                """;

        try (
                Connection con =
                        ConexionSupabase.conectar();

                PreparedStatement ps =
                        con.prepareStatement(sql)
        ) {

            ps.setObject(1, p.getWasteId());

            ps.setString(2, p.getPreviousProcessId());

            ps.setString(3, p.getCurrentProcessId());

            ps.setBigDecimal(4, p.getQuantity());

            ps.setBigDecimal(5, p.getLongitude());

            ps.setBigDecimal(6, p.getLatitude());

            ps.setBoolean(7, p.isCompleted());

            ps.setBoolean(8, p.isStatus());

            ps.setObject(9, p.getEntityGeneratorId());

            ps.setObject(10, p.getEntityOperatorId());

            ps.setLong(11, p.getId());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {

            System.out.println(
                    "Error updateProcessFlow: "
                            + e.getMessage()
            );

            return false;
        }
    }

    public boolean deleteProcessFlow(long id) {

        String sql =
                "DELETE FROM process_flows WHERE id=?";

        try (
                Connection con =
                        ConexionSupabase.conectar();

                PreparedStatement ps =
                        con.prepareStatement(sql)
        ) {

            ps.setLong(1, id);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {

            System.out.println(
                    "Error deleteProcessFlow: "
                            + e.getMessage()
            );

            return false;
        }
    }

    public ProcessFlow findById(long id) {

        String sql =
                "SELECT * FROM process_flows WHERE id=?";

        try (
                Connection con =
                        ConexionSupabase.conectar();

                PreparedStatement ps =
                        con.prepareStatement(sql)
        ) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    ProcessFlow p =
                            new ProcessFlow();

                    p.setId(
                            rs.getLong("id")
                    );

                    p.setWasteId(
                            (Long) rs.getObject("waste_id")
                    );

                    p.setPreviousProcessId(
                            rs.getString(
                                    "previous_process_id"
                            )
                    );

                    p.setCurrentProcessId(
                            rs.getString(
                                    "current_process_id"
                            )
                    );

                    p.setQuantity(
                            rs.getBigDecimal("quantity")
                    );

                    p.setLongitude(
                            rs.getBigDecimal("longitude")
                    );

                    p.setLatitude(
                            rs.getBigDecimal("latitude")
                    );

                    p.setCompleted(
                            rs.getBoolean("completed")
                    );

                    p.setStatus(
                            rs.getBoolean("status")
                    );

                    p.setEntityGeneratorId(
                            (Long) rs.getObject(
                                    "entity_generator_id"
                            )
                    );

                    p.setEntityOperatorId(
                            (Long) rs.getObject(
                                    "entity_operator_id"
                            )
                    );

                    return p;
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
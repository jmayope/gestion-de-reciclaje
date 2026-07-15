package dao;

import conexion.ConexionSupabase;
import estructura.CacheRegistros;
import modelo.RegistroRecoleccion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * RegistroDAO optimizado.
 *
 * Estrategia:
 *  - La lista completa se obtiene de la BD solo cuando el cache está vacío
 *    o se invalida explícitamente → listarRegistros() pasa a ser O(n) local.
 *  - buscarPorId() resuelve en O(log n) desde el árbol BST del cache.
 *  - Cada mutación (insertar / actualizar / eliminar) actualiza el cache
 *    en lugar de recargarlo completo desde la BD.
 *  - Se reutiliza una sola conexión por operación (try-with-resources).
 */
public class RegistroDAO {

    private final CacheRegistros cache = CacheRegistros.getInstance();
    private boolean cacheInicializado = false;

    // ── SQL ──────────────────────────────────────────────────────────────────
    private static final String SQL_LISTAR =
        "SELECT id_registro, id_usuario, id_ubicacion, id_residuo, "
      + "cantidad, unidad, fecha, observaciones "
      + "FROM registros_recoleccion ORDER BY id_registro DESC";

    private static final String SQL_BUSCAR_ID =
        "SELECT id_registro, id_usuario, id_ubicacion, id_residuo, "
      + "cantidad, unidad, fecha, observaciones "
      + "FROM registros_recoleccion WHERE id_registro = ?";

    private static final String SQL_INSERTAR =
        "INSERT INTO registros_recoleccion "
      + "(id_usuario, id_ubicacion, id_residuo, cantidad, unidad, observaciones) "
      + "VALUES (?, ?, ?, ?, ?, ?) RETURNING id_registro";

    private static final String SQL_ACTUALIZAR =
        "UPDATE registros_recoleccion "
      + "SET id_usuario=?, id_ubicacion=?, id_residuo=?, "
      + "    cantidad=?, unidad=?, observaciones=? "
      + "WHERE id_registro=?";

    private static final String SQL_ELIMINAR =
        "DELETE FROM registros_recoleccion WHERE id_registro=?";

    private static final String SQL_RESTAURAR =
        "INSERT INTO registros_recoleccion "
      + "(id_registro, id_usuario, id_ubicacion, id_residuo, "
      + " cantidad, unidad, fecha, observaciones) "
      + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_CONTAR =
        "SELECT COUNT(*) AS total FROM registros_recoleccion";

    private static final String SQL_TOTAL_KG =
        "SELECT COALESCE(SUM(cantidad), 0) AS total_kg "
      + "FROM registros_recoleccion";

    // ═══════════════════════════════════════════════════════════════════════
    //  LECTURA
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Primera llamada: consulta la BD y llena el árbol BST del cache.
     * Llamadas siguientes: devuelve la lista desde el árbol (O(n) local).
     */
    public List<RegistroRecoleccion> listarRegistros() {
        if (!cacheInicializado) {
            recargarCacheDesdeDB();
        }
        return cache.listarRegistros();
    }

    /**
     * Búsqueda por ID en O(log n) gracias al árbol BST.
     * Solo consulta la BD si el registro no está en cache.
     */
    public RegistroRecoleccion buscarPorId(int id) {
        if (!cacheInicializado) {
            recargarCacheDesdeDB();
        }
        RegistroRecoleccion enCache = cache.buscarRegistro(id);
        if (enCache != null) return enCache;

        // Fallback a BD si por algún motivo no está en cache
        return buscarEnDB(id);
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  ESCRITURA  (actualizan cache + BD en la misma operación)
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Inserta en BD y, si tiene éxito, agrega al árbol BST del cache.
     * Usa RETURNING para obtener el id generado y hacer solo 1 round-trip.
     */
    public boolean insertarRegistro(RegistroRecoleccion r) {
        try (Connection con = ConexionSupabase.conectar();
             PreparedStatement ps = con.prepareStatement(SQL_INSERTAR)) {

            ps.setInt(1, r.getIdUsuario());
            ps.setInt(2, r.getIdUbicacion());
            ps.setInt(3, r.getIdResiduo());
            ps.setDouble(4, r.getCantidad());
            ps.setString(5, r.getUnidad());
            ps.setString(6, r.getObservaciones());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    r.setIdRegistro(rs.getInt("id_registro"));
                    cache.insertarRegistro(r);   // ← árbol BST
                    return true;
                }
            }

        } catch (Exception e) {
            System.err.println("Error insertarRegistro: " + e.getMessage());
        }
        return false;
    }

    /**
     * Actualiza en BD y refleja el cambio en el árbol BST (O(log n)).
     */
    public boolean actualizarRegistro(RegistroRecoleccion r) {
        try (Connection con = ConexionSupabase.conectar();
             PreparedStatement ps = con.prepareStatement(SQL_ACTUALIZAR)) {

            ps.setInt(1, r.getIdUsuario());
            ps.setInt(2, r.getIdUbicacion());
            ps.setInt(3, r.getIdResiduo());
            ps.setDouble(4, r.getCantidad());
            ps.setString(5, r.getUnidad());
            ps.setString(6, r.getObservaciones());
            ps.setInt(7, r.getIdRegistro());

            boolean ok = ps.executeUpdate() > 0;
            if (ok) cache.insertarRegistro(r);  // ← actualiza nodo en árbol BST
            return ok;

        } catch (Exception e) {
            System.err.println("Error actualizarRegistro: " + e.getMessage());
            return false;
        }
    }

    /**
     * Elimina de BD y del árbol BST del cache en O(log n).
     */
    public boolean eliminarRegistro(int id) {
        try (Connection con = ConexionSupabase.conectar();
             PreparedStatement ps = con.prepareStatement(SQL_ELIMINAR)) {

            ps.setInt(1, id);
            boolean ok = ps.executeUpdate() > 0;
            if (ok) cache.eliminarRegistro(id);  // ← árbol BST
            return ok;

        } catch (Exception e) {
            System.err.println("Error eliminarRegistro: " + e.getMessage());
            return false;
        }
    }

    /**
     * Restaura un registro previamente eliminado (operación UNDO).
     * Reinserta en BD con el id_registro original e inserta en árbol BST.
     */
    public boolean restaurarRegistro(RegistroRecoleccion r) {
        try (Connection con = ConexionSupabase.conectar();
             PreparedStatement ps = con.prepareStatement(SQL_RESTAURAR)) {

            ps.setInt(1, r.getIdRegistro());
            ps.setInt(2, r.getIdUsuario());
            ps.setInt(3, r.getIdUbicacion());
            ps.setInt(4, r.getIdResiduo());
            ps.setDouble(5, r.getCantidad());
            ps.setString(6, r.getUnidad());

            if (r.getFecha() != null)
                ps.setTimestamp(7, Timestamp.valueOf(r.getFecha()));
            else
                ps.setNull(7, Types.TIMESTAMP);

            ps.setString(8, r.getObservaciones());

            boolean ok = ps.executeUpdate() > 0;
            if (ok) cache.insertarRegistro(r);   // ← árbol BST
            return ok;

        } catch (Exception e) {
            System.err.println("Error restaurarRegistro: " + e.getMessage());
            return false;
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  ESTADÍSTICAS  (siempre desde BD para precisión)
    // ═══════════════════════════════════════════════════════════════════════

    public int contarRegistros() {
        try (Connection con = ConexionSupabase.conectar();
             PreparedStatement ps = con.prepareStatement(SQL_CONTAR);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) return rs.getInt("total");

        } catch (Exception e) {
            System.err.println("Error contarRegistros: " + e.getMessage());
        }
        return 0;
    }

    public double totalKgRecolectados() {
        try (Connection con = ConexionSupabase.conectar();
             PreparedStatement ps = con.prepareStatement(SQL_TOTAL_KG);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) return rs.getDouble("total_kg");

        } catch (Exception e) {
            System.err.println("Error totalKgRecolectados: " + e.getMessage());
        }
        return 0;
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  HELPERS PRIVADOS
    // ═══════════════════════════════════════════════════════════════════════

    /** Recarga completa desde BD y reconstruye el árbol BST del cache. */
    public void recargarCacheDesdeDB() {
        List<RegistroRecoleccion> lista = new ArrayList<>();

        try (Connection con = ConexionSupabase.conectar();
             PreparedStatement ps = con.prepareStatement(SQL_LISTAR);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) lista.add(mapearFila(rs));

        } catch (Exception e) {
            System.err.println("Error recargarCache: " + e.getMessage());
        }

        cache.cargarRegistros(lista);
        cacheInicializado = true;
    }

    private RegistroRecoleccion buscarEnDB(int id) {
        try (Connection con = ConexionSupabase.conectar();
             PreparedStatement ps = con.prepareStatement(SQL_BUSCAR_ID)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    RegistroRecoleccion r = mapearFila(rs);
                    cache.insertarRegistro(r); // insertar en árbol para la próxima vez
                    return r;
                }
            }

        } catch (Exception e) {
            System.err.println("Error buscarEnDB: " + e.getMessage());
        }
        return null;
    }

    private RegistroRecoleccion mapearFila(ResultSet rs) throws SQLException {
        RegistroRecoleccion r = new RegistroRecoleccion();
        r.setIdRegistro(rs.getInt("id_registro"));
        r.setIdUsuario(rs.getInt("id_usuario"));
        r.setIdUbicacion(rs.getInt("id_ubicacion"));
        r.setIdResiduo(rs.getInt("id_residuo"));
        r.setCantidad(rs.getDouble("cantidad"));
        r.setUnidad(rs.getString("unidad"));
        Timestamp ts = rs.getTimestamp("fecha");
        if (ts != null) r.setFecha(ts.toLocalDateTime());
        r.setObservaciones(rs.getString("observaciones"));
        return r;
    }
}

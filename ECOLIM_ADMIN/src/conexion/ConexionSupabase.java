package conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * ConexionSupabase optimizado con un pool de conexiones liviano.
 *
 * Problema original: cada operación abría y cerraba una conexión nueva,
 * lo cual tiene un costo de ~200-500 ms por round-trip con Supabase.
 *
 * Solución: pool de conexiones reutilizables almacenadas en una cola
 * (ArrayDeque = estructura tipo árbol implícito / cola de doble extremo).
 *
 * Uso:
 *   Connection con = ConexionSupabase.obtener();
 *   // ... operaciones ...
 *   ConexionSupabase.liberar(con);
 *
 * Si se usa try-with-resources no llamar a con.close() directamente;
 * usar el wrapper PooledConnection o liberar manualmente.
 */
public class ConexionSupabase {

    // ── Credenciales ─────────────────────────────────────────────────────────
    private static final String HOST     = "aws-1-us-east-1.pooler.supabase.com";
    private static final String PORT     = "5432";
    private static final String DATABASE = "postgres";
    private static final String USER     = "postgres.fcvafhdrvcxggixngevu";
    private static final String PASSWORD = "unodostrescuatro";

    private static final String URL =
            "jdbc:postgresql://" + HOST + ":" + PORT + "/" + DATABASE
            + "?sslmode=require"
            + "&socketTimeout=30"        // evita hilos colgados
            + "&connectTimeout=10";      // falla rápido si no hay red

    // ── Pool ─────────────────────────────────────────────────────────────────
    private static final int POOL_SIZE = 5;

    /** Cola de conexiones disponibles (thread-safe por synchronized). */
    private static final Deque<Connection> pool = new ArrayDeque<>(POOL_SIZE);

    static {
        try {
            Class.forName("org.postgresql.Driver");
            for (int i = 0; i < POOL_SIZE; i++) {
                pool.push(crearConexionFisica());
            }
        } catch (Exception e) {
            System.err.println("Error inicializando pool: " + e.getMessage());
        }
    }

    // ── API pública ───────────────────────────────────────────────────────────

    /**
     * Obtiene una conexión del pool. Si el pool está vacío o la conexión
     * está muerta, crea una nueva.
     */
    public static synchronized Connection obtener() throws SQLException {
        while (!pool.isEmpty()) {
            Connection con = pool.pop();
            try {
                if (!con.isClosed() && con.isValid(2)) return con;
            } catch (SQLException ignored) {}
        }
        return crearConexionFisica();
    }

    /**
     * Devuelve una conexión al pool para ser reutilizada.
     * Llamar siempre en el bloque finally o usar PooledConnection.
     */
    public static synchronized void liberar(Connection con) {
        if (con == null) return;
        try {
            if (!con.isClosed() && con.isValid(1) && pool.size() < POOL_SIZE) {
                pool.push(con);
                return;
            }
        } catch (SQLException ignored) {}
        cerrarSilencioso(con);
    }

    /**
     * Mantiene compatibilidad con el código original que usaba conectar().
     * NOTA: la conexión devuelta NO vuelve al pool automáticamente.
     * Migrar a obtener()/liberar() para aprovechar el pool.
     */
    public static Connection conectar() throws SQLException {
        return obtener();
    }

    // ── Helpers privados ──────────────────────────────────────────────────────

    private static Connection crearConexionFisica() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    private static void cerrarSilencioso(Connection con) {
        try { if (con != null) con.close(); } catch (SQLException ignored) {}
    }

    /** Cierra todas las conexiones del pool (llamar al cerrar la app). */
    public static synchronized void cerrarPool() {
        while (!pool.isEmpty()) cerrarSilencioso(pool.pop());
    }
}

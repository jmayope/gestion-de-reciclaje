package estructura;

import modelo.RegistroRecoleccion;
import modelo.Usuario;
import modelo.Ubicacion;

import java.util.List;
import java.util.TreeMap;

/**
 * Cache centralizado que mantiene índices en memoria usando árboles
 * (ArbolBST propio + TreeMap de Java) para búsquedas/accesos en O(log n).
 *
 * Responsabilidades:
 *  - Índice de registros  por id_registro  → O(log n) exacto
 *  - Índice de usuarios   por id_usuario   → O(log n) exacto
 *  - Índice de ubicaciones por id_ubicacion → O(log n) exacto
 *  - Índice inverso nombre→id de ubicaciones para el formulario
 *
 * La BD se consulta UNA sola vez al iniciar o al invalidar el cache.
 */
public class CacheRegistros {

    // ── Índices de registros ─────────────────────────────────────────────────
    /** BST propio: clave = id_registro, valor = RegistroRecoleccion */
    private final ArbolBST<Integer, RegistroRecoleccion> arbolRegistros
            = new ArbolBST<>();

    // ── Índices auxiliares (Java TreeMap = árbol rojo-negro) ─────────────────
    /** id_usuario  → nombre completo */
    private final TreeMap<Integer, String> mapaUsuarios   = new TreeMap<>();

    /** id_ubicacion → nombre del lugar */
    private final TreeMap<Integer, String> mapaUbicaciones = new TreeMap<>();

    /** nombre del lugar → id_ubicacion  (inverso para formularios) */
    private final TreeMap<String, Integer> mapaUbicacionesInv = new TreeMap<>();

    // ── Singleton / instancia única por sesión ───────────────────────────────
    private static CacheRegistros instancia;

    private CacheRegistros() {}

    public static CacheRegistros getInstance() {
        if (instancia == null) instancia = new CacheRegistros();
        return instancia;
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  CARGA / INVALIDACIÓN
    // ═══════════════════════════════════════════════════════════════════════

    /** Carga (o recarga) todos los registros desde la lista dada. */
    public void cargarRegistros(List<RegistroRecoleccion> lista) {
        arbolRegistros.limpiar();
        for (RegistroRecoleccion r : lista)
            arbolRegistros.insertar(r.getIdRegistro(), r);
    }

    /** Carga (o recarga) el mapa de usuarios. */
    public void cargarUsuarios(List<Usuario> lista) {
        mapaUsuarios.clear();
        for (Usuario u : lista)
            mapaUsuarios.put(u.getIdUsuario(),
                             u.getNombre() + " " + u.getApellido());
    }

    /** Carga (o recarga) el mapa de ubicaciones (directo e inverso). */
    public void cargarUbicaciones(List<Ubicacion> lista) {
        mapaUbicaciones.clear();
        mapaUbicacionesInv.clear();
        for (Ubicacion u : lista) {
            mapaUbicaciones.put(u.getIdUbicacion(), u.getNombreLugar());
            mapaUbicacionesInv.put(u.getNombreLugar(), u.getIdUbicacion());
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  OPERACIONES SOBRE REGISTROS  (O log n)
    // ═══════════════════════════════════════════════════════════════════════

    /** Inserta o actualiza un registro en el árbol. */
    public void insertarRegistro(RegistroRecoleccion r) {
        arbolRegistros.insertar(r.getIdRegistro(), r);
    }

    /** Elimina un registro del árbol por su id. */
    public void eliminarRegistro(int id) {
        arbolRegistros.eliminar(id);
    }

    /**
     * Busca un registro exacto por id.  O(log n).
     * Evita hacer SELECT a la BD si ya está en cache.
     */
    public RegistroRecoleccion buscarRegistro(int id) {
        return arbolRegistros.buscar(id);
    }

    /** Devuelve todos los registros en orden ascendente de id. */
    public List<RegistroRecoleccion> listarRegistros() {
        return arbolRegistros.enOrden();
    }

    /**
     * Filtra registros por contenido de texto en un campo calculado.
     * Aplica la función de extracción y compara con el texto buscado.
     * O(n) — pero sobre datos ya en memoria, sin tocar la BD.
     */
    public List<RegistroRecoleccion> filtrarPorTexto(
            String campo, String texto) {

        String t = texto.toLowerCase();
        List<RegistroRecoleccion> todos = arbolRegistros.enOrden();
        List<RegistroRecoleccion> resultado = new java.util.ArrayList<>();

        for (RegistroRecoleccion r : todos) {
            String valor = "";
            switch (campo) {
                case "ID":       valor = String.valueOf(r.getIdRegistro()); break;
                case "Usuario":  valor = nombreUsuario(r.getIdUsuario());   break;
                case "Ubicación":valor = nombreUbicacion(r.getIdUbicacion()); break;
                case "Residuo":  valor = nombreResiduo(r.getIdResiduo()).toUpperCase();   break;
                case "Unidad":   valor = r.getUnidad() != null
                                             ? r.getUnidad() : "";          break;
            }
            if (valor.toLowerCase().contains(t))
                resultado.add(r);
        }
        return resultado;
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  RESOLUCIÓN DE IDs → NOMBRES  (O log n via TreeMap)
    // ═══════════════════════════════════════════════════════════════════════

    public String nombreUsuario(int id) {
        return mapaUsuarios.getOrDefault(id, "Desconocido");
    }

    public String nombreUbicacion(int id) {
        return mapaUbicaciones.getOrDefault(id, "Desconocida");
    }

    public int idUbicacion(String nombre) {
        return mapaUbicacionesInv.getOrDefault(nombre, 0);
    }

    public String[] nombresUbicaciones() {
        return mapaUbicaciones.values().toArray(new String[0]);
    }

    /**
     * Resolución de residuo: centralizada aquí para que sea fácil extender
     * si en el futuro viene de BD en lugar de estar hardcodeada.
     */
    public static String nombreResiduo(int id) {
        switch (id) {
            case 1: return "Sólido";
            case 2: return "Líquido";
            case 3: return "Gaseoso";
            case 4: return "Metálico";
            default: return "Desconocido";
        }
    }

    public static int idResiduo(String nombre) {
        switch (nombre) {
            case "Sólido":   return 1;
            case "Líquido":  return 2;
            case "Gaseoso":  return 3;
            case "Metálico": return 4;
            default:         return 0;
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  INVALIDACIÓN TOTAL
    // ═══════════════════════════════════════════════════════════════════════

    public void invalidar() {
        arbolRegistros.limpiar();
        mapaUsuarios.clear();
        mapaUbicaciones.clear();
        mapaUbicacionesInv.clear();
    }
}

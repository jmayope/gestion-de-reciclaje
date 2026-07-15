package estructura;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Árbol Binario de Búsqueda (BST) genérico.
 * Permite insertar, buscar, eliminar y recorrer nodos en O(log n) promedio.
 *
 * @param <K> Tipo de la clave de comparación (debe ser Comparable)
 * @param <V> Tipo del valor almacenado
 */
public class ArbolBST<K extends Comparable<K>, V> {

    // ── Nodo interno ────────────────────────────────────────────────────────
    private static class Nodo<K, V> {
        K clave;
        V valor;
        Nodo<K, V> izquierdo, derecho;

        Nodo(K clave, V valor) {
            this.clave = clave;
            this.valor = valor;
        }
    }

    // ── Estado ──────────────────────────────────────────────────────────────
    private Nodo<K, V> raiz;
    private int tamanio;

    // ── Insertar / actualizar ────────────────────────────────────────────────
    public void insertar(K clave, V valor) {
        raiz = insertarRec(raiz, clave, valor);
    }

    private Nodo<K, V> insertarRec(Nodo<K, V> nodo, K clave, V valor) {
        if (nodo == null) {
            tamanio++;
            return new Nodo<>(clave, valor);
        }
        int cmp = clave.compareTo(nodo.clave);
        if      (cmp < 0) nodo.izquierdo = insertarRec(nodo.izquierdo, clave, valor);
        else if (cmp > 0) nodo.derecho   = insertarRec(nodo.derecho,   clave, valor);
        else              nodo.valor = valor; // actualizar si ya existe
        return nodo;
    }

    // ── Buscar exacto ────────────────────────────────────────────────────────
    public V buscar(K clave) {
        Nodo<K, V> nodo = buscarRec(raiz, clave);
        return nodo != null ? nodo.valor : null;
    }

    private Nodo<K, V> buscarRec(Nodo<K, V> nodo, K clave) {
        if (nodo == null) return null;
        int cmp = clave.compareTo(nodo.clave);
        if      (cmp < 0) return buscarRec(nodo.izquierdo, clave);
        else if (cmp > 0) return buscarRec(nodo.derecho,   clave);
        else              return nodo;
    }

    // ── Buscar por prefijo / contiene (recorre todo el árbol) ────────────────
    /**
     * Devuelve todos los valores cuya clave (convertida a String) contiene
     * el texto dado (sin distinción de mayúsculas). O(n) — útil para filtros.
     */
    public List<V> buscarPorContenido(String texto, Function<K, String> claveAString) {
        List<V> resultado = new ArrayList<>();
        buscarContenidoRec(raiz, texto.toLowerCase(), claveAString, resultado);
        return resultado;
    }

    private void buscarContenidoRec(Nodo<K, V> nodo, String texto,
                                    Function<K, String> fn, List<V> resultado) {
        if (nodo == null) return;
        if (fn.apply(nodo.clave).toLowerCase().contains(texto))
            resultado.add(nodo.valor);
        buscarContenidoRec(nodo.izquierdo, texto, fn, resultado);
        buscarContenidoRec(nodo.derecho,   texto, fn, resultado);
    }

    // ── Eliminar ─────────────────────────────────────────────────────────────
    public void eliminar(K clave) {
        raiz = eliminarRec(raiz, clave);
    }

    private Nodo<K, V> eliminarRec(Nodo<K, V> nodo, K clave) {
        if (nodo == null) return null;
        int cmp = clave.compareTo(nodo.clave);
        if (cmp < 0) {
            nodo.izquierdo = eliminarRec(nodo.izquierdo, clave);
        } else if (cmp > 0) {
            nodo.derecho = eliminarRec(nodo.derecho, clave);
        } else {
            tamanio--;
            if (nodo.izquierdo == null) return nodo.derecho;
            if (nodo.derecho   == null) return nodo.izquierdo;
            // Sucesor in-order: mínimo del subárbol derecho
            Nodo<K, V> sucesor = minimoRec(nodo.derecho);
            nodo.clave  = sucesor.clave;
            nodo.valor  = sucesor.valor;
            nodo.derecho = eliminarRec(nodo.derecho, sucesor.clave);
        }
        return nodo;
    }

    private Nodo<K, V> minimoRec(Nodo<K, V> nodo) {
        return nodo.izquierdo == null ? nodo : minimoRec(nodo.izquierdo);
    }

    // ── Recorrido in-order (ordenado) ────────────────────────────────────────
    public List<V> enOrden() {
        List<V> lista = new ArrayList<>();
        enOrdenRec(raiz, lista);
        return lista;
    }

    private void enOrdenRec(Nodo<K, V> nodo, List<V> lista) {
        if (nodo == null) return;
        enOrdenRec(nodo.izquierdo, lista);
        lista.add(nodo.valor);
        enOrdenRec(nodo.derecho, lista);
    }

    // ── Utilidades ───────────────────────────────────────────────────────────
    public boolean contiene(K clave)  { return buscar(clave) != null; }
    public int     tamanio()          { return tamanio; }
    public boolean estaVacio()        { return raiz == null; }

    public void limpiar() {
        raiz     = null;
        tamanio  = 0;
    }
}

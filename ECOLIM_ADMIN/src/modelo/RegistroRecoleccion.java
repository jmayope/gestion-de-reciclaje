package modelo;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Modelo optimizado con equals/hashCode basados en idRegistro
 * y toString legible para depuración.
 */
public class RegistroRecoleccion {

    private int           idRegistro;
    private int           idUsuario;
    private int           idUbicacion;
    private int           idResiduo;
    private double        cantidad;
    private String        unidad;
    private LocalDateTime fecha;
    private String        observaciones;

    // ── Constructores ────────────────────────────────────────────────────────
    public RegistroRecoleccion() {}

    public RegistroRecoleccion(int idRegistro, int idUsuario, int idUbicacion,
                               int idResiduo, double cantidad, String unidad,
                               LocalDateTime fecha, String observaciones) {
        this.idRegistro    = idRegistro;
        this.idUsuario     = idUsuario;
        this.idUbicacion   = idUbicacion;
        this.idResiduo     = idResiduo;
        this.cantidad      = cantidad;
        this.unidad        = unidad;
        this.fecha         = fecha;
        this.observaciones = observaciones;
    }

    // ── Getters / Setters ────────────────────────────────────────────────────
    public int           getIdRegistro()    { return idRegistro; }
    public void          setIdRegistro(int v)   { this.idRegistro = v; }

    public int           getIdUsuario()     { return idUsuario; }
    public void          setIdUsuario(int v)    { this.idUsuario = v; }

    public int           getIdUbicacion()   { return idUbicacion; }
    public void          setIdUbicacion(int v)  { this.idUbicacion = v; }

    public int           getIdResiduo()     { return idResiduo; }
    public void          setIdResiduo(int v)    { this.idResiduo = v; }

    public double        getCantidad()      { return cantidad; }
    public void          setCantidad(double v)  { this.cantidad = v; }

    public String        getUnidad()        { return unidad; }
    public void          setUnidad(String v)    { this.unidad = v; }

    public LocalDateTime getFecha()         { return fecha; }
    public void          setFecha(LocalDateTime v) { this.fecha = v; }

    public String        getObservaciones() { return observaciones; }
    public void          setObservaciones(String v) { this.observaciones = v; }

    // ── equals / hashCode (basados en clave de negocio: idRegistro) ──────────
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RegistroRecoleccion)) return false;
        return idRegistro == ((RegistroRecoleccion) o).idRegistro;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idRegistro);
    }

    // ── toString ─────────────────────────────────────────────────────────────
    @Override
    public String toString() {
        return "RegistroRecoleccion{"
             + "id=" + idRegistro
             + ", usuario=" + idUsuario
             + ", ubicacion=" + idUbicacion
             + ", residuo=" + idResiduo
             + ", cantidad=" + cantidad
             + ", unidad='" + unidad + '\''
             + ", fecha=" + fecha
             + '}';
    }
}

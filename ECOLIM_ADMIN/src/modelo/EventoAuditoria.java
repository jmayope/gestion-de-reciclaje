package modelo;

import java.time.LocalDateTime;

public class EventoAuditoria {

    private String accion;
    private LocalDateTime fecha;

    public EventoAuditoria(String accion) {
        this.accion = accion;
        this.fecha = LocalDateTime.now();
    }

    public String getAccion() {
        return accion;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    @Override
    public String toString() {
        return fecha + " - " + accion;
    }
}
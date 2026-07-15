package util;

import modelo.EventoAuditoria;
import java.util.LinkedList;
import java.util.Queue;

public class AuditoriaManager {

    private static final Queue<EventoAuditoria> cola =
            new LinkedList<>();

    public static void registrar(String accion) {

        cola.offer(
                new EventoAuditoria(accion)
        );
    }

    public static Queue<EventoAuditoria> getEventos() {
        return cola;
    }
}
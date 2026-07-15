package estructuras;

import java.util.ArrayList;
import java.util.List;

public class NodoEmpresa {

    private String nombre;
    private List<NodoEmpresa> hijos;

    public NodoEmpresa(String nombre) {

        this.nombre = nombre;
        this.hijos = new ArrayList<>();
    }

    public void agregarHijo(NodoEmpresa hijo) {
        hijos.add(hijo);
    }

    public String getNombre() {
        return nombre;
    }

    public List<NodoEmpresa> getHijos() {
        return hijos;
    }
}
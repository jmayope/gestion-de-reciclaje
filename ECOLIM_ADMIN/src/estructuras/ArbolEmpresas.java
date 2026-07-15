package estructuras;

public class ArbolEmpresas {

    private NodoEmpresa raiz;

    public ArbolEmpresas(String nombreRaiz) {
        raiz = new NodoEmpresa(nombreRaiz);
    }

    public NodoEmpresa getRaiz() {
        return raiz;
    }

    public void recorrerPreOrden(
            NodoEmpresa nodo,
            StringBuilder sb) {

        if (nodo == null) {
            return;
        }

        sb.append(nodo.getNombre())
          .append("\n");

        for (NodoEmpresa hijo : nodo.getHijos()) {
            recorrerPreOrden(hijo, sb);
        }
    }
}
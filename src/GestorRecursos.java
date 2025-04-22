package src;

import java.util.ArrayList;
import java.util.List;

public class GestorRecursos {
    private List<RecursoDigital> recursos;

    public GestorRecursos() {
        this.recursos = new ArrayList<>();
    }

    public void agregarRecurso(RecursoDigital recurso) {
        this.recursos.add(recurso);
    }

    public RecursoDigital obtenerRecurso(String id) {
        for (RecursoDigital recurso : recursos) {
            if (recurso.getId().equals(id)) {
                return recurso;
            }
        }
        return null; // Retorna null si no se encuentra el recurso
    }

    public List<RecursoDigital> getRecursos() {
        return recursos;
    }

    // Métodos de búsqueda más avanzados se añadirán en otros Issues
}
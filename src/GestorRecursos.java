package src;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        return null;
    }

    public List<RecursoDigital> buscarRecursosPorTitulo(String titulo) {
        return recursos.stream()
                .filter(recurso -> recurso.getTitulo().toLowerCase().contains(titulo.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<RecursoDigital> getRecursos() {
        return recursos;
    }

    // Métodos para ordenamiento se añadirán después
}
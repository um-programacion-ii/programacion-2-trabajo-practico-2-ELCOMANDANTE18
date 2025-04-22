package src;

import java.util.ArrayList;
import java.util.Comparator;
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

    public List<RecursoDigital> buscarRecursosPorCategoria(CategoriaRecurso categoria) {
        return recursos.stream()
                .filter(recurso -> recurso.getCategoria() == categoria)
                .collect(Collectors.toList());
    }

    public List<RecursoDigital> getRecursos() {
        return recursos;
    }

    public void ordenarRecursos(Comparator<RecursoDigital> comparador) {
        recursos.sort(comparador);
    }

    public static Comparator<RecursoDigital> compararPorId() {
        return Comparator.comparing(RecursoDigital::getId);
    }

    public static Comparator<RecursoDigital> compararPorTitulo() {
        return Comparator.comparing(RecursoDigital::getTitulo);
    }
}
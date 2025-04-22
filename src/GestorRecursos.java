package src;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class GestorRecursos {
    private List<RecursoDigital> recursos = new ArrayList<>();

    public void agregarRecurso(RecursoDigital recurso) {
        recursos.add(recurso);
    }

    public RecursoDigital obtenerRecurso(String id) throws RecursoNoDisponibleException {
        for (RecursoDigital recurso : recursos) {
            if (recurso.getId().equals(id)) {
                return recurso;
            }
        }
        throw new RecursoNoDisponibleException("No se encontró ningún recurso con el ID: " + id);
    }

    public List<RecursoDigital> buscarRecursosPorTitulo(String titulo) {
        return recursos.stream()
                .filter(r -> r.getTitulo().toLowerCase().contains(titulo.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<RecursoDigital> buscarRecursosPorCategoria(CategoriaRecurso categoria) {
        return recursos.stream()
                .filter(r -> r.getCategoria() == categoria)
                .collect(Collectors.toList());
    }

    public void ordenarRecursos(Comparator<RecursoDigital> comparator) {
        recursos.sort(comparator);
    }

    public static Comparator<RecursoDigital> compararPorId() {
        return Comparator.comparing(RecursoDigital::getId);
    }

    public static Comparator<RecursoDigital> compararPorTitulo() {
        return Comparator.comparing(RecursoDigital::getTitulo);
    }

    public List<RecursoDigital> getRecursos() {
        return recursos;
    }
}
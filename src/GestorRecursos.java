package src;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

public class GestorRecursos {
    private List<RecursoDigital> recursos = new ArrayList<>();
    private List<Prestamo> prestamosActivos = new ArrayList<>(); // Para rastrear los préstamos activos

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

    // Métodos para préstamos
    public synchronized void prestar(RecursoDigital recurso, Usuario usuario) {
        if (recurso.getEstado() == EstadoRecurso.DISPONIBLE) {
            Prestamo nuevoPrestamo = new Prestamo(recurso, usuario, LocalDateTime.now());
            prestamosActivos.add(nuevoPrestamo);
            recurso.setEstado(EstadoRecurso.PRESTADO);
            System.out.println("Recurso con ID " + recurso.getId() + " prestado a " + usuario.getNombre() + " el " + nuevoPrestamo.getFechaPrestamo());
            // Aquí podríamos añadir una notificación
        } else {
            System.out.println("El recurso con ID " + recurso.getId() + " no está disponible para préstamo.");
        }
    }

    public synchronized void devolver(RecursoDigital recurso, Usuario usuario) {
        Prestamo prestamoActivo = null;
        for (Prestamo prestamo : prestamosActivos) {
            if (prestamo.getRecurso().equals(recurso) && prestamo.getUsuario().equals(usuario) && prestamo.getFechaDevolucion() == null) {
                prestamoActivo = prestamo;
                break;
            }
        }

        if (prestamoActivo != null) {
            prestamoActivo.setFechaDevolucion(LocalDateTime.now());
            recurso.setEstado(EstadoRecurso.DISPONIBLE);
            System.out.println("Recurso con ID " + recurso.getId() + " devuelto por " + usuario.getNombre() + " el " + prestamoActivo.getFechaDevolucion());
            // Aquí podríamos añadir una notificación
        } else {
            System.out.println("No se encontró un préstamo activo para el recurso con ID " + recurso.getId() + " y el usuario " + usuario.getNombre() + ".");
        }
    }
}
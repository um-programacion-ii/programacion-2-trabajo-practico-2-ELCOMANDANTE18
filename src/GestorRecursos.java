package src;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

public class GestorRecursos {
    private Map<String, RecursoDigital> recursos = new HashMap<>();
    private Map<String, Queue<Reserva>> reservas = new HashMap<>(); // Mapa de colas de reserva por ID de recurso
    private List<Prestamo> prestamosActivos = new ArrayList<>(); // Para rastrear los préstamos activos

    public void agregarRecurso(RecursoDigital recurso) {
        this.recursos.put(recurso.getId(), recurso);
        this.reservas.put(recurso.getId(), new LinkedList<>()); // Inicializar cola de reserva para el recurso
    }

    public RecursoDigital obtenerRecurso(String id) throws RecursoNoDisponibleException {
        RecursoDigital recurso = recursos.get(id);
        if (recurso == null) {
            throw new RecursoNoDisponibleException("No se encontró ningún recurso con el ID: " + id);
        }
        return recurso;
    }

    public List<RecursoDigital> buscarRecursosPorTitulo(String titulo) {
        return recursos.values().stream()
                .filter(r -> r.getTitulo().toLowerCase().contains(titulo.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<RecursoDigital> buscarRecursosPorCategoria(CategoriaRecurso categoria) {
        return recursos.values().stream()
                .filter(r -> r.getCategoria() == categoria)
                .collect(Collectors.toList());
    }

    public void ordenarRecursos(Comparator<RecursoDigital> comparator) {
        List<RecursoDigital> listaRecursos = new ArrayList<>(recursos.values());
        listaRecursos.sort(comparator);
        // Si necesitas actualizar el mapa 'recursos' con el orden, necesitarías una lógica más compleja
        // Ya que los Maps no garantizan un orden específico. Para mantener el orden, podrías usar
        // un LinkedHashMap si el orden de inserción es importante, o almacenar los IDs ordenados aparte.
        // Por ahora, la ordenación solo afecta a la lista local 'listaRecursos'.
    }

    public static Comparator<RecursoDigital> compararPorId() {
        return Comparator.comparing(RecursoDigital::getId);
    }

    public static Comparator<RecursoDigital> compararPorTitulo() {
        return Comparator.comparing(RecursoDigital::getTitulo);
    }

    public List<RecursoDigital> getRecursos() {
        return new ArrayList<>(recursos.values());
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

    public void reservar(RecursoDigital recurso, Usuario usuario) {
        if (recurso.getEstado() == EstadoRecurso.DISPONIBLE || recurso.getEstado() == EstadoRecurso.PRESTADO) {
            Reserva nuevaReserva = new Reserva(recurso, usuario, LocalDateTime.now());
            Queue<Reserva> colaReservas = reservas.get(recurso.getId());
            if (colaReservas != null && !colaReservas.contains(nuevaReserva)) {
                colaReservas.offer(nuevaReserva);
                recurso.setEstado(EstadoRecurso.RESERVADO);
                System.out.println("Recurso '" + recurso.getTitulo() + "' reservado por " + usuario.getNombre());
            } else if (colaReservas == null) {
                System.out.println("Error: No se encontró la cola de reservas para el recurso: " + recurso.getId());
            } else {
                System.out.println("El usuario '" + usuario.getNombre() + "' ya tiene una reserva para el recurso '" + recurso.getTitulo() + "'.");
            }
        } else if (recurso.getEstado() == EstadoRecurso.RESERVADO && !reservas.get(recurso.getId()).contains(new Reserva(recurso, usuario, null))) {
            reservas.get(recurso.getId()).offer(new Reserva(recurso, usuario, LocalDateTime.now()));
            System.out.println("Reserva adicional para '" + recurso.getTitulo() + "' realizada por " + usuario.getNombre());
        }
        else {
            System.out.println("El recurso '" + recurso.getTitulo() + "' no se puede reservar en su estado actual: " + recurso.getEstado());
        }
    }

    public void cancelarReserva(RecursoDigital recurso, Usuario usuario) {
        Queue<Reserva> colaReservas = reservas.get(recurso.getId());
        if (colaReservas != null) {
            Reserva reservaACancelar = new Reserva(recurso, usuario, null);
            boolean cancelada = colaReservas.remove(reservaACancelar);
            if (cancelada) {
                System.out.println("Reserva del recurso '" + recurso.getTitulo() + "' cancelada por " + usuario.getNombre());
                if (colaReservas.isEmpty() && recurso.getEstado() != EstadoRecurso.PRESTADO) {
                    recurso.setEstado(EstadoRecurso.DISPONIBLE);
                    System.out.println("El recurso '" + recurso.getTitulo() + "' vuelve a estar disponible.");
                }
            } else {
                System.out.println("No se encontró una reserva para el recurso '" + recurso.getTitulo() + "' del usuario '" + usuario.getNombre() + "'.");
            }
        } else {
            System.out.println("Error: No se encontró la cola de reservas para el recurso: " + recurso.getId());
        }
    }
}
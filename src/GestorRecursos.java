package src;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.LinkedList;
import java.util.stream.Collectors;

public class GestorRecursos {
    private List<RecursoDigital> recursos;
    private Map<String, Queue<Reserva>> reservas;

    public GestorRecursos() {
        this.recursos = new ArrayList<>();
        this.reservas = new HashMap<>();
    }

    public void agregarRecurso(RecursoDigital recurso) {
        recursos.add(recurso);
    }

    public RecursoDigital obtenerRecurso(String id) throws RecursoNoDisponibleException {
        for (RecursoDigital recurso : recursos) {
            if (recurso.getId().equals(id)) {
                return recurso;
            }
        }
        throw new RecursoNoDisponibleException("No se encontró el recurso con ID: " + id);
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

    public void prestar(Prestable recurso, Usuario usuario) {
        if (recurso.isDisponible()) {
            recurso.prestar(usuario);
        } else {
            System.out.println("El recurso " + ((RecursoDigital) recurso).getTitulo() + " no está disponible.");
        }
    }

    public void devolver(RecursoDigital recurso, Usuario usuario) {
        if (recurso instanceof Prestable) {
            ((Prestable) recurso).devolver(usuario);
            // Notificar al siguiente usuario en la cola de reserva si existe
            Queue<Reserva> colaReservas = reservas.get(recurso.getId());
            if (colaReservas != null && !colaReservas.isEmpty()) {
                Reserva siguienteReserva = colaReservas.poll();
                Usuario usuarioAReservar = siguienteReserva.getUsuario();
                if (recurso instanceof Prestable) {
                    ((Prestable) recurso).prestar(usuarioAReservar);
                    recurso.notificarDisponibilidad(usuarioAReservar);
                    System.out.println("El recurso " + recurso.getTitulo() + " ha sido prestado al usuario " + usuarioAReservar.getNombre() + " (ID: " + usuarioAReservar.getId() + ").");
                }
            }
        } else {
            System.out.println("El recurso " + recurso.getTitulo() + " no se puede prestar.");
        }
    }

    public void reservar(RecursoDigital recurso, Usuario usuario) {
        if (recurso instanceof Reservable) {
            if (((Reservable) recurso).estaDisponibleParaReserva(usuario)) {
                String recursoId = recurso.getId();
                if (!reservas.containsKey(recursoId)) {
                    reservas.put(recursoId, new LinkedList<>());
                }
                reservas.get(recursoId).offer(new Reserva(recurso, usuario, LocalDateTime.now()));
                recurso.notificarReservaExitosa(usuario);
                System.out.println("El usuario " + usuario.getNombre() + " ha reservado el recurso " + recurso.getTitulo() + ".");
            } else {
                System.out.println("El recurso " + recurso.getTitulo() + " no se puede reservar para el usuario " + usuario.getNombre() + " en este momento.");
            }
        } else {
            System.out.println("El recurso " + recurso.getTitulo() + " no se puede reservar.");
        }
    }

    public void cancelarReserva(RecursoDigital recurso, Usuario usuario) {
        String recursoId = recurso.getId();
        if (reservas.containsKey(recursoId)) {
            reservas.get(recursoId).removeIf(reserva -> reserva.getUsuario().getId().equals(usuario.getId()));
            System.out.println("La reserva del usuario " + usuario.getNombre() + " para el recurso " + recurso.getTitulo() + " ha sido cancelada.");
        } else {
            System.out.println("No se encontró una reserva para el usuario " + usuario.getNombre() + " para el recurso " + recurso.getTitulo() + ".");
        }
    }

    public Queue<Reserva> obtenerReservas(String recursoId) {
        return reservas.get(recursoId);
    }

    public Map<String, Queue<Reserva>> obtenerTodasLasReservas() {
        return reservas;
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
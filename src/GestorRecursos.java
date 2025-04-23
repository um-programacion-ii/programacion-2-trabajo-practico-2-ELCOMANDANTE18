package src;

import src.NotificacionReserva;
import src.NotificacionPrestamo;
import src.NotificacionDevolucion;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;


public class GestorRecursos {
    private List<RecursoDigital> recursos;
    private Map<String, Queue<Reserva>> reservas;
    private ServicioNotificaciones servicioNotificaciones;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final ExecutorService executorService = Executors.newFixedThreadPool(5);

    public GestorRecursos(ServicioNotificaciones servicioNotificaciones) {
        this.recursos = new ArrayList<>();
        this.reservas = new HashMap<>();
        this.servicioNotificaciones = servicioNotificaciones;
    }


    public void agregarRecurso(RecursoDigital recurso) {
        this.recursos.add(recurso);
        System.out.println("Recurso '" + recurso.getTitulo() + "' agregado al sistema.");
    }
    // ... (otros métodos existentes) ...

    public List<RecursoDigital> buscarRecursosPorTitulo(String titulo) {
        List<RecursoDigital> resultados = new ArrayList<>();
        for (RecursoDigital recurso : this.recursos) {
            if (recurso.getTitulo().toLowerCase().contains(titulo.toLowerCase())) {
                resultados.add(recurso);
            }
        }
        return resultados;
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
                // Crear y enviar notificación de reserva
                NotificacionReserva notificacion = new NotificacionReserva(usuario, recurso, LocalDateTime.now().format(FORMATTER));
                enviarNotificacionAsincrona(notificacion);
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
            Queue<Reserva> colaReservas = reservas.get(recursoId);
            colaReservas.removeIf(reserva -> reserva.getUsuario().equals(usuario));
            System.out.println("Reserva del usuario " + usuario.getNombre() + " para el recurso " + recurso.getTitulo() + " cancelada.");
            recurso.notificarCancelacionReserva(usuario);
        } else {
            System.out.println("No se encontró reserva para el recurso " + recurso.getTitulo() + " del usuario " + usuario.getNombre() + ".");
        }
    }
    public Queue<Reserva> obtenerReservas(String recursoId) {
        return reservas.get(recursoId);
    }

    public List<RecursoDigital> buscarRecursosPorCategoria(CategoriaRecurso categoria) {
        List<RecursoDigital> resultados = new ArrayList<>();
        for (RecursoDigital recurso : this.recursos) {
            if (recurso.getCategoria() == categoria) {
                resultados.add(recurso);
            }
        }
        return resultados;
    }

    public RecursoDigital obtenerRecurso(String id) throws RecursoNoDisponibleException {
        for (RecursoDigital recurso : this.recursos) {
            if (recurso.getId().equals(id)) {
                return recurso;
            }
        }
        throw new RecursoNoDisponibleException("No se encontró el recurso con ID: " + id);
    }

    public void prestar(Prestable recurso, Usuario usuario) {
        if (recurso.isDisponible()) {
            recurso.prestar(usuario);
            // Crear y enviar notificación de préstamo
            NotificacionPrestamo notificacion = new NotificacionPrestamo(usuario, (RecursoDigital) recurso, LocalDateTime.now().format(FORMATTER));
            enviarNotificacionAsincrona(notificacion);
        } else {
            System.out.println("El recurso " + ((RecursoDigital) recurso).getTitulo() + " no está disponible.");
        }
    }

    public void devolver(RecursoDigital recurso, Usuario usuario) {
        if (recurso instanceof Prestable) {
            ((Prestable) recurso).devolver(usuario);
            // Crear y enviar notificación de devolución
            NotificacionDevolucion notificacion = new NotificacionDevolucion(usuario, recurso, LocalDateTime.now().format(FORMATTER));
            enviarNotificacionAsincrona(notificacion);
            // Notificar al siguiente usuario en la cola de reserva si existe
            Queue<Reserva> colaReservas = reservas.get(recurso.getId());
            if (colaReservas != null && !colaReservas.isEmpty()) {
                Reserva siguienteReserva = colaReservas.poll();
                Usuario usuarioAReservar = siguienteReserva.getUsuario();
                if (recurso instanceof Prestable) {
                    ((Prestable) recurso).prestar(usuarioAReservar);
                    recurso.notificarDisponibilidad(usuarioAReservar);
                    System.out.println("El recurso " + recurso.getTitulo() + " ha sido prestado al usuario " + usuarioAReservar.getNombre() + " (ID: " + usuarioAReservar.getId() + ").");
                    // Aquí también podríamos enviar una notificación al usuario que tomó la reserva
                    NotificacionPrestamo notificacionReservaTomada = new NotificacionPrestamo(usuarioAReservar, recurso, LocalDateTime.now().format(FORMATTER));
                    enviarNotificacionAsincrona(notificacionReservaTomada);
                } // Cierre del if (recurso instanceof Prestable) interno
            } // Cierre del if (colaReservas != null && !colaReservas.isEmpty())
        } else {
            System.out.println("El recurso " + recurso.getTitulo() + " no se puede prestar.");
        }
    }

    public void ordenarRecursos(Comparator<RecursoDigital> comparator) {
        this.recursos.sort(comparator);
    }

    public static Comparator<RecursoDigital> compararPorId() {
        return Comparator.comparing(RecursoDigital::getId);
    }

    public static Comparator<RecursoDigital> compararPorTitulo() {
        return Comparator.comparing(RecursoDigital::getTitulo);
    }

    private void enviarNotificacionAsincrona(Notificacion notificacion) {
        executorService.submit(() -> servicioNotificaciones.enviarNotificacion(notificacion));
    }

    public void setServicioNotificaciones(ServicioNotificaciones servicioNotificaciones) {
        this.servicioNotificaciones = servicioNotificaciones;
    }

    public void shutdown() {
        executorService.shutdown();
    }
}
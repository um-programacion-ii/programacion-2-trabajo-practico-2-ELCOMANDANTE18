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
    private final List<RecursoDigital> recursos;
    private final Map<String, Queue<Reserva>> reservas;
    private final ServicioNotificaciones servicioNotificaciones;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final ExecutorService executorService = Executors.newFixedThreadPool(5);
    private final Map<String, Integer> contadorPrestamos = new HashMap<>(); // Nuevo mapa para contar préstamos

    public GestorRecursos(ServicioNotificaciones servicioNotificaciones) {
        this.recursos = new ArrayList<>();
        this.reservas = new HashMap<>();
        this.servicioNotificaciones = servicioNotificaciones;
    }

    public void agregarRecurso(RecursoDigital recurso) {
        this.recursos.add(recurso);
        contadorPrestamos.put(recurso.getId(), 0); // Inicializar contador en 0 al agregar recurso
        System.out.println("Recurso '" + recurso.getTitulo() + "' agregado al sistema.");
    }

    public List<RecursoDigital> buscarRecursosPorTitulo(String titulo) {
        List<RecursoDigital> resultados = new ArrayList<>();
        for (RecursoDigital recurso : this.recursos) {
            if (recurso.getTitulo().toLowerCase().contains(titulo.toLowerCase())) {
                resultados.add(recurso);
            }
        }
        return resultados;
    }

    public synchronized void reservar(RecursoDigital recurso, Usuario usuario) {
        System.out.println("Hilo " + Thread.currentThread().getName() + ": Adquiriendo lock para reservar el recurso " + recurso.getTitulo());
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
        System.out.println("Hilo " + Thread.currentThread().getName() + ": Liberando lock para reservar el recurso " + recurso.getTitulo());
    }

    public synchronized void cancelarReserva(RecursoDigital recurso, Usuario usuario) {
        System.out.println("Hilo " + Thread.currentThread().getName() + ": Adquiriendo lock para cancelar la reserva del recurso " + recurso.getTitulo());
        String recursoId = recurso.getId();
        if (reservas.containsKey(recursoId)) {
            Queue<Reserva> colaReservas = reservas.get(recursoId);
            colaReservas.removeIf(reserva -> reserva.getUsuario().equals(usuario));
            System.out.println("Reserva del usuario " + usuario.getNombre() + " para el recurso " + recurso.getTitulo() + " cancelada.");
            recurso.notificarCancelacionReserva(usuario);
        } else {
            System.out.println("No se encontró reserva para el recurso " + recurso.getTitulo() + " del usuario " + usuario.getNombre() + ".");
        }
        System.out.println("Hilo " + Thread.currentThread().getName() + ": Liberando lock para cancelar la reserva del recurso " + recurso.getTitulo());
    }

    public synchronized Queue<Reserva> obtenerReservas(String recursoId) {
        System.out.println("Hilo " + Thread.currentThread().getName() + ": Adquiriendo lock para obtener las reservas del recurso con ID " + recursoId);
        Queue<Reserva> reservasRecurso = reservas.get(recursoId);
        System.out.println("Hilo " + Thread.currentThread().getName() + ": Liberando lock para obtener las reservas del recurso con ID " + recursoId);
        return reservasRecurso;
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


    public synchronized void prestar(Prestable recurso, Usuario usuario) {
        System.out.println("Hilo " + Thread.currentThread().getName() + ": Adquiriendo lock para prestar el recurso " + ((RecursoDigital) recurso).getTitulo());
        if (recurso.isDisponible()) {
            recurso.prestar(usuario);
            // Incrementar el contador de préstamos
            String recursoId = ((RecursoDigital) recurso).getId();
            contadorPrestamos.put(recursoId, contadorPrestamos.getOrDefault(recursoId, 0) + 1);
            // Crear y enviar notificación de préstamo
            NotificacionPrestamo notificacion = new NotificacionPrestamo(usuario, (RecursoDigital) recurso, LocalDateTime.now().format(FORMATTER));
            enviarNotificacionAsincrona(notificacion);
        } else {
            System.out.println("El recurso " + ((RecursoDigital) recurso).getTitulo() + " no está disponible.");
        }
        System.out.println("Hilo " + Thread.currentThread().getName() + ": Liberando lock para prestar el recurso " + ((RecursoDigital) recurso).getTitulo());
    }

    public synchronized void devolver(RecursoDigital recurso, Usuario usuario) {
        System.out.println("Hilo " + Thread.currentThread().getName() + ": Adquiriendo lock para devolver el recurso " + recurso.getTitulo());
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
        System.out.println("Hilo " + Thread.currentThread().getName() + ": Liberando lock para devolver el recurso " + recurso.getTitulo());
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

    public ServicioNotificaciones getServicioNotificaciones() {
        return this.servicioNotificaciones;
    }

    // ELIMINAR ESTE MÉTODO
    // public void setServicioNotificaciones(ServicioNotificaciones servicioNotificaciones) {
    //     this.servicioNotificaciones = servicioNotificaciones;
    // }

    public Map<String, Integer> getContadorPrestamos() {
        return contadorPrestamos;
    }

    public List<Map.Entry<String, Integer>> generarReporteRecursosMasPrestados() {
        return contadorPrestamos.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toList());
    }

    public void shutdown() {
        executorService.shutdown();
    }
}
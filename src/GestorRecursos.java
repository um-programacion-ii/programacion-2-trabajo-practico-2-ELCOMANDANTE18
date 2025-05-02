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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class GestorRecursos {
    private final List<RecursoDigital> recursos;
    private final Map<String, Queue<Reserva>> reservas;
    private final ServicioNotificaciones servicioNotificaciones;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final ExecutorService executorService = Executors.newFixedThreadPool(5);
    private final Map<String, Integer> contadorPrestamos = new HashMap<>();
    private final List<Prestamo> historialPrestamos = new ArrayList<>();

    public GestorRecursos(ServicioNotificaciones servicioNotificaciones) {
        this.recursos = new ArrayList<>();
        this.reservas = new HashMap<>();
        this.servicioNotificaciones = servicioNotificaciones;
    }

    public Map<Usuario, Integer> contarPrestamosPorUsuario() {
        Map<Usuario, Integer> conteo = new HashMap<>();
        synchronized (historialPrestamos) {
            for (Prestamo prestamo : historialPrestamos) {
                Usuario usuario = prestamo.getUsuario();
                conteo.put(usuario, conteo.getOrDefault(usuario, 0) + 1);
            }
        }
        return conteo;
    }

    public CompletableFuture<List<Map.Entry<String, Integer>>> generarReporteRecursosMasPrestadosAsync() {
        return CompletableFuture.supplyAsync(() -> {
            synchronized (contadorPrestamos) {
                return contadorPrestamos.entrySet().stream()
                        .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                        .collect(Collectors.toList());
            }
        }, executorService);
    }

    public CompletableFuture<List<Map.Entry<Usuario, Integer>>> generarReporteUsuariosMasPrestadoresAsync() {
        return CompletableFuture.supplyAsync(() -> {
            Map<Usuario, Integer> conteo;
            synchronized (historialPrestamos) {
                conteo = contarPrestamosPorUsuario();
            }
            return conteo.entrySet().stream()
                    .sorted(Map.Entry.<Usuario, Integer>comparingByValue(Comparator.reverseOrder()))
                    .collect(Collectors.toList());
        }, executorService);
    }

    public void agregarRecurso(RecursoDigital recurso) {
        this.recursos.add(recurso);
        synchronized (contadorPrestamos) {
            contadorPrestamos.put(recurso.getId(), 0);
        }
        System.out.println("Recurso '" + recurso.getTitulo() + "' agregado al sistema.");
    }

    public List<RecursoDigital> buscarRecursosPorTitulo(String titulo) {
        List<RecursoDigital> resultados = new ArrayList<>();
        synchronized (recursos) {
            for (RecursoDigital recurso : this.recursos) {
                if (recurso.getTitulo().toLowerCase().contains(titulo.toLowerCase())) {
                    resultados.add(recurso);
                }
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
        synchronized (recursos) {
            for (RecursoDigital recurso : this.recursos) {
                if (recurso.getCategoria() == categoria) {
                    resultados.add(recurso);
                }
            }
        }
        return resultados;
    }

    public RecursoDigital obtenerRecurso(String id) throws RecursoNoDisponibleException {
        synchronized (recursos) {
            for (RecursoDigital recurso : this.recursos) {
                if (recurso.getId().equals(id)) {
                    return recurso;
                }
            }
        }
        throw new RecursoNoDisponibleException("No se encontró el recurso con ID: " + id);
    }

    public synchronized void prestar(Prestable recurso, Usuario usuario) {
        System.out.println("Hilo " + Thread.currentThread().getName() + ": Adquiriendo lock para prestar el recurso " + ((RecursoDigital) recurso).getTitulo());
        if (recurso.isDisponible()) {
            recurso.prestar(usuario);
            String recursoId = ((RecursoDigital) recurso).getId();
            synchronized (contadorPrestamos) {
                contadorPrestamos.put(recursoId, contadorPrestamos.getOrDefault(recursoId, 0) + 1);
            }
            synchronized (historialPrestamos) {
                historialPrestamos.add(new Prestamo((RecursoDigital) recurso, usuario, LocalDateTime.now()));
            }
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
            NotificacionDevolucion notificacion = new NotificacionDevolucion(usuario, recurso, LocalDateTime.now().format(FORMATTER));
            enviarNotificacionAsincrona(notificacion);
            Queue<Reserva> colaReservas = reservas.get(recurso.getId());
            if (colaReservas != null && !colaReservas.isEmpty()) {
                Reserva siguienteReserva = colaReservas.poll();
                Usuario usuarioAReservar = siguienteReserva.getUsuario();
                if (recurso instanceof Prestable) {
                    ((Prestable) recurso).prestar(usuarioAReservar);
                    recurso.notificarDisponibilidad(usuarioAReservar);
                    System.out.println("El recurso " + recurso.getTitulo() + " ha sido prestado al usuario " + usuarioAReservar.getNombre() + " (ID: " + usuarioAReservar.getId() + ").");
                    NotificacionPrestamo notificacionReservaTomada = new NotificacionPrestamo(usuarioAReservar, recurso, LocalDateTime.now().format(FORMATTER));
                    enviarNotificacionAsincrona(notificacionReservaTomada);
                }
            }
        } else {
            System.out.println("El recurso " + recurso.getTitulo() + " no se puede prestar.");
        }
        System.out.println("Hilo " + Thread.currentThread().getName() + ": Liberando lock para devolver el recurso " + recurso.getTitulo());
    }

    public void ordenarRecursos(Comparator<RecursoDigital> comparator) {
        synchronized (recursos) {
            this.recursos.sort(comparator);
        }
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

    public Map<String, Integer> getContadorPrestamos() {
        synchronized (contadorPrestamos) {
            return new HashMap<>(contadorPrestamos); // Devolver una copia para seguridad
        }
    }

    public CompletableFuture<List<Map.Entry<String, Integer>>> generarReporteRecursosMasPrestados() {
        return CompletableFuture.supplyAsync(() -> {
            synchronized (contadorPrestamos) {
                return contadorPrestamos.entrySet().stream()
                        .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                        .collect(Collectors.toList());
            }
        }, executorService);
    }

    public CompletableFuture<List<Map.Entry<Usuario, Integer>>> generarReporteUsuariosMasPrestadores() {
        return CompletableFuture.supplyAsync(() -> {
            Map<Usuario, Integer> conteo;
            synchronized (historialPrestamos) {
                conteo = contarPrestamosPorUsuario();
            }
            return conteo.entrySet().stream()
                    .sorted(Map.Entry.<Usuario, Integer>comparingByValue(Comparator.reverseOrder()))
                    .collect(Collectors.toList());
        }, executorService);
    }

    public void shutdown() {
        executorService.shutdown();
    }

    private static class Prestamo {
        private RecursoDigital recurso;
        private Usuario usuario;
        private LocalDateTime fechaPrestamo;

        public Prestamo(RecursoDigital recurso, Usuario usuario, LocalDateTime fechaPrestamo) {
            this.recurso = recurso;
            this.usuario = usuario;
            this.fechaPrestamo = fechaPrestamo;
        }

        public RecursoDigital getRecurso() {
            return recurso;
        }

        public Usuario getUsuario() {
            return usuario;
        }

        public LocalDateTime getFechaPrestamo() {
            return fechaPrestamo;
        }
    }
}
package src;

import src.NotificacionPrestamo;
import src.NotificacionDevolucion;
import src.NotificacionReserva;
import src.NotificacionCancelacionReserva;
import src.NotificacionDisponibilidad;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class Audiolibro extends RecursoDigital implements Prestable, Localizable, Reservable {
    private String narrador;
    private double duracion;
    private String ubicacion;
    private boolean disponible = true;
    private Usuario usuarioPrestamo;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Audiolibro(String titulo, String id, CategoriaRecurso categoria, ServicioNotificaciones servicioNotificaciones, String narrador, double duracion, String ubicacion) {
        super(titulo, id, categoria, servicioNotificaciones);
        this.narrador = narrador;
        this.duracion = duracion;
        this.ubicacion = ubicacion;
    }

    @Override
    public boolean isDisponible() {
        return disponible;
    }

    @Override
    public void prestar(Usuario usuario) {
        if (disponible) {
            disponible = false;
            usuarioPrestamo = usuario;
            notificarPrestamo(usuario); // Aquí debes crear un NotificacionPrestamo
            System.out.println("Audiolibro '" + getTitulo() + "' prestado a " + usuario.getNombre() + ".");
        } else {
            System.out.println("El audiolibro '" + getTitulo() + "' no está disponible.");
        }
    }

    @Override
    public void devolver(Usuario usuario) {
        if (!disponible && usuarioPrestamo != null && usuarioPrestamo.equals(usuario)) {
            disponible = true;
            usuarioPrestamo = null;
            notificarDevolucion(usuario); // Aquí debes crear un NotificacionDevolucion
            System.out.println("Audiolibro '" + getTitulo() + "' devuelto por " + usuario.getNombre() + ".");
        } else {
            System.out.println("No se puede devolver el audiolibro '" + getTitulo() + "'.");
        }
    }

    @Override
    public boolean estaDisponibleParaReserva(Usuario usuario) {
        return disponible && (usuarioPrestamo == null || !usuarioPrestamo.equals(usuario));
    }

    @Override
    public void notificarReservaExitosa(Usuario usuario) {
        getServicioNotificaciones().enviarNotificacion(new NotificacionReserva(usuario, this, LocalDateTime.now().format(FORMATTER)));
    }

    @Override
    public void notificarCancelacionReserva(Usuario usuario) {
        getServicioNotificaciones().enviarNotificacion(new NotificacionCancelacionReserva(usuario, this, LocalDateTime.now().format(FORMATTER)));
    }

    @Override
    public void notificarDisponibilidad(Usuario usuario) {
        getServicioNotificaciones().enviarNotificacion(new NotificacionDisponibilidad(usuario, this, LocalDateTime.now().format(FORMATTER)));
    }

    @Override
    public void mostrarDetalles() {
        System.out.println("Audiolibro - Título: " + getTitulo() + ", ID: " + getId() + ", Narrador: " + narrador + ", Duración: " + duracion + " horas, Ubicación: " + ubicacion + ", Disponible: " + disponible);
    }

    @Override
    public String getUbicacion() {
        return ubicacion;
    }
}
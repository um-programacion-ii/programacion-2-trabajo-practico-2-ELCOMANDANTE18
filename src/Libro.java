package src;

import src.NotificacionReserva;
import src.NotificacionCancelacionReserva;
import src.NotificacionDisponibilidad;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Libro extends RecursoDigital implements Prestable, Localizable, Reservable {
    private String autor;
    private String isbn;
    private String ubicacion;
    private boolean disponible = true;
    private Usuario usuarioPrestamo;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Libro(String titulo, String id, CategoriaRecurso categoria, ServicioNotificaciones servicioNotificaciones, String autor, String isbn, String ubicacion) {
        super(titulo, id, categoria, servicioNotificaciones);
        this.autor = autor;
        this.isbn = isbn;
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
            notificarPrestamo(usuario);
            System.out.println("Libro '" + getTitulo() + "' prestado a " + usuario.getNombre() + ".");
        } else {
            System.out.println("El libro '" + getTitulo() + "' no está disponible.");
        }
    }

    @Override
    public void devolver(Usuario usuario) {
        if (!disponible && usuarioPrestamo != null && usuarioPrestamo.equals(usuario)) {
            disponible = true;
            usuarioPrestamo = null;
            notificarDevolucion(usuario);
            System.out.println("Libro '" + getTitulo() + "' devuelto por " + usuario.getNombre() + ".");
        } else {
            System.out.println("No se puede devolver el libro '" + getTitulo() + "'.");
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
        System.out.println("Libro - Título: " + getTitulo() + ", ID: " + getId() + ", Autor: " + autor + ", ISBN: " + isbn + ", Ubicación: " + ubicacion + ", Disponible: " + disponible);
    }

    @Override
    public String getUbicacion() {
        return ubicacion;
    }
}
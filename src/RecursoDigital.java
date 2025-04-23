package src;

import src.NotificacionPrestamo;
import src.NotificacionDevolucion;
import src.NotificacionReserva;
import src.NotificacionCancelacionReserva;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public abstract class RecursoDigital {
    private String titulo;
    private String id;
    private CategoriaRecurso categoria;
    private ServicioNotificaciones servicioNotificaciones;
    private EstadoRecurso estado;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public RecursoDigital(String titulo, String id, CategoriaRecurso categoria, ServicioNotificaciones servicioNotificaciones) {
        this.titulo = titulo;
        this.id = id;
        this.categoria = categoria;
        this.servicioNotificaciones = servicioNotificaciones;
        this.estado = EstadoRecurso.DISPONIBLE; // Estado inicial por defecto
    }
    public void notificarDisponibilidad(Usuario usuario) {
        NotificacionDisponibilidad notificacion = new NotificacionDisponibilidad(usuario, this, LocalDateTime.now().format(FORMATTER));
        servicioNotificaciones.enviarNotificacion(notificacion);
    }
    // Getters
    public String getTitulo() {
        return titulo;
    }

    public String getId() {
        return id;
    }

    public CategoriaRecurso getCategoria() {
        return categoria;
    }

    public ServicioNotificaciones getServicioNotificaciones() {
        return servicioNotificaciones;
    }

    public EstadoRecurso getEstado() {
        return estado;
    }

    // Setters
    public void setServicioNotificaciones(ServicioNotificaciones servicioNotificaciones) {
        this.servicioNotificaciones = servicioNotificaciones;
    }

    public void setEstado(EstadoRecurso estado) {
        this.estado = estado;
    }

    // Método abstracto que las subclases deben implementar
    public abstract void mostrarDetalles();

    // Métodos para notificaciones
    public void notificarPrestamo(Usuario usuario) {
        NotificacionPrestamo notificacion = new NotificacionPrestamo(usuario, this, LocalDateTime.now().format(FORMATTER));
        servicioNotificaciones.enviarNotificacion(notificacion);
    }

    public void notificarDevolucion(Usuario usuario) {
        NotificacionDevolucion notificacion = new NotificacionDevolucion(usuario, this, LocalDateTime.now().format(FORMATTER));
        servicioNotificaciones.enviarNotificacion(notificacion);
    }

    public void notificarReservaExitosa(Usuario usuario) {
        NotificacionReserva notificacion = new NotificacionReserva(usuario, this, LocalDateTime.now().format(FORMATTER));
        servicioNotificaciones.enviarNotificacion(notificacion);
    }



    public void notificarCancelacionReserva(Usuario usuario) {
        NotificacionCancelacionReserva notificacion = new NotificacionCancelacionReserva(usuario, this, LocalDateTime.now().format(FORMATTER));
        servicioNotificaciones.enviarNotificacion(notificacion);
    }
}
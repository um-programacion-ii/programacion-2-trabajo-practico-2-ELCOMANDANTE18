package src;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class NotificacionDisponibilidad implements Notificacion {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private Usuario usuario;
    private RecursoDigital recurso;
    private String mensaje;

    public NotificacionDisponibilidad(Usuario usuario, RecursoDigital recurso, String fechaHora) {
        this.usuario = usuario;
        this.recurso = recurso;
        this.mensaje = "El recurso '" + recurso.getTitulo() + "' ahora está disponible. (" + fechaHora + ")";
    }

    @Override
    public String getMensaje() {
        return mensaje;
    }

    @Override
    public Usuario getUsuario() {
        return usuario;
    }

    @Override
    public RecursoDigital getRecurso() {
        return recurso;
    }
}
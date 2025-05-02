package src;

import src.Usuario;
import src.RecursoDigital;

public class NotificacionPrestamo implements Notificacion {
    private Usuario usuario;
    private RecursoDigital recurso;
    private String fechaPrestamo; // Podemos usar LocalDate o LocalDateTime si es necesario

    public NotificacionPrestamo(Usuario usuario, RecursoDigital recurso, String fechaPrestamo) {
        this.usuario = usuario;
        this.recurso = recurso;
        this.fechaPrestamo = fechaPrestamo;
    }

    @Override
    public String getMensaje() {
        return "Se ha realizado el préstamo del recurso '" + recurso.getTitulo() + "' al usuario " + usuario.getNombre() + " el día " + fechaPrestamo + ".";
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
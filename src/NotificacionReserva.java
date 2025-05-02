package src;
import src.Usuario;
import src.RecursoDigital;

public class NotificacionReserva implements Notificacion {
    private Usuario usuario;
    private RecursoDigital recurso;
    private String fechaReserva;

    public NotificacionReserva(Usuario usuario, RecursoDigital recurso, String fechaReserva) {
        this.usuario = usuario;
        this.recurso = recurso;
        this.fechaReserva = fechaReserva;
    }

    @Override
    public String getMensaje() {
        return "Se ha realizado una reserva para el recurso '" + recurso.getTitulo() + "' por el usuario " + usuario.getNombre() + " el día " + fechaReserva + ".";
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
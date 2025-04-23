package src;

import src.Usuario;
import src.RecursoDigital;

public class NotificacionDevolucion implements Notificacion {
    private Usuario usuario;
    private RecursoDigital recurso;
    private String fechaDevolucion;

    public NotificacionDevolucion(Usuario usuario, RecursoDigital recurso, String fechaDevolucion) {
        this.usuario = usuario;
        this.recurso = recurso;
        this.fechaDevolucion = fechaDevolucion;
    }

    @Override
    public String getMensaje() {
        return "Se ha registrado la devolución del recurso '" + recurso.getTitulo() + "' por el usuario " + usuario.getNombre() + " el día " + fechaDevolucion + ".";
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
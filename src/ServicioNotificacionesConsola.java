package src;

import src.Notificacion;

public class ServicioNotificacionesConsola implements ServicioNotificaciones {

    @Override
    public void enviarNotificacion(Notificacion notificacion) {
        System.out.println("[Notificación Consola] Para: " + notificacion.getUsuario().getNombre() + " (ID: " + notificacion.getUsuario().getId() + ")");
        System.out.println("[Notificación Consola] Mensaje: " + notificacion.getMensaje());
        if (notificacion.getRecurso() != null) {
            System.out.println("[Notificación Consola] Recurso: " + notificacion.getRecurso().getTitulo() + " (ID: " + notificacion.getRecurso().getId() + ")");
        }
        System.out.println("----------------------------------------");
    }
}
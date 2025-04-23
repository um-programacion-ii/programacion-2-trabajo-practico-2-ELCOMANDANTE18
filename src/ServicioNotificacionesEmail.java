package src;

public class ServicioNotificacionesEmail implements ServicioNotificaciones {
    @Override
    public void enviarNotificacion(Notificacion notificacion) {
        Usuario usuario = notificacion.getUsuario();
        String mensaje = notificacion.getMensaje();
        System.out.println("[Email] Para: " + usuario.getEmail() + ", Mensaje: " + mensaje);
        // Aquí iría la lógica real para enviar un email
    }
}
package src;

public class ServicioNotificacionesSMS implements ServicioNotificaciones {

    @Override
    public void enviarNotificacion(Notificacion notificacion) {
        Usuario usuario = notificacion.getUsuario();
        String mensaje = notificacion.getMensaje();
        System.out.println("Enviando SMS a " + usuario.getNombre() + ": " + mensaje);
        // Aquí iría la lógica real para enviar un SMS
    }
}
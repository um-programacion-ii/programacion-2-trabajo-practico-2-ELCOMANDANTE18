package src;

public class ServicioNotificacionesEmail implements ServicioNotificaciones {
    @Override
    public void enviarNotificacion(Usuario usuario, String mensaje) {
        System.out.println("[Email] Para: " + usuario.getEmail() + ", Mensaje: " + mensaje);
        // Aquí iría la lógica real para enviar un email
    }
}
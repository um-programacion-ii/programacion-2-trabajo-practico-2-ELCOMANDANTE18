package src;

public class ServicioNotificacionesSMS implements ServicioNotificaciones {

    @Override
    public void enviarNotificacion(Usuario usuario, String mensaje) {
        System.out.println("Enviando SMS a " + usuario.getNombre() + ": " + mensaje);
        // Aquí iría la lógica real para enviar un SMS
    }
}
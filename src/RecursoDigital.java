package src;

public abstract class RecursoDigital {
    private String titulo;
    private String id;
    private CategoriaRecurso categoria;
    private ServicioNotificaciones servicioNotificaciones;
    private EstadoRecurso estado;

    public RecursoDigital(String titulo, String id, CategoriaRecurso categoria, ServicioNotificaciones servicioNotificaciones) {
        this.titulo = titulo;
        this.id = id;
        this.categoria = categoria;
        this.servicioNotificaciones = servicioNotificaciones;
        this.estado = EstadoRecurso.DISPONIBLE; // Estado inicial por defecto
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
        servicioNotificaciones.enviarNotificacion(usuario, "Recurso '" + titulo + "' prestado.");
    }

    public void notificarDevolucion(Usuario usuario) {
        servicioNotificaciones.enviarNotificacion(usuario, "Recurso '" + titulo + "' devuelto.");
    }

    public void notificarReservaExitosa(Usuario usuario) {
        servicioNotificaciones.enviarNotificacion(usuario, "Reserva exitosa para '" + titulo + "'.");
    }

    // Añade este método:
    public void notificarDisponibilidad(Usuario usuario) {
        servicioNotificaciones.enviarNotificacion(usuario, "Recurso '" + titulo + "' ahora disponible para reserva/préstamo.");
    }
}
package src;

public class Audiolibro extends RecursoDigital implements Prestable, Localizable {
    private String narrador;
    private String duracion;
    private String ubicacion;
    private EstadoRecurso estado; // Nuevo atributo

    public Audiolibro(String titulo, String id, String narrador, String duracion, String ubicacion, ServicioNotificaciones servicioNotificaciones) {
        super(titulo, id, CategoriaRecurso.AUDIOLIBRO, servicioNotificaciones);
        this.narrador = narrador;
        this.duracion = duracion;
        this.ubicacion = ubicacion;
        this.estado = EstadoRecurso.DISPONIBLE; // Inicializar estado
    }

    // Getters para narrador, duracion, ubicacion y estado

    public String getNarrador() {
        return narrador;
    }

    public String getDuracion() {
        return duracion;
    }

    @Override
    public String getUbicacion() {
        return ubicacion;
    }

    public EstadoRecurso getEstado() {
        return estado;
    }

    public void setEstado(EstadoRecurso estado) {
        this.estado = estado;
    }

    @Override
    public void prestar(Usuario usuario) {
        if (estado == EstadoRecurso.DISPONIBLE) {
            estado = EstadoRecurso.PRESTADO;
            getServicioNotificaciones().enviarNotificacion(usuario, "Préstamo del audiolibro: " + getTitulo());
            System.out.println("Audiolibro '" + getTitulo() + "' prestado a " + usuario.getNombre());
        } else {
            System.out.println("El audiolibro '" + getTitulo() + "' no está disponible para préstamo.");
        }
    }

    @Override
    public void devolver(Usuario usuario) {
        if (estado == EstadoRecurso.PRESTADO) {
            estado = EstadoRecurso.DISPONIBLE;
            getServicioNotificaciones().enviarNotificacion(usuario, "Devolución del audiolibro: " + getTitulo());
            System.out.println("Audiolibro '" + getTitulo() + "' devuelto por " + usuario.getNombre());
        } else {
            System.out.println("El audiolibro '" + getTitulo() + "' no estaba prestado a este usuario.");
        }
    }

    @Override
    public void mostrarDetalles() {
        super.mostrarDetalles();
        System.out.println("Narrador: " + narrador);
        System.out.println("Duración: " + duracion);
        System.out.println("Ubicación: " + ubicacion);
        System.out.println("Estado: " + estado);
    }
}
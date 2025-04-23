package src;

public class Audiolibro extends RecursoDigital implements Prestable, Localizable, Reservable {
    private String narrador;
    private String duracion;
    private String ubicacion;

    public Audiolibro(String titulo, String id, String narrador, String duracion, String ubicacion, ServicioNotificaciones servicioNotificaciones) {
        super(titulo, id, CategoriaRecurso.AUDIOLIBRO, servicioNotificaciones);
        this.narrador = narrador;
        this.duracion = duracion;
        this.ubicacion = ubicacion;
        // El estado se inicializa en RecursoDigital
    }

    // Getters para narrador, duracion y ubicacion

    @Override
    public void prestar(Usuario usuario) {
        if (getEstado() == EstadoRecurso.DISPONIBLE) {
            setEstado(EstadoRecurso.PRESTADO);
            getServicioNotificaciones().enviarNotificacion(usuario, "Préstamo del audiolibro: " + getTitulo());
            System.out.println("Audiolibro '" + getTitulo() + "' prestado a " + usuario.getNombre());
        } else {
            System.out.println("El audiolibro '" + getTitulo() + "' no está disponible para préstamo.");
        }
    }

    @Override
    public void devolver(Usuario usuario) {
        if (getEstado() == EstadoRecurso.PRESTADO) {
            setEstado(EstadoRecurso.DISPONIBLE);
            getServicioNotificaciones().enviarNotificacion(usuario, "Devolución del audiolibro: " + getTitulo());
            System.out.println("Audiolibro '" + getTitulo() + "' devuelto por " + usuario.getNombre());
        } else {
            System.out.println("El audiolibro '" + getTitulo() + "' no estaba prestado a este usuario.");
        }
    }

    @Override
    public String getUbicacion() {
        return ubicacion;
    }

    @Override
    public boolean estaDisponibleParaReserva(Usuario usuario) {
        return this.getEstado() != EstadoRecurso.PRESTADO; // Accede al estado usando el getter
    }

    @Override
    public void notificarReservaExitosa(Usuario usuario) {
        super.notificarReservaExitosa(usuario);
    }

    // Añade esta implementación:
    @Override
    public boolean isDisponible() {
        return this.getEstado() == EstadoRecurso.DISPONIBLE; // Accede al estado usando el getter
    }

    @Override
    public void mostrarDetalles() {
        // No llamar a super.mostrarDetalles() aquí
        System.out.println("Título: " + getTitulo());
        System.out.println("ID: " + getId());
        System.out.println("Categoría: " + getCategoria());
        System.out.println("Narrador: " + narrador);
        System.out.println("Duración: " + duracion);
        System.out.println("Ubicación: " + ubicacion);
        System.out.println("Estado: " + getEstado());
    }
}
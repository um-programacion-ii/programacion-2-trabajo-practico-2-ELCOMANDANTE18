package src;

public class Libro extends RecursoDigital implements Prestable, Localizable, Reservable {
    private String autor;
    private String isbn;
    private String ubicacion;

    public Libro(String titulo, String id, String autor, String isbn, String ubicacion, ServicioNotificaciones servicioNotificaciones) {
        super(titulo, id, CategoriaRecurso.LIBRO, servicioNotificaciones);
        this.autor = autor;
        this.isbn = isbn;
        this.ubicacion = ubicacion;
        // El estado se inicializa en RecursoDigital
    }

    // Getters para autor, isbn y ubicacion

    @Override
    public void prestar(Usuario usuario) {
        if (getEstado() == EstadoRecurso.DISPONIBLE) {
            setEstado(EstadoRecurso.PRESTADO);
            getServicioNotificaciones().enviarNotificacion(usuario, "Préstamo del libro: " + getTitulo());
            System.out.println("Libro '" + getTitulo() + "' prestado a " + usuario.getNombre());
        } else {
            System.out.println("El libro '" + getTitulo() + "' no está disponible para préstamo.");
        }
    }

    @Override
    public void devolver(Usuario usuario) {
        if (getEstado() == EstadoRecurso.PRESTADO) {
            setEstado(EstadoRecurso.DISPONIBLE);
            getServicioNotificaciones().enviarNotificacion(usuario, "Devolución del libro: " + getTitulo());
            System.out.println("Libro '" + getTitulo() + "' devuelto por " + usuario.getNombre());
        } else {
            System.out.println("El libro '" + getTitulo() + "' no estaba prestado a este usuario.");
        }
    }

    @Override
    public String getUbicacion() {
        return ubicacion;
    }

    @Override
    public boolean estaDisponibleParaReserva(Usuario usuario) {
        return this.getEstado() == EstadoRecurso.DISPONIBLE; // Un libro puede reservarse si está disponible
    }

    @Override
    public void notificarReservaExitosa(Usuario usuario) {
        super.notificarReservaExitosa(usuario);
    }

    @Override
    public boolean isDisponible() {
        return this.getEstado() == EstadoRecurso.DISPONIBLE;
    }

    @Override
    public void mostrarDetalles() {
        // No llamar a super.mostrarDetalles() aquí
        System.out.println("Título: " + getTitulo());
        System.out.println("ID: " + getId());
        System.out.println("Categoría: " + getCategoria());
        System.out.println("Autor: " + autor);
        System.out.println("ISBN: " + isbn);
        System.out.println("Ubicación: " + ubicacion);
        System.out.println("Estado: " + getEstado());
    }
}
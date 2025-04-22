package src;

public class Libro extends RecursoDigital implements Prestable, Localizable {
    private String autor;
    private String isbn;
    private String ubicacion;
    private EstadoRecurso estado; // Nuevo atributo

    public Libro(String titulo, String id, String autor, String isbn, String ubicacion, ServicioNotificaciones servicioNotificaciones) {
        super(titulo, id, CategoriaRecurso.LIBRO, servicioNotificaciones);
        this.autor = autor;
        this.isbn = isbn;
        this.ubicacion = ubicacion;
        this.estado = EstadoRecurso.DISPONIBLE; // Inicializar estado
    }

    // Getters para autor, isbn, ubicacion y estado

    @Override
    public void prestar(Usuario usuario) {
        if (estado == EstadoRecurso.DISPONIBLE) {
            estado = EstadoRecurso.PRESTADO;
            getServicioNotificaciones().enviarNotificacion(usuario, "Préstamo del libro: " + getTitulo());
            System.out.println("Libro '" + getTitulo() + "' prestado a " + usuario.getNombre());
        } else {
            System.out.println("El libro '" + getTitulo() + "' no está disponible para préstamo.");
        }
    }

    @Override
    public void devolver(Usuario usuario) {
        if (estado == EstadoRecurso.PRESTADO) {
            estado = EstadoRecurso.DISPONIBLE;
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
    public void mostrarDetalles() {
        super.mostrarDetalles();
        System.out.println("Autor: " + autor);
        System.out.println("ISBN: " + isbn);
        System.out.println("Ubicación: " + ubicacion);
        System.out.println("Estado: " + estado);
    }
}
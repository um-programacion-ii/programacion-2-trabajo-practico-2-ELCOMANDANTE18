package src;

public class Audiolibro implements RecursoDigital, Prestable, Localizable {
    private String titulo;
    private String id;
    private String narrador;
    private String duracion;
    private String ubicacion;
    private boolean prestado;
    private Usuario usuarioPrestamo;
    private ServicioNotificaciones servicioNotificaciones;
    private final CategoriaRecurso categoria = CategoriaRecurso.AUDIOLIBRO;

    public Audiolibro(String titulo, String id, String narrador, String duracion, String ubicacion, ServicioNotificaciones servicioNotificaciones) {
        this.titulo = titulo;
        this.id = id;
        this.narrador = narrador;
        this.duracion = duracion;
        this.ubicacion = ubicacion;
        this.servicioNotificaciones = servicioNotificaciones;
        this.prestado = false;
        this.usuarioPrestamo = null;
    }

    @Override
    public String getTitulo() {
        return titulo;
    }

    @Override
    public String getId() {
        return id;
    }
    @Override
    public boolean estaPrestado() {
        return prestado;
    }

    @Override
    public String getUbicacion() { // <---- Una ÚNICA definición de getUbicacion()
        return ubicacion;
    }

    @Override
    public CategoriaRecurso getCategoria() {
        return categoria;
    }

    @Override
    public ServicioNotificaciones getServicioNotificaciones() {
        return servicioNotificaciones;
    }

    @Override
    public void mostrarDetalles() {
        System.out.println("Audiolibro:");
        System.out.println("  Título: " + titulo);
        System.out.println("  ID: " + id);
        System.out.println("  Narrador: " + narrador);
        System.out.println("  Duración: " + duracion);
        System.out.println("  Ubicación: " + ubicacion);
        System.out.println("  Categoría: " + categoria);
        if (prestado) {
            System.out.println("  Prestado a: " + usuarioPrestamo.getNombre());
        } else {
            System.out.println("  Disponible");
        }
    }

    @Override
    public void prestar(Usuario usuario) {
        if (!prestado) {
            this.prestado = true;
            this.usuarioPrestamo = usuario;
            getServicioNotificaciones().enviarNotificacion(usuario, "Préstamo del audiolibro: " + titulo);
        } else {
            System.out.println("El audiolibro \"" + titulo + "\" ya está prestado a " + this.usuarioPrestamo.getNombre());
        }
    }

    @Override
    public void devolver(Usuario usuario) {
        if (prestado && this.usuarioPrestamo.equals(usuario)) {
            this.prestado = false;
            this.usuarioPrestamo = null;
            getServicioNotificaciones().enviarNotificacion(usuario, "Devolución del audiolibro: " + titulo);
        } else if (!prestado) {
            System.out.println("El audiolibro \"" + titulo + "\" no está prestado.");
        } else {
            System.out.println("El audiolibro \"" + titulo + "\" fue prestado a otro usuario.");
        }
    }

    @Override
    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }
}
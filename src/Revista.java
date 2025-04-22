package src;

public class Revista implements RecursoDigital, Localizable {
    private String titulo;
    private String id;
    private String numero;
    private String issn;
    private String ubicacion;
    private ServicioNotificaciones servicioNotificaciones;
    private final CategoriaRecurso categoria = CategoriaRecurso.REVISTA;

    public Revista(String titulo, String id, String numero, String issn, String ubicacion, ServicioNotificaciones servicioNotificaciones) {
        this.titulo = titulo;
        this.id = id;
        this.numero = numero;
        this.issn = issn;
        this.ubicacion = ubicacion;
        this.servicioNotificaciones = servicioNotificaciones;
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
        System.out.println("Revista:");
        System.out.println("  Título: " + titulo);
        System.out.println("  ID: " + id);
        System.out.println("  Número: " + numero);
        System.out.println("  ISSN: " + issn);
        System.out.println("  Ubicación: " + ubicacion);
        System.out.println("  Categoría: " + categoria);
    }

    @Override
    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }
}
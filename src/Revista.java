package src;

public class Revista extends RecursoDigital implements Localizable {
    private String numero;
    private String issn;
    private String ubicacion;

    public Revista(String titulo, String id, String numero, String issn, String ubicacion, ServicioNotificaciones servicioNotificaciones) {
        super(titulo, id, CategoriaRecurso.REVISTA, servicioNotificaciones);
        this.numero = numero;
        this.issn = issn;
        this.ubicacion = ubicacion;
        // El estado se inicializa en RecursoDigital
    }

    // Getters para numero, issn y ubicacion

    @Override
    public String getUbicacion() {
        return ubicacion;
    }

    @Override
    public void mostrarDetalles() {
        // No llamar a super.mostrarDetalles() aquí
        System.out.println("Título: " + getTitulo());
        System.out.println("ID: " + getId());
        System.out.println("Categoría: " + getCategoria());
        System.out.println("Número: " + numero);
        System.out.println("ISSN: " + issn);
        System.out.println("Ubicación: " + ubicacion);
        System.out.println("Estado: " + getEstado());
    }
}
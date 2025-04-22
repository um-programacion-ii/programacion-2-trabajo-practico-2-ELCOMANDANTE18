package src;

public class Revista extends RecursoDigital implements Localizable {
    private String numero;
    private String issn;
    private String ubicacion;
    private EstadoRecurso estado; // Nuevo atributo

    public Revista(String titulo, String id, String numero, String issn, String ubicacion, ServicioNotificaciones servicioNotificaciones) {
        super(titulo, id, CategoriaRecurso.REVISTA, servicioNotificaciones);
        this.numero = numero;
        this.issn = issn;
        this.ubicacion = ubicacion;
        this.estado = EstadoRecurso.DISPONIBLE; // Inicializar estado
    }

    // Getters para numero, issn, ubicacion y estado

    public String getNumero() {
        return numero;
    }

    public String getIssn() {
        return issn;
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
    public void mostrarDetalles() {
        super.mostrarDetalles();
        System.out.println("Número: " + numero);
        System.out.println("ISSN: " + issn);
        System.out.println("Ubicación: " + ubicacion);
        System.out.println("Estado: " + estado);
    }
}
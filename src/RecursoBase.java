package src;

public abstract class RecursoBase extends RecursoDigital {
    private String titulo;
    private String id;

    public RecursoBase(String titulo, String id, CategoriaRecurso categoria, ServicioNotificaciones servicioNotificaciones) {
        super(titulo, id, categoria, servicioNotificaciones);
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
    public abstract void mostrarDetalles();
}
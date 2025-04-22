package src;

public enum CategoriaRecurso {
    LIBRO("Libro"),
    REVISTA("Revista"),
    AUDIOLIBRO("Audiolibro"),
    EBOOK("Libro Electrónico"),
    PODCAST("Podcast"),
    VIDEO("Video");

    private final String nombre;

    CategoriaRecurso(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
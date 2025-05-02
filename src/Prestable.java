package src;

public interface Prestable {
    void prestar(Usuario usuario);
    void devolver(Usuario usuario);
    boolean isDisponible(); // Añade esta línea
}
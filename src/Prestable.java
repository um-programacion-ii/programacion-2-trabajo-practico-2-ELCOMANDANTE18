package src;

public interface Prestable {
    void prestar(Usuario usuario);
    void devolver(Usuario usuario);
    boolean estaPrestado(); // <---- Este es el método que falta en Audiolibro
}
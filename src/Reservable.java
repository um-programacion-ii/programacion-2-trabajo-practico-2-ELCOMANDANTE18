package src;

public interface Reservable {
    boolean estaDisponibleParaReserva(Usuario usuario);
    void notificarReservaExitosa(Usuario usuario);
}
package src;

import java.time.LocalDateTime;

public class Reserva {
    private Usuario usuario;
    private RecursoDigital recurso;
    private LocalDateTime fechaReserva;

    public Reserva(RecursoDigital recurso, Usuario usuario, LocalDateTime fechaReserva) {
        this.usuario = usuario;
        this.recurso = recurso;
        this.fechaReserva = fechaReserva;
    }

    public RecursoDigital getRecurso() {
        return recurso;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public LocalDateTime getFechaReserva() {
        return fechaReserva;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Reserva reserva = (Reserva) obj;
        return recurso.equals(reserva.recurso) && usuario.equals(reserva.usuario);
    }

    @Override
    public int hashCode() {
        int result = recurso.hashCode();
        result = 31 * result + usuario.hashCode();
        return result;
    }
}
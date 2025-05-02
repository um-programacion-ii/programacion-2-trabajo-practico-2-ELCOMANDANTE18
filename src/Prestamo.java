package src;

import java.time.LocalDateTime;

public class Prestamo {
    private RecursoDigital recurso;
    private Usuario usuario;
    private LocalDateTime fechaPrestamo;
    private LocalDateTime fechaDevolucion;

    public Prestamo(RecursoDigital recurso, Usuario usuario, LocalDateTime fechaPrestamo) {
        this.recurso = recurso;
        this.usuario = usuario;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaDevolucion = null; // Inicialmente no se ha devuelto
    }

    // Getters para los atributos (puedes agregarlos según necesites)
    public RecursoDigital getRecurso() {
        return recurso;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public LocalDateTime getFechaPrestamo() {
        return fechaPrestamo;
    }

    public LocalDateTime getFechaDevolucion() {
        return fechaDevolucion;
    }

    public void setFechaDevolucion(LocalDateTime fechaDevolucion) {
        this.fechaDevolucion = fechaDevolucion;
    }
}
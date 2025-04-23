package src;

import src.RecursoDigital;
import java.util.Scanner;

@FunctionalInterface
public interface FuncionCrearRecurso {
    RecursoDigital crear(Scanner scanner, ServicioNotificaciones servicioNotificaciones);
}
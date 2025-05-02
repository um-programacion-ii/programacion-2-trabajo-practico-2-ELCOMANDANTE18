package src;

import src.Usuario;
import src.RecursoDigital;

public interface Notificacion {
    String getMensaje();
    Usuario getUsuario();
    RecursoDigital getRecurso(); // Puede ser null en algunos casos
}
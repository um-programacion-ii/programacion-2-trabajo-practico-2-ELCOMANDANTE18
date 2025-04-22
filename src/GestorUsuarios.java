package src;

import java.util.ArrayList;
import java.util.List;

public class GestorUsuarios {
    private List<Usuario> usuarios = new ArrayList<>();

    public void agregarUsuario(Usuario usuario) {
        usuarios.add(usuario);
    }

    public Usuario obtenerUsuario(String id) throws UsuarioNoEncontradoException {
        for (Usuario usuario : usuarios) {
            if (usuario.getId().equals(id)) {
                return usuario;
            }
        }
        throw new UsuarioNoEncontradoException("No se encontró ningún usuario con el ID: " + id);
    }
}
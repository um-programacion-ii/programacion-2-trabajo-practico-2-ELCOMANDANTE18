package src;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GestorUsuarios {
    private List<Usuario> listaUsuarios;

    public GestorUsuarios() {
        this.listaUsuarios = new ArrayList<>();
    }

    public void agregarUsuario(Usuario usuario) {
        this.listaUsuarios.add(usuario);
    }

    public List<Usuario> getUsuarios() {
        return this.listaUsuarios;
    }

    public Usuario obtenerUsuario(String usuarioId) throws UsuarioNoEncontradoException {
        Optional<Usuario> usuarioEncontrado = this.listaUsuarios.stream()
                .filter(usuario -> usuario.getId().equals(usuarioId))
                .findFirst();
        if (usuarioEncontrado.isPresent()) {
            return usuarioEncontrado.get();
        } else {
            throw new UsuarioNoEncontradoException("No se encontró un usuario con el ID: " + usuarioId);
        }
    }

    // Puedes añadir aquí otros métodos para gestionar usuarios (buscar, eliminar, etc.) si los necesitas
}
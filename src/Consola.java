package src;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Queue;
import java.util.LinkedList;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Consola {
    private Scanner scanner;
    private GestorRecursos gestorRecursos;
    private GestorUsuarios gestorUsuarios;
    private ServicioNotificaciones servicioNotificaciones;
    private Map<String, Supplier<RecursoDigital>> creadoresRecursos;
    private Map<String, String> tiposRecursos;
    private List<RecursoDigital> resultadosBusqueda; // Para almacenar los resultados de la búsqueda
    private boolean ejecutar = true;

    public Consola(GestorRecursos gestorRecursos, GestorUsuarios gestorUsuarios, ServicioNotificaciones servicioNotificaciones) {
        this.scanner = new Scanner(System.in);
        this.gestorRecursos = gestorRecursos;
        this.gestorUsuarios = gestorUsuarios;
        this.servicioNotificaciones = servicioNotificaciones;
        this.creadoresRecursos = new HashMap<>();
        this.tiposRecursos = new HashMap<>();
        this.resultadosBusqueda = new ArrayList<>(); // Inicializar la lista de resultados
        inicializarOpcionesRecursos();
    }

    private void inicializarOpcionesRecursos() {
        creadoresRecursos.put("1", this::crearLibroDesdeInput);
        tiposRecursos.put("1", "Libro");
        creadoresRecursos.put("2", this::crearRevistaDesdeInput);
        tiposRecursos.put("2", "Revista");
        creadoresRecursos.put("3", this::crearAudiolibroDesdeInput);
        tiposRecursos.put("3", "Audiolibro");
    }

    public void mostrarMenu() {
        System.out.println("\n--- Menú ---");
        System.out.println("1. Agregar recurso");
        System.out.println("2. Buscar recurso por título");
        System.out.println("3. Buscar recurso por categoría");
        System.out.println("4. Prestar recurso");
        System.out.println("5. Devolver recurso");
        System.out.println("6. Reservar recurso");
        System.out.println("7. Cancelar reserva");
        System.out.println("8. Mostrar reservas de recurso"); // Nueva opción
        System.out.println("0. Salir");
        System.out.print("Seleccione una opción: ");
    }

    public void ejecutarOpcion(String opcionStr) {
        try {
            int opcion = Integer.parseInt(opcionStr);
            ejecutarAccion(opcion);
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida. Por favor, ingrese un número.");
        }
    }
    private void agregarRecurso() {
        System.out.println("\n--- Agregar Nuevo Recurso ---");
        System.out.println("Seleccione el tipo de recurso a agregar:");
        System.out.println("1. Libro");
        System.out.println("2. Revista");
        System.out.println("3. Audiolibro");
        System.out.print("Ingrese su opción: ");
        String opcion = scanner.nextLine();

        switch (opcion) {
            case "1":
                Libro nuevoLibro = crearLibroDesdeInput();
                gestorRecursos.agregarRecurso(nuevoLibro);
                System.out.println("Libro agregado con ID: " + nuevoLibro.getId());
                break;
            case "2":
                Revista nuevaRevista = crearRevistaDesdeInput();
                gestorRecursos.agregarRecurso(nuevaRevista);
                System.out.println("Revista agregada con ID: " + nuevaRevista.getId());
                break;
            case "3":
                Audiolibro nuevoAudiolibro = crearAudiolibroDesdeInput();
                gestorRecursos.agregarRecurso(nuevoAudiolibro);
                System.out.println("Audiolibro agregado con ID: " + nuevoAudiolibro.getId());
                break;
            default:
                System.out.println("Opción inválida.");
        }
    }
    private void mostrarOpcionesOrdenamiento(List<RecursoDigital> listaAOrdenar) {
        if (!listaAOrdenar.isEmpty()) {
            System.out.println("\n--- Opciones de Ordenamiento ---");
            System.out.println("1. Ordenar por ID");
            System.out.println("2. Ordenar por Título");
            System.out.println("3. No ordenar y volver");
            System.out.print("Seleccione una opción de ordenamiento: ");
            String opcionOrdenamiento = scanner.nextLine();

            switch (opcionOrdenamiento) {
                case "1":
                    gestorRecursos.ordenarRecursos(GestorRecursos.compararPorId());
                    System.out.println("\n--- Resultados ordenados por ID ---");
                    mostrarResultados(listaAOrdenar);
                    break;
                case "2":
                    gestorRecursos.ordenarRecursos(GestorRecursos.compararPorTitulo());
                    System.out.println("\n--- Resultados ordenados por Título ---");
                    mostrarResultados(listaAOrdenar);
                    break;
                case "3":
                    System.out.println("Volviendo sin ordenar.");
                    break;
                default:
                    System.out.println("Opción de ordenamiento inválida.");
                    mostrarOpcionesOrdenamiento(listaAOrdenar);
            }
        } else {
            System.out.println("No hay resultados para ordenar.");
        }
    }
    private void buscarPorTitulo() {
        System.out.print("Ingrese el título a buscar: ");
        String tituloBusqueda = scanner.nextLine();
        List<RecursoDigital> resultados = gestorRecursos.buscarRecursosPorTitulo(tituloBusqueda);
        this.resultadosBusqueda = resultados; // Guardar resultados para posible ordenamiento
        if (resultados.isEmpty()) {
            System.out.println("No se encontraron recursos con el título: " + tituloBusqueda);
        } else {
            System.out.println("\n--- Resultados de la búsqueda por título: \"" + tituloBusqueda + "\" ---");
            mostrarResultados(resultados);
            mostrarOpcionesOrdenamiento(resultados);
        }
    }
    private void mostrarResultados(List<RecursoDigital> resultados) {
        for (RecursoDigital recurso : resultados) {
            System.out.println("ID: " + recurso.getId() + ", Título: " + recurso.getTitulo() + ", Categoría: " + recurso.getCategoria());
        }
    }
    private void buscarPorCategoria() {
        System.out.println("\n--- Buscar por Categoría ---");
        CategoriaRecurso[] categorias = CategoriaRecurso.values();
        for (int i = 0; i < categorias.length; i++) {
            System.out.println((i + 1) + ". " + categorias[i].getNombre());
        }
        System.out.print("Seleccione el número de la categoría a buscar: ");
        String opcionCategoria = scanner.nextLine();
        try {
            int indiceSeleccionado = Integer.parseInt(opcionCategoria) - 1;
            if (indiceSeleccionado >= 0 && indiceSeleccionado < categorias.length) {
                CategoriaRecurso categoriaSeleccionada = categorias[indiceSeleccionado];
                List<RecursoDigital> resultados = gestorRecursos.buscarRecursosPorCategoria(categoriaSeleccionada);
                this.resultadosBusqueda = resultados; // Guardar resultados para posible ordenamiento
                if (resultados.isEmpty()) {
                    System.out.println("No se encontraron recursos en la categoría: " + categoriaSeleccionada.getNombre());
                } else {
                    System.out.println("\n--- Resultados de la búsqueda por categoría: " + categoriaSeleccionada.getNombre() + " ---");
                    mostrarResultados(resultados);
                    mostrarOpcionesOrdenamiento(resultados);
                }
            } else {
                System.out.println("Opción de categoría inválida.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida. Por favor, ingrese un número.");
        }
    }

    private void ejecutarAccion(int opcion) {
        switch (opcion) {
            case 1:
                agregarRecurso(); // Asumiendo que tu método para agregar se llama así
                break;
            case 2:
                buscarPorTitulo(); // Asumiendo que tu método para buscar por título se llama así
                break;
            case 3:
                buscarPorCategoria(); // Asumiendo que tu método para buscar por categoría se llama así
                break;
            case 4:
                prestarRecurso(); // Asumiendo que tu método para prestar se llama así
                break;
            case 5:
                devolverRecurso(); // Asumiendo que tu método para devolver se llama así
                break;
            case 6:
                reservarRecursoConsola(); // Asumiendo que tu método para reservar se llama así
                break;
            case 7:
                cancelarReservaConsola(); // Asumiendo que tu método para cancelar reserva se llama así
                break;
            case 8:
                mostrarReservasDeRecurso(); // Asumiendo que tu método para mostrar reservas se llama así
                break;
            case 0:
                System.out.println("Saliendo del sistema.");
                ejecutar = false;
                break;
            default:
                System.out.println("Opción inválida. Por favor, intente de nuevo.");
        }
    }

    private Libro crearLibroDesdeInput() {
        System.out.print("Ingrese el título del libro: ");
        String titulo = scanner.nextLine();
        System.out.print("Ingrese el ID del libro: ");
        String id = scanner.nextLine();
        System.out.print("Ingrese el autor del libro: ");
        String autor = scanner.nextLine();
        System.out.print("Ingrese el ISBN del libro: ");
        String isbn = scanner.nextLine();
        System.out.print("Ingrese la ubicación del libro: ");
        String ubicacion = scanner.nextLine();
        return new Libro(titulo, id, autor, isbn, ubicacion, servicioNotificaciones);
    }

    private Revista crearRevistaDesdeInput() {
        System.out.print("Ingrese el título de la revista: ");
        String titulo = scanner.nextLine();
        System.out.print("Ingrese el ID de la revista: ");
        String id = scanner.nextLine();
        System.out.print("Ingrese el número de la revista: ");
        String numero = scanner.nextLine();
        System.out.print("Ingrese el ISSN de la revista: ");
        String issn = scanner.nextLine();
        System.out.print("Ingrese la ubicación de la revista: ");
        String ubicacion = scanner.nextLine();
        return new Revista(titulo, id, numero, issn, ubicacion, servicioNotificaciones);
    }

    private Audiolibro crearAudiolibroDesdeInput() {
        System.out.print("Ingrese el título del audiolibro: ");
        String titulo = scanner.nextLine();
        System.out.print("Ingrese el ID del audiolibro: ");
        String id = scanner.nextLine();
        System.out.print("Ingrese el narrador del audiolibro: ");
        String narrador = scanner.nextLine();
        System.out.print("Ingrese la duración del audiolibro: ");
        String duracion = scanner.nextLine();
        System.out.print("Ingrese la ubicación del audiolibro: ");
        String ubicacion = scanner.nextLine();
        return new Audiolibro(titulo, id, narrador, duracion, ubicacion, servicioNotificaciones);
    }

    private void mostrarRecursoPorId() {
        System.out.print("Ingrese el ID del recurso a mostrar: ");
        String id = scanner.nextLine();
        try {
            RecursoDigital recurso = gestorRecursos.obtenerRecurso(id);
            System.out.println("\n--- Detalles del Recurso ---");
            recurso.mostrarDetalles();
        } catch (RecursoNoDisponibleException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    private void prestarRecurso() {
        System.out.print("Ingrese el ID del recurso a prestar: ");
        String recursoId = scanner.nextLine();
        try {
            RecursoDigital recurso = gestorRecursos.obtenerRecurso(recursoId);
            if (recurso instanceof Prestable) {
                System.out.print("Ingrese el ID del usuario que tomará prestado el recurso: ");
                String usuarioId = scanner.nextLine();
                try {
                    Usuario usuario = gestorUsuarios.obtenerUsuario(usuarioId);
                    ((Prestable) recurso).prestar(usuario);
                } catch (UsuarioNoEncontradoException e) {
                    System.out.println("Error: " + e.getMessage());
                }
            } else {
                System.out.println("El recurso con ID " + recursoId + " no se puede prestar.");
            }
        } catch (RecursoNoDisponibleException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void devolverRecurso() {
        System.out.print("Ingrese el ID del recurso a devolver: ");
        String recursoId = scanner.nextLine();
        try {
            RecursoDigital recurso = gestorRecursos.obtenerRecurso(recursoId);
            System.out.print("Ingrese su ID de usuario: ");
            String usuarioId = scanner.nextLine();
            try {
                Usuario usuario = gestorUsuarios.obtenerUsuario(usuarioId);
                gestorRecursos.devolver(recurso, usuario);
            } catch (UsuarioNoEncontradoException e) {
                System.out.println("Error: " + e.getMessage());
            }
        } catch (RecursoNoDisponibleException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void reservarRecursoConsola() {
        System.out.print("Ingrese el ID del recurso a reservar: ");
        String recursoId = scanner.nextLine();
        try {
            RecursoDigital recurso = gestorRecursos.obtenerRecurso(recursoId);
            if (recurso instanceof Reservable) {
                System.out.print("Ingrese su ID de usuario: ");
                String usuarioId = scanner.nextLine();
                try {
                    Usuario usuario = gestorUsuarios.obtenerUsuario(usuarioId);
                    gestorRecursos.reservar(recurso, usuario);
                } catch (UsuarioNoEncontradoException e) {
                    System.out.println("Error: " + e.getMessage());
                }
            } else {
                System.out.println("El recurso con ID " + recursoId + " no se puede reservar.");
            }
        } catch (RecursoNoDisponibleException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void cancelarReservaConsola() {
        System.out.print("Ingrese el ID del recurso para cancelar la reserva: ");
        String recursoId = scanner.nextLine();
        try {
            RecursoDigital recurso = gestorRecursos.obtenerRecurso(recursoId);
            System.out.print("Ingrese su ID de usuario: ");
            String usuarioId = scanner.nextLine();
            try {
                Usuario usuario = gestorUsuarios.obtenerUsuario(usuarioId);
                gestorRecursos.cancelarReserva(recurso, usuario);
            } catch (UsuarioNoEncontradoException e) {
                System.out.println("Error: " + e.getMessage());
            }
        } catch (RecursoNoDisponibleException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void mostrarReservasDeRecurso() {
        System.out.print("Ingrese el ID del recurso para ver sus reservas: ");
        String recursoId = scanner.nextLine();
        try {
            RecursoDigital recurso = gestorRecursos.obtenerRecurso(recursoId);
            Queue<Reserva> colaReservas = gestorRecursos.obtenerReservas(recursoId);
            if (colaReservas != null && !colaReservas.isEmpty()) {
                System.out.println("\nReservas pendientes para el recurso '" + recurso.getTitulo() + "':");
                int i = 1;
                for (Reserva reserva : colaReservas) {
                    System.out.println(i + ". Usuario ID: " + reserva.getUsuario().getId() + ", Fecha de Reserva: " + reserva.getFechaReserva());
                    i++;
                }
            } else if (colaReservas != null && colaReservas.isEmpty()) {
                System.out.println("No hay reservas pendientes para el recurso '" + recurso.getTitulo() + "'.");
            } else {
                System.out.println("No se encontraron reservas para el recurso con ID: " + recursoId);
            }
        } catch (RecursoNoDisponibleException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }


    private void mostrarUbicacion() {
        System.out.print("Ingrese el ID del recurso para mostrar su ubicación: ");
        String recursoId = scanner.nextLine();
        try {
            RecursoDigital recurso = gestorRecursos.obtenerRecurso(recursoId);
            if (recurso instanceof Localizable) {
                String ubicacion = ((Localizable) recurso).getUbicacion();
                System.out.println("La ubicación del recurso con ID " + recursoId + " es: " + ubicacion);
            } else {
                System.out.println("El recurso con ID " + recursoId + " no tiene información de ubicación.");
            }
        } catch (RecursoNoDisponibleException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void cerrarScanner() {
        scanner.close();
    }

    public void ejecutar() {
        String opcion;
        do {
            mostrarMenu();
            opcion = scanner.nextLine();
            ejecutarOpcion(opcion);
        } while (ejecutar);

        cerrarScanner();
    }

    public static void main(String[] args) {
        GestorRecursos gestorRecursos = new GestorRecursos();
        GestorUsuarios gestorUsuarios = new GestorUsuarios();
        ServicioNotificacionesConsola servicioNotificacionesConsola = new ServicioNotificacionesConsola();
        Consola consola = new Consola(gestorRecursos, gestorUsuarios, servicioNotificacionesConsola);

        consola.gestorUsuarios.agregarUsuario(new Usuario("Juan Perez", "1", "juan.perez@email.com"));
        consola.gestorUsuarios.agregarUsuario(new Usuario("Maria Lopez", "2", "maria.lopez@email.com"));

        // Agregar algunos recursos para probar
        consola.gestorRecursos.agregarRecurso(new Libro("El Señor de los Anillos", "L001", "J.R.R. Tolkien", "978-0618260274", "Estantería A1", servicioNotificacionesConsola));
        consola.gestorRecursos.agregarRecurso(new Revista("National Geographic", "R001", "Vol. 240 No. 3", "0027-9358", "Hemeroteca 2", servicioNotificacionesConsola));
        consola.gestorRecursos.agregarRecurso(new Audiolibro("Harry Potter y la Piedra Filosofal", "A001", "Stephen Fry", "12 horas", "Sección Audiolibros", servicioNotificacionesConsola));

        consola.ejecutar();
    }
}
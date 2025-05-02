package src;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Queue;
import java.util.LinkedList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Consola {
    private GestorRecursos gestorRecursos;
    private GestorUsuarios gestorUsuarios;
    private ServicioNotificaciones servicioNotificaciones;
    private Scanner scanner;
    private Map<String, FuncionCrearRecurso> creadoresRecursos;
    private Map<Integer, String> tiposRecursos;
    private List<RecursoDigital> resultadosBusqueda;
    private boolean ejecutar = true;

    public Consola() {
        ServicioNotificaciones consolaNotificaciones = new ServicioNotificacionesConsola();
        gestorRecursos = new GestorRecursos(consolaNotificaciones);
        gestorUsuarios = new GestorUsuarios();
        this.gestorUsuarios = gestorUsuarios;
        this.servicioNotificaciones = consolaNotificaciones;
        scanner = new Scanner(System.in);
        this.creadoresRecursos = new HashMap<>();
        this.tiposRecursos = new HashMap<>();
        this.resultadosBusqueda = new ArrayList<>();
        inicializarOpcionesRecursos();

        creadoresRecursos.put("1", (Scanner s, ServicioNotificaciones sn) -> crearLibroDesdeInput(s, sn));
        creadoresRecursos.put("2", (Scanner s, ServicioNotificaciones sn) -> crearRevistaDesdeInput(s, sn));
        creadoresRecursos.put("3", (Scanner s, ServicioNotificaciones sn) -> crearAudiolibroDesdeInput(s, sn));
    }

    private void inicializarOpcionesRecursos() {
        tiposRecursos.put(1, "Libro");
        tiposRecursos.put(2, "Revista");
        tiposRecursos.put(3, "Audiolibro");
    }

    private void mostrarMenu() {
        System.out.println("--- Menú ---");
        System.out.println("1. Agregar recurso");
        System.out.println("2. Buscar recurso por título");
        System.out.println("3. Buscar recurso por categoría");
        System.out.println("4. Prestar recurso");
        System.out.println("5. Devolver recurso");
        System.out.println("6. Reservar recurso");
        System.out.println("7. Cancelar reserva");
        System.out.println("8. Mostrar reservas de recurso");
        System.out.println("9. Generar Reportes");
        System.out.println("10. Gestionar Usuarios");
        System.out.println("0. Salir");
        System.out.print("Seleccione una opción: ");
    }

    public void ejecutarOpcion(String opcionStr) {
        try {
            int opcion = Integer.parseInt(opcionStr);
            switch (opcion) {
                case 1:
                    agregarRecurso();
                    break;
                case 2:
                    buscarPorTitulo();
                    break;
                case 3:
                    buscarPorCategoria();
                    break;
                case 4:
                    prestarRecurso();
                    break;
                case 5:
                    devolverRecurso();
                    break;
                case 6:
                    reservarRecursoConsola();
                    break;
                case 7:
                    cancelarReservaConsola();
                    break;
                case 8:
                    mostrarReservasDeRecurso();
                    break;
                case 9:
                    generarReportes();
                    break;
                case 10:
                    gestionarUsuarios();
                    break;
                case 0:
                    ejecutar = false;
                    System.out.println("Saliendo del sistema.");
                    gestorRecursos.shutdown();
                    break;
                default:
                    System.out.println("Opción inválida. Por favor, ingrese un número entre 0 y 10.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida. Por favor, ingrese un número.");
        }
    }

    private void gestionarUsuarios() {
        int opcionUsuario;
        do {
            System.out.println("\n--- Gestión de Usuarios ---");
            System.out.println("1. Agregar Usuario");
            System.out.println("2. Listar Usuarios");
            System.out.println("0. Volver al menú principal");
            System.out.print("Ingrese una opción: ");
            String opcionUsuarioStr = scanner.nextLine();
            try {
                opcionUsuario = Integer.parseInt(opcionUsuarioStr);
                switch (opcionUsuario) {
                    case 1:
                        agregarUsuarioDesdeInput();
                        break;
                    case 2:
                        listarUsuarios();
                        break;
                    case 0:
                        System.out.println("Volviendo al menú principal.");
                        break;
                    default:
                        System.out.println("Opción inválida. Intente nuevamente.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Por favor, ingrese un número.");
                opcionUsuario = -1;
            }
            System.out.println();
        } while (opcionUsuario != 0);
    }

    private void agregarUsuarioDesdeInput() {
        System.out.println("\n--- Agregar Nuevo Usuario ---");
        System.out.print("Ingrese el ID del usuario: ");
        String id = scanner.nextLine();
        System.out.print("Ingrese el nombre del usuario: ");
        String nombre = scanner.nextLine();
        System.out.print("Ingrese el email del usuario: ");
        String email = scanner.nextLine();
        Usuario nuevoUsuario = new Usuario(nombre, id, email);
        gestorUsuarios.agregarUsuario(nuevoUsuario);
        System.out.println("Usuario con ID " + nuevoUsuario.getId() + " agregado.");
    }

    private void listarUsuarios() {
        System.out.println("\n--- Listado de Usuarios ---");
        List<Usuario> usuarios = gestorUsuarios.getUsuarios();
        if (usuarios.isEmpty()) {
            System.out.println("No hay usuarios registrados en el sistema.");
        } else {
            for (Usuario usuario : usuarios) {
                System.out.println("ID: " + usuario.getId() + ", Nombre: " + usuario.getNombre());
            }
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

        FuncionCrearRecurso creador = creadoresRecursos.get(opcion);
        if (creador != null) {
            RecursoDigital nuevoRecurso = creador.crear(scanner, servicioNotificaciones);
            gestorRecursos.agregarRecurso(nuevoRecurso);
            System.out.println(nuevoRecurso.getCategoria().getNombre() + " agregado con ID: " + nuevoRecurso.getId());
        } else {
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
        this.resultadosBusqueda = resultados;
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
                this.resultadosBusqueda = resultados;
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
                    gestorRecursos.prestar((Prestable) recurso, usuario);
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

    private void generarReportes() {
        int opcionReporte;
        do {
            System.out.println("--- Generación de Reportes ---");
            System.out.println("1. Recursos Más Prestados");
            System.out.println("2. Usuarios con Más Préstamos");
            System.out.println("0. Volver al menú principal");
            System.out.print("Ingrese una opción: ");
            String opcionReporteStr = scanner.nextLine();
            try {
                opcionReporte = Integer.parseInt(opcionReporteStr);
                switch (opcionReporte) {
                    case 1:
                        mostrarReporteRecursosMasPrestadosAsync();
                        break;
                    case 2:
                        mostrarReporteUsuariosMasPrestadoresAsync();
                        break;
                    case 0:
                        System.out.println("Volviendo al menú principal.");
                        break;
                    default:
                        System.out.println("Opción inválida. Intente nuevamente.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Por favor, ingrese un número.");
                opcionReporte = -1;
            }
            System.out.println();
        } while (opcionReporte != 0);
    }

    private void mostrarReporteRecursosMasPrestadosAsync() {
        System.out.println("Generando reporte de recursos más prestados en segundo plano...");
        CompletableFuture<List<Map.Entry<String, Integer>>> reporteFuture = gestorRecursos.generarReporteRecursosMasPrestadosAsync();

        reporteFuture.thenAccept(reporte -> {
            System.out.println("\n--- Reporte de Recursos Más Prestados ---");
            if (reporte.isEmpty()) {
                System.out.println("No hay datos de préstamos disponibles.");
            } else {
                System.out.println("Título del Recurso\t\tCantidad de Préstamos");
                System.out.println("-----------------------------------------");
                for (Map.Entry<String, Integer> entry : reporte) {
                    try {
                        RecursoDigital recurso = gestorRecursos.obtenerRecurso(entry.getKey());
                        System.out.printf("%-30s\t\t%d%n", recurso.getTitulo(), entry.getValue());
                    } catch (RecursoNoDisponibleException e) {
                        System.out.println("Error al obtener el título del recurso con ID: " + entry.getKey());
                    }
                }
            }
        }).exceptionally(ex -> {
            System.err.println("Error al generar el reporte de recursos más prestados: " + ex.getMessage());
            return null;
        });
    }

    private void mostrarReporteUsuariosMasPrestadoresAsync() {
        System.out.println("Generando reporte de usuarios con más préstamos en segundo plano...");
        CompletableFuture<List<Map.Entry<Usuario, Integer>>> reporteFuture = gestorRecursos.generarReporteUsuariosMasPrestadoresAsync();

        reporteFuture.thenAccept(reporte -> {
            System.out.println("\n--- Reporte de Usuarios con Más Préstamos ---");
            if (reporte.isEmpty()) {
                System.out.println("No hay datos de préstamos disponibles.");
            } else {
                System.out.println("ID Usuario\tNombre Usuario\tCantidad de Préstamos");
                System.out.println("--------------------------------------------------");
                for (Map.Entry<Usuario, Integer> entry : reporte) {
                    Usuario usuario = entry.getKey();
                    Integer cantidad = entry.getValue();
                    System.out.printf("%-10s\t%-20s\t%d%n", usuario.getId(), usuario.getNombre(), cantidad);
                }
            }
        }).exceptionally(ex -> {
            System.err.println("Error al generar el reporte de usuarios más prestadores: " + ex.getMessage());
            return null;
        });
    }

    public Libro crearLibroDesdeInput(Scanner scanner, ServicioNotificaciones servicioNotificaciones) {
        System.out.println("Ingrese el título del libro:");
        String titulo = scanner.nextLine();
        System.out.println("Ingrese el ID del libro:");
        String id = scanner.nextLine();
        System.out.println("Ingrese el autor del libro:");
        String autor = scanner.nextLine();
        System.out.println("Ingrese el ISBN del libro:");
        String isbn = scanner.nextLine();
        System.out.println("Ingrese la ubicación del libro:");
        String ubicacion = scanner.nextLine();
        System.out.println("Ingrese la categoría del libro (EJEMPLO: NOVELA, CIENCIA_FICCION, etc.):");
        String categoriaStr = scanner.nextLine().toUpperCase().replace(" ", "_");
        CategoriaRecurso categoria = CategoriaRecurso.valueOf(categoriaStr);

        return new Libro(titulo, id, categoria, servicioNotificaciones, autor, isbn, ubicacion);
    }

    private Revista crearRevistaDesdeInput(Scanner scanner, ServicioNotificaciones servicioNotificaciones) {
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

    private Audiolibro crearAudiolibroDesdeInput(Scanner scanner, ServicioNotificaciones servicioNotificaciones) {
        System.out.print("Ingrese el título del audiolibro: ");
        String titulo = scanner.nextLine();
        System.out.print("Ingrese el ID del audiolibro: ");
        String id = scanner.nextLine();
        System.out.print("Ingrese el narrador del audiolibro: ");
        String narrador = scanner.nextLine();
        System.out.print("Ingrese la duración del audiolibro (en horas, ejemplo: 1.5): ");
        String duracionStr = scanner.nextLine();
        double duracion = Double.parseDouble(duracionStr);
        System.out.print("Ingrese la ubicación del audiolibro: ");
        String ubicacion = scanner.nextLine();
        System.out.print("Ingrese la categoría del audiolibro (EJEMPLO: FICCIÓN, NO_FICCIÓN, etc.): ");
        String categoriaStr = scanner.nextLine().toUpperCase().replace(" ", "_");
        CategoriaRecurso categoria = CategoriaRecurso.valueOf(categoriaStr);

        return new Audiolibro(titulo, id, categoria, servicioNotificaciones, narrador, duracion, ubicacion);
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
        Consola consola = new Consola();

        Libro libro1 = new Libro("El Señor de los Anillos", "LSA001", CategoriaRecurso.LIBRO, consola.servicioNotificaciones, "J.R.R. Tolkien", "978-0618260274", "Estantería A, Sección 1");
        Libro libro2 = new Libro("Cien años de soledad", "CAS002", CategoriaRecurso.LIBRO, consola.servicioNotificaciones, "Gabriel García Márquez", "978-0307350528", "Estantería B, Sección 2");
        Libro libro3 = new Libro("1984", "NIN003", CategoriaRecurso.LIBRO, consola.servicioNotificaciones, "George Orwell", "978-0451524935", "Estantería A, Sección 3");

        consola.gestorRecursos.agregarRecurso(libro1);
        consola.gestorRecursos.agregarRecurso(libro2);
        consola.gestorRecursos.agregarRecurso(libro3);

        Usuario usuario1 = new Usuario("123", "Ana Pérez", "ana.perez@email.com");
        Usuario usuario2 = new Usuario("456", "Carlos López", "carlos.lopez@email.com");

        consola.gestorUsuarios.agregarUsuario(usuario1);
        consola.gestorUsuarios.agregarUsuario(usuario2);

        if (libro1 instanceof Prestable && usuario1 != null) {
            consola.gestorRecursos.prestar((Prestable) libro1, usuario1);
            consola.gestorRecursos.prestar((Prestable) libro1, usuario1);
        }
        if (libro2 instanceof Prestable && usuario2 != null) {
            consola.gestorRecursos.prestar((Prestable) libro2, usuario2);
        }
        if (libro3 instanceof Prestable && usuario1 != null) {
            consola.gestorRecursos.prestar((Prestable) libro3, usuario1);
        }

        consola.ejecutar();
    }
}
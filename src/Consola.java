package src;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Consola {
    private Scanner scanner;
    private GestorRecursos gestorRecursos;
    private GestorUsuarios gestorUsuarios;
    private ServicioNotificaciones servicioNotificaciones;
    private Map<String, Supplier<RecursoDigital>> creadoresRecursos;
    private Map<String, String> tiposRecursos;

    public Consola(GestorRecursos gestorRecursos, GestorUsuarios gestorUsuarios, ServicioNotificaciones servicioNotificaciones) {
        this.scanner = new Scanner(System.in);
        this.gestorRecursos = gestorRecursos;
        this.gestorUsuarios = gestorUsuarios;
        this.servicioNotificaciones = servicioNotificaciones;
        this.creadoresRecursos = new HashMap<>();
        this.tiposRecursos = new HashMap<>();
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
        System.out.println("\n--- Menú Biblioteca Digital ---");
        System.out.println("1. Agregar Recurso");
        System.out.println("2. Mostrar Recurso por ID");
        System.out.println("3. Prestar Recurso");
        System.out.println("4. Reservar Recurso");
        System.out.println("5. Cancelar Reserva");
        System.out.println("6. Mostrar Ubicación");
        System.out.println("7. Buscar Recursos"); // Opción para la búsqueda
        System.out.println("8. Salir");
        System.out.print("Seleccione una opción: ");
    }

    public void ejecutarOpcion(String opcion) {
        switch (opcion) {
            case "1":
                agregarNuevoRecurso();
                break;
            case "2":
                mostrarRecursoPorId();
                break;
            case "3":
                prestarRecurso();
                break;
            case "4":
                reservarRecurso();
                break;
            case "5":
                cancelarReserva();
                break;
            case "6":
                mostrarUbicacion();
                break;
            case "7":
                mostrarMenuBusqueda(); // Llamada al nuevo menú de búsqueda
                break;
            case "8":
                System.out.println("Saliendo del sistema.");
                break;
            default:
                System.out.println("Opción inválida.");
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
        RecursoDigital recurso = gestorRecursos.obtenerRecurso(id);
        if (recurso != null) {
            System.out.println("\n--- Detalles del Recurso ---");
            recurso.mostrarDetalles();
        } else {
            System.out.println("No se encontró ningún recurso con el ID: " + id);
        }
    }

    private void mostrarMenuBusqueda() {
        System.out.println("\n--- Menú de Búsqueda de Recursos ---");
        System.out.println("1. Buscar por Título");
        System.out.println("2. Buscar por Categoría");
        System.out.println("3. Listar todos los recursos"); // Podemos añadir esto aquí
        System.out.println("4. Volver al menú principal");
        System.out.print("Seleccione una opción de búsqueda: ");
        String opcionBusqueda = scanner.nextLine();
        ejecutarOpcionBusqueda(opcionBusqueda);
    }

    private void ejecutarOpcionBusqueda(String opcionBusqueda) {
        switch (opcionBusqueda) {
            case "1":
                buscarPorTitulo(); // Llamada al nuevo método de búsqueda por título
                break;
            case "2":
                // Implementar búsqueda por categoría (Issue GRC-Unificado-3)
                System.out.println("Búsqueda por categoría (próximamente).");
                break;
            case "3":
                listarTodosLosRecursos(); // Implementación básica para listar
                break;
            case "4":
                System.out.println("Volviendo al menú principal.");
                break;
            default:
                System.out.println("Opción de búsqueda inválida.");
        }
    }

    private void buscarPorTitulo() {
        System.out.print("Ingrese el título a buscar: ");
        String tituloBusqueda = scanner.nextLine();
        List<RecursoDigital> resultados = gestorRecursos.buscarRecursosPorTitulo(tituloBusqueda);
        if (resultados.isEmpty()) {
            System.out.println("No se encontraron recursos con el título: " + tituloBusqueda);
        } else {
            System.out.println("\n--- Resultados de la búsqueda por título: \"" + tituloBusqueda + "\" ---");
            for (RecursoDigital recurso : resultados) {
                System.out.println("ID: " + recurso.getId() + ", Título: " + recurso.getTitulo());
            }
        }
    }

    private void listarTodosLosRecursos() {
        List<RecursoDigital> todosLosRecursos = gestorRecursos.getRecursos();
        if (todosLosRecursos.isEmpty()) {
            System.out.println("No hay recursos disponibles en la biblioteca.");
        } else {
            System.out.println("\n--- Listado de todos los recursos ---");
            for (RecursoDigital recurso : todosLosRecursos) {
                System.out.println("ID: " + recurso.getId() + ", Título: " + recurso.getTitulo());
            }
        }
    }

    private void agregarNuevoRecurso() {
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

    private void prestarRecurso() {
        System.out.print("Ingrese el ID del recurso a prestar: ");
        String recursoId = scanner.nextLine();
        RecursoDigital recurso = gestorRecursos.obtenerRecurso(recursoId);

        if (recurso instanceof Prestable) {
            System.out.print("Ingrese el ID del usuario que tomará prestado el recurso: ");
            String usuarioId = scanner.nextLine();
            Usuario usuario = gestorUsuarios.obtenerUsuario(usuarioId);

            if (usuario != null) {
                ((Prestable) recurso).prestar(usuario);
            } else {
                System.out.println("No se encontró ningún usuario con el ID: " + usuarioId);
            }
        } else {
            System.out.println("El recurso con ID " + recursoId + " no se puede prestar.");
        }
    }

    private void reservarRecurso() {
        System.out.print("Ingrese el ID del recurso a reservar: ");
        String recursoId = scanner.nextLine();
        RecursoDigital recurso = gestorRecursos.obtenerRecurso(recursoId);

        if (recurso instanceof Reservable) {
            System.out.print("Ingrese su ID de usuario: ");
            String usuarioId = scanner.nextLine();
            Usuario usuario = gestorUsuarios.obtenerUsuario(usuarioId);

            if (usuario != null) {
                ((Reservable) recurso).reservar(usuario);
            } else {
                System.out.println("No se encontró ningún usuario con el ID: " + usuarioId);
            }
        } else {
            System.out.println("El recurso con ID " + recursoId + " no se puede reservar.");
        }
    }

    private void cancelarReserva() {
        System.out.print("Ingrese el ID del recurso para cancelar la reserva: ");
        String recursoId = scanner.nextLine();
        RecursoDigital recurso = gestorRecursos.obtenerRecurso(recursoId);

        if (recurso instanceof Reservable) {
            System.out.print("Ingrese su ID de usuario: ");
            String usuarioId = scanner.nextLine();
            Usuario usuario = gestorUsuarios.obtenerUsuario(usuarioId);

            if (usuario != null) {
                ((Reservable) recurso).cancelarReserva(usuario);
            } else {
                System.out.println("No se encontró ningún usuario con el ID: " + usuarioId);
            }
        } else {
            System.out.println("El recurso con ID " + recursoId + " no se puede reservar.");
        }
    }

    private void mostrarUbicacion() {
        System.out.print("Ingrese el ID del recurso para mostrar su ubicación: ");
        String recursoId = scanner.nextLine();
        RecursoDigital recurso = gestorRecursos.obtenerRecurso(recursoId);

        if (recurso instanceof Localizable) {
            String ubicacion = ((Localizable) recurso).getUbicacion();
            System.out.println("La ubicación del recurso con ID " + recursoId + " es: " + ubicacion);
        } else {
            System.out.println("El recurso con ID " + recursoId + " no tiene información de ubicación.");
        }
    }

    public void cerrarScanner() {
        scanner.close();
    }

    public static void main(String[] args) {
        GestorRecursos gestorRecursos = new GestorRecursos();
        GestorUsuarios gestorUsuarios = new GestorUsuarios();
        ServicioNotificaciones servicioNotificacionesConsola = new ServicioNotificacionesConsola();
        Consola consola = new Consola(gestorRecursos, gestorUsuarios, servicioNotificacionesConsola);

        consola.gestorUsuarios.agregarUsuario(new Usuario("Juan Perez", "1", "juan.perez@email.com"));
        consola.gestorUsuarios.agregarUsuario(new Usuario("Maria Lopez", "2", "maria.lopez@email.com"));

        // Agregar algunos recursos para probar
        consola.gestorRecursos.agregarRecurso(new Libro("El Señor de los Anillos", "L001", "J.R.R. Tolkien", "978-0618260274", "Estantería A1", servicioNotificacionesConsola));
        consola.gestorRecursos.agregarRecurso(new Revista("National Geographic", "R001", "Vol. 240 No. 3", "0027-9358", "Hemeroteca 2", servicioNotificacionesConsola));
        consola.gestorRecursos.agregarRecurso(new Audiolibro("Harry Potter y la Piedra Filosofal", "A001", "Stephen Fry", "12 horas", "Sección Audiolibros", servicioNotificacionesConsola));

        String opcion;
        do {
            consola.mostrarMenu();
            opcion = consola.scanner.nextLine();
            consola.ejecutarOpcion(opcion);
        } while (!opcion.equals("8"));
    }
}
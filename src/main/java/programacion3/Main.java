package programacion3;

import jakarta.persistence.EntityManager;
import programacion3.entities.Categoria;
import programacion3.entities.DetallePedido;
import programacion3.entities.Pedido;
import programacion3.entities.Producto;
import programacion3.entities.Usuario;
import programacion3.enums.Estado;
import programacion3.enums.FormaPago;
import programacion3.enums.Rol;
import programacion3.repository.CategoriaRepository;
import programacion3.repository.PedidoRepository;
import programacion3.repository.ProductoRepository;
import programacion3.repository.UsuarioRepository;
import programacion3.util.JPAUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Scanner;

public class Main {

    private static final CategoriaRepository categoriaRepo = new CategoriaRepository();
    private static final ProductoRepository productoRepo = new ProductoRepository();
    private static final UsuarioRepository usuarioRepo = new UsuarioRepository();
    private static final PedidoRepository pedidoRepo = new PedidoRepository();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        boolean salir = false;
        while (!salir) {
            System.out.println("\n========================================");
            System.out.println("         MENU PRINCIPAL");
            System.out.println("========================================");
            System.out.println("1. Categorias");
            System.out.println("2. Productos");
            System.out.println("3. Usuarios");
            System.out.println("4. Pedidos");
            System.out.println("5. Reportes");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opcion: ");

            String opcion = scanner.nextLine().trim();
            switch (opcion) {
                case "1" -> menuCategorias();
                case "2" -> menuProductos();
                case "3" -> menuUsuarios();
                case "4" -> menuPedidos();
                case "5" -> menuReportes();
                case "0" -> salir = true;
                default -> System.out.println("Opcion invalida. Intente nuevamente.");
            }
        }

        JPAUtil.close();
        System.out.println("Hasta luego!");
    }

    //  MENU CATEGORIAS
    // ─────────────────────────────────────────────────────────────

    private static void menuCategorias() {
        boolean volver = false;
        while (!volver) {
            System.out.println("\n--- CATEGORIAS ---");
            System.out.println("1. Alta de categoria");
            System.out.println("2. Baja logica de categoria");
            System.out.println("3. Modificacion de categoria");
            System.out.println("4. Listado de categorias activas");
            System.out.println("0. Volver");
            System.out.print("Seleccione una opcion: ");

            String opcion = scanner.nextLine().trim();
            switch (opcion) {
                case "1" -> altaCategoria();
                case "2" -> bajaCategoria();
                case "3" -> modificarCategoria();
                case "4" -> listarCategorias();
                case "0" -> volver = true;
                default -> System.out.println("Opcion invalida.");
            }
        }
    }

    private static void altaCategoria() {
        System.out.println("\n-- Alta de Categoria --");
        System.out.print("Nombre: ");
        String nombre = scanner.nextLine().trim();
        if (nombre.isEmpty()) {
            System.out.println("Error: el nombre no puede estar vacio.");
            return;
        }
        System.out.print("Descripcion: ");
        String descripcion = scanner.nextLine().trim();

        Categoria categoria = Categoria.builder()
                .nombre(nombre)
                .descripcion(descripcion)
                .eliminado(false)
                .createdAt(LocalDateTime.now())
                .build();

        Categoria guardada = (Categoria) categoriaRepo.guardar(categoria);
        System.out.println("Categoria creada con exito. ID generado: " + guardada.getId());
    }

    private static void bajaCategoria() {
        System.out.println("\n-- Baja Logica de Categoria --");
        listarCategorias();

        System.out.print("Ingrese el ID de la categoria a dar de baja: ");
        Long id = leerLong();
        if (id == null) return;

        Optional<Categoria> opt = categoriaRepo.buscarPorId(id);
        if (opt.isEmpty() || opt.get().isEliminado()) {
            System.out.println("Error: no existe una categoria activa con ese ID.");
            return;
        }

        String nombre = opt.get().getNombre();
        boolean resultado = categoriaRepo.eliminarLogico(id);
        if (resultado) {
            System.out.println("Categoria \"" + nombre + "\" dada de baja correctamente.");
        } else {
            System.out.println("Error al dar de baja la categoria.");
        }
    }

    private static void modificarCategoria() {
        System.out.println("\n-- Modificacion de Categoria --");
        listarCategorias();

        System.out.print("Ingrese el ID de la categoria a modificar: ");
        Long id = leerLong();
        if (id == null) return;

        Optional<Categoria> opt = categoriaRepo.buscarPorId(id);
        if (opt.isEmpty() || opt.get().isEliminado()) {
            System.out.println("Error: no existe una categoria activa con ese ID.");
            return;
        }

        Categoria categoria = opt.get();
        System.out.println("Valores actuales:");
        System.out.println("  Nombre     : " + categoria.getNombre());
        System.out.println("  Descripcion: " + categoria.getDescripcion());

        System.out.print("Nuevo nombre (Enter para conservar): ");
        String nombre = scanner.nextLine().trim();
        if (!nombre.isEmpty()) {
            categoria.setNombre(nombre);
        }

        System.out.print("Nueva descripcion (Enter para conservar): ");
        String descripcion = scanner.nextLine().trim();
        if (!descripcion.isEmpty()) {
            categoria.setDescripcion(descripcion);
        }

        categoriaRepo.guardar(categoria);
        System.out.println("Categoria modificada correctamente.");
    }

    private static void listarCategorias() {
        List<Categoria> categorias = categoriaRepo.listarActivos();
        if (categorias.isEmpty()) {
            System.out.println("No hay categorias activas.");
            return;
        }
        System.out.println("\n-- Categorias activas --");
        System.out.printf("%-6s %-20s %s%n", "ID", "Nombre", "Descripcion");
        System.out.println("-".repeat(60));
        for (Categoria c : categorias) {
            System.out.printf("%-6d %-20s %s%n", c.getId(), c.getNombre(), c.getDescripcion());
        }
    }

    // ─────────────────────────────────────────────────────────────
    //  MENU PRODUCTOS
    // ─────────────────────────────────────────────────────────────

    private static void menuProductos() {
        boolean volver = false;
        while (!volver) {
            System.out.println("\n--- PRODUCTOS ---");
            System.out.println("1. Alta de producto");
            System.out.println("2. Baja logica de producto");
            System.out.println("3. Modificacion de producto");
            System.out.println("4. Listado de productos activos");
            System.out.println("0. Volver");
            System.out.print("Seleccione una opcion: ");

            String opcion = scanner.nextLine().trim();
            switch (opcion) {
                case "1" -> altaProducto();
                case "2" -> bajaProducto();
                case "3" -> modificarProducto();
                case "4" -> listarProductos();
                case "0" -> volver = true;
                default -> System.out.println("Opcion invalida.");
            }
        }
    }

    private static void altaProducto() {
        System.out.println("\n-- Alta de Producto --");

        List<Categoria> categorias = categoriaRepo.listarActivos();
        if (categorias.isEmpty()) {
            System.out.println("No hay categorias activas. Debe crear una categoria primero.");
            return;
        }

        System.out.println("Categorias disponibles:");
        System.out.printf("%-6s %s%n", "ID", "Nombre");
        System.out.println("-".repeat(30));
        for (Categoria c : categorias) {
            System.out.printf("%-6d %s%n", c.getId(), c.getNombre());
        }

        System.out.print("Seleccione el ID de la categoria: ");
        Long catId = leerLong();
        if (catId == null) return;

        Optional<Categoria> optCat = categoriaRepo.buscarPorId(catId);
        if (optCat.isEmpty() || optCat.get().isEliminado()) {
            System.out.println("Error: categoria invalida.");
            return;
        }
        Categoria categoria = optCat.get();

        System.out.print("Nombre del producto: ");
        String nombre = scanner.nextLine().trim();
        if (nombre.isEmpty()) {
            System.out.println("Error: el nombre no puede estar vacio.");
            return;
        }

        System.out.print("Descripcion: ");
        String descripcion = scanner.nextLine().trim();

        System.out.print("Precio (mayor a 0): ");
        Double precio = leerDouble();
        if (precio == null || precio <= 0) {
            System.out.println("Error: el precio debe ser mayor a 0.");
            return;
        }

        System.out.print("Stock (mayor o igual a 0): ");
        Integer stock = leerInt();
        if (stock == null || stock < 0) {
            System.out.println("Error: el stock no puede ser negativo.");
            return;
        }

        Producto producto = Producto.builder()
                .nombre(nombre)
                .descripcion(descripcion)
                .precio(precio)
                .stock(stock)
                .disponible(true)
                .eliminado(false)
                .createdAt(LocalDateTime.now())
                .build();

        producto.setCategoria(categoria);

        Producto guardado = (Producto) productoRepo.guardar(producto);

        System.out.println("Producto creado con exito. ID generado: " + guardado.getId()
                + " | Categoria: " + categoria.getNombre());
    }

    private static void bajaProducto() {
        System.out.println("\n-- Baja Logica de Producto --");
        listarProductos();

        System.out.print("Ingrese el ID del producto a dar de baja: ");
        Long id = leerLong();
        if (id == null) return;

        Optional<Producto> opt = productoRepo.buscarPorId(id);
        if (opt.isEmpty() || opt.get().isEliminado()) {
            System.out.println("Error: no existe un producto activo con ese ID.");
            return;
        }

        String nombre = opt.get().getNombre();
        boolean resultado = productoRepo.eliminarLogico(id);
        if (resultado) {
            System.out.println("Producto \"" + nombre + "\" dado de baja correctamente.");
        } else {
            System.out.println("Error al dar de baja el producto.");
        }
    }

    private static void modificarProducto() {
        System.out.println("\n-- Modificacion de Producto --");
        listarProductos();

        System.out.print("Ingrese el ID del producto a modificar: ");
        Long id = leerLong();
        if (id == null) return;

        Optional<Producto> opt = productoRepo.buscarPorId(id);
        if (opt.isEmpty() || opt.get().isEliminado()) {
            System.out.println("Error: no existe un producto activo con ese ID.");
            return;
        }

        Producto producto = opt.get();
        System.out.println("Valores actuales:");
        System.out.println("  Nombre : " + producto.getNombre());
        System.out.println("  Precio : $" + producto.getPrecio());
        System.out.println("  Stock  : " + producto.getStock());

        System.out.print("Nuevo nombre (Enter para conservar): ");
        String nombre = scanner.nextLine().trim();
        if (!nombre.isEmpty()) {
            producto.setNombre(nombre);
        }

        System.out.print("Nuevo precio (Enter para conservar): ");
        String precioStr = scanner.nextLine().trim();
        if (!precioStr.isEmpty()) {
            try {
                double nuevoPrecio = Double.parseDouble(precioStr);
                if (nuevoPrecio <= 0) {
                    System.out.println("Error: el precio debe ser mayor a 0. Se conserva el valor anterior.");
                } else {
                    producto.setPrecio(nuevoPrecio);
                }
            } catch (NumberFormatException e) {
                System.out.println("Valor invalido. Se conserva el precio anterior.");
            }
        }

        System.out.print("Nuevo stock (Enter para conservar): ");
        String stockStr = scanner.nextLine().trim();
        if (!stockStr.isEmpty()) {
            try {
                int nuevoStock = Integer.parseInt(stockStr);
                if (nuevoStock < 0) {
                    System.out.println("Error: el stock no puede ser negativo. Se conserva el valor anterior.");
                } else {
                    producto.setStock(nuevoStock);
                }
            } catch (NumberFormatException e) {
                System.out.println("Valor invalido. Se conserva el stock anterior.");
            }
        }

        productoRepo.guardar(producto);
        System.out.println("Producto modificado correctamente.");
    }

    private static void listarProductos() {
        List<Producto> productos = productoRepo.listarActivos();
        if (productos.isEmpty()) {
            System.out.println("No hay productos activos.");
            return;
        }
        System.out.println("\n-- Productos activos --");
        System.out.printf("%-6s %-25s %-10s %-8s%n", "ID", "Nombre", "Precio", "Stock");
        System.out.println("-".repeat(55));
        for (Producto p : productos) {
            System.out.printf("%-6d %-25s $%-9.2f %-8d%n",
                    p.getId(), p.getNombre(), p.getPrecio(), p.getStock());
        }
    }

    // ─────────────────────────────────────────────────────────────
    //  MENU REPORTES
    // ─────────────────────────────────────────────────────────────

    private static void menuReportes() {
        boolean volver = false;
        while (!volver) {
            System.out.println("\n--- REPORTES ---");
            System.out.println("1. Productos por categoria");
            System.out.println("2. Pedidos por usuario");
            System.out.println("3. Pedidos por estado");
            System.out.println("4. Total facturado");
            System.out.println("0. Volver");
            System.out.print("Seleccione una opcion: ");

            String opcion = scanner.nextLine().trim();
            switch (opcion) {
                case "1" -> productosPorCategoria();
                case "2" -> pedidosPorUsuario();
                case "3" -> pedidosPorEstado();
                case "4" -> totalFacturado();
                case "0" -> volver = true;
                default -> System.out.println("Opcion invalida.");
            }
        }
    }

    private static void productosPorCategoria() {
        System.out.println("\n-- Productos por Categoria (JPQL) --");

        List<Categoria> categorias = categoriaRepo.listarActivos();
        if (categorias.isEmpty()) {
            System.out.println("No hay categorias activas.");
            return;
        }

        System.out.println("Categorias disponibles:");
        System.out.printf("%-6s %s%n", "ID", "Nombre");
        System.out.println("-".repeat(30));
        for (Categoria c : categorias) {
            System.out.printf("%-6d %s%n", c.getId(), c.getNombre());
        }

        System.out.print("Seleccione el ID de la categoria: ");
        Long id = leerLong();
        if (id == null) return;

        Optional<Categoria> optCat = categoriaRepo.buscarPorId(id);
        if (optCat.isEmpty() || optCat.get().isEliminado()) {
            System.out.println("Error: categoria invalida.");
            return;
        }

        List<Producto> productos = productoRepo.buscarPorCategoria(id);
        if (productos.isEmpty()) {
            System.out.println("No hay productos activos en la categoria \""
                    + optCat.get().getNombre() + "\".");
            return;
        }

        System.out.println("\nProductos en categoria \"" + optCat.get().getNombre() + "\":");
        System.out.printf("%-6s %-25s %-10s %-8s%n", "ID", "Nombre", "Precio", "Stock");
        System.out.println("-".repeat(55));
        for (Producto p : productos) {
            System.out.printf("%-6d %-25s $%-9.2f %-8d%n",
                    p.getId(), p.getNombre(), p.getPrecio(), p.getStock());
        }
    }

    private static void totalFacturado() {
        System.out.println("\n-- Total Facturado --");

        List<Pedido> terminados = pedidoRepo.buscarPorEstado(Estado.TERMINADO);

        double total = terminados.stream()
                .mapToDouble(p -> p.getTotal() == null ? 0.0 : p.getTotal())
                .sum();

        System.out.println("Pedidos terminados: " + terminados.size());
        System.out.println("Total facturado: " + String.format(Locale.US, "$%.2f", total));
    }


    // ─────────────────────────────────────────────────────────────
    //  MENU USUARIOS
    // ─────────────────────────────────────────────────────────────

    private static void menuUsuarios() {
        boolean volver = false;
        while (!volver) {
            System.out.println("\n--- USUARIOS ---");
            System.out.println("1. Alta de usuario");
            System.out.println("2. Modificar usuario");
            System.out.println("3. Baja logica de usuario");
            System.out.println("4. Listado de usuarios activos");
            System.out.println("5. Buscar por mail");
            System.out.println("0. Volver");
            System.out.print("Seleccione una opcion: ");

            String opcion = scanner.nextLine().trim();
            switch (opcion) {
                case "1" -> altaUsuario();
                case "2" -> modificarUsuario();
                case "3" -> bajaUsuario();
                case "4" -> listarUsuarios();
                case "5" -> buscarUsuarioPorMail();
                case "0" -> volver = true;
                default -> System.out.println("Opcion invalida.");
            }
        }
    }

    private static void altaUsuario() {
        System.out.println("\n-- Alta de Usuario --");

        System.out.print("Nombre: ");
        String nombre = scanner.nextLine().trim();
        if (nombre.isEmpty()) {
            System.out.println("Error: el nombre no puede estar vacio.");
            return;
        }

        System.out.print("Apellido: ");
        String apellido = scanner.nextLine().trim();
        if (apellido.isEmpty()) {
            System.out.println("Error: el apellido no puede estar vacio.");
            return;
        }

        System.out.print("Mail: ");
        String mail = scanner.nextLine().trim();
        if (mail.isEmpty()) {
            System.out.println("Error: el mail no puede estar vacio.");
            return;
        }

        Optional<Usuario> existente = usuarioRepo.buscarPorMail(mail);
        if (existente.isPresent()) {
            System.out.println("Error: ya existe un usuario activo con ese mail.");
            return;
        }

        System.out.print("Celular (opcional): ");
        String celular = scanner.nextLine().trim();

        System.out.print("Contrasena: ");
        String contrasena = scanner.nextLine().trim();
        if (contrasena.isEmpty()) {
            System.out.println("Error: la contrasena no puede estar vacia.");
            return;
        }

        Rol rol = seleccionarRol();
        if (rol == null) return;

        Usuario usuario = Usuario.builder()
                .nombre(nombre)
                .apellido(apellido)
                .mail(mail)
                .celular(celular)
                .contrasena(contrasena)
                .rol(rol)
                .eliminado(false)
                .createdAt(LocalDateTime.now())
                .build();

        Usuario guardado = usuarioRepo.guardar(usuario);
        System.out.println("Usuario creado con exito. ID generado: " + guardado.getId());
    }

    private static void modificarUsuario() {
        System.out.println("\n-- Modificacion de Usuario --");
        listarUsuarios();

        System.out.print("Ingrese el ID del usuario a modificar: ");
        Long id = leerLong();
        if (id == null) return;

        Optional<Usuario> opt = usuarioRepo.buscarPorId(id);
        if (opt.isEmpty() || opt.get().isEliminado()) {
            System.out.println("Error: no existe un usuario activo con ese ID.");
            return;
        }

        Usuario usuario = opt.get();
        System.out.println("Valores actuales:");
        System.out.println("  Nombre  : " + usuario.getNombre());
        System.out.println("  Apellido: " + usuario.getApellido());
        System.out.println("  Mail    : " + usuario.getMail());
        System.out.println("  Celular : " + usuario.getCelular());

        System.out.print("Nuevo nombre (Enter para conservar): ");
        String nombre = scanner.nextLine().trim();
        if (!nombre.isEmpty()) {
            usuario.setNombre(nombre);
        }

        System.out.print("Nuevo apellido (Enter para conservar): ");
        String apellido = scanner.nextLine().trim();
        if (!apellido.isEmpty()) {
            usuario.setApellido(apellido);
        }

        System.out.print("Nuevo celular (Enter para conservar): ");
        String celular = scanner.nextLine().trim();
        if (!celular.isEmpty()) {
            usuario.setCelular(celular);
        }

        System.out.print("Nueva contrasena (Enter para conservar): ");
        String contrasena = scanner.nextLine().trim();
        if (!contrasena.isEmpty()) {
            usuario.setContrasena(contrasena);
        }

        System.out.print("Nuevo mail (Enter para conservar): ");
        String mail = scanner.nextLine().trim();
        if (!mail.isEmpty()) {
            Optional<Usuario> existente = usuarioRepo.buscarPorMail(mail);
            if (existente.isPresent() && !existente.get().getId().equals(usuario.getId())) {
                System.out.println("Error: ese mail ya esta en uso por otro usuario. Se conserva el mail anterior.");
            } else {
                usuario.setMail(mail);
            }
        }

        usuarioRepo.guardar(usuario);
        System.out.println("Usuario modificado correctamente.");
    }

    private static void bajaUsuario() {
        System.out.println("\n-- Baja Logica de Usuario --");
        listarUsuarios();

        System.out.print("Ingrese el ID del usuario a dar de baja: ");
        Long id = leerLong();
        if (id == null) return;

        Optional<Usuario> opt = usuarioRepo.buscarPorId(id);
        if (opt.isEmpty() || opt.get().isEliminado()) {
            System.out.println("Error: no existe un usuario activo con ese ID.");
            return;
        }

        String nombreCompleto = opt.get().getNombre() + " " + opt.get().getApellido();
        boolean resultado = usuarioRepo.eliminarLogico(id);
        if (resultado) {
            System.out.println("Usuario \"" + nombreCompleto + "\" dado de baja correctamente.");
            System.out.println("Sus pedidos permanecen en el sistema sin modificacion.");
        } else {
            System.out.println("Error al dar de baja el usuario.");
        }
    }

    private static void listarUsuarios() {
        List<Usuario> usuarios = usuarioRepo.listarActivos();
        if (usuarios.isEmpty()) {
            System.out.println("No hay usuarios activos.");
            return;
        }
        System.out.println("\n-- Usuarios activos --");
        System.out.printf("%-6s %-25s %-25s %-10s%n", "ID", "Nombre completo", "Mail", "Rol");
        System.out.println("-".repeat(70));
        for (Usuario u : usuarios) {
            System.out.printf("%-6d %-25s %-25s %-10s%n",
                    u.getId(), u.getNombre() + " " + u.getApellido(), u.getMail(), u.getRol());
        }
    }

    private static void buscarUsuarioPorMail() {
        System.out.println("\n-- Buscar Usuario por Mail --");
        System.out.print("Mail: ");
        String mail = scanner.nextLine().trim();

        Optional<Usuario> opt = usuarioRepo.buscarPorMail(mail);
        if (opt.isEmpty()) {
            System.out.println("No existe usuario activo con ese mail.");
            return;
        }

        Usuario u = opt.get();
        System.out.println("\n-- Datos del usuario --");
        System.out.println("  ID      : " + u.getId());
        System.out.println("  Nombre  : " + u.getNombre() + " " + u.getApellido());
        System.out.println("  Mail    : " + u.getMail());
        System.out.println("  Celular : " + u.getCelular());
        System.out.println("  Rol     : " + u.getRol());
    }

    private static Rol seleccionarRol() {
        System.out.println("Seleccione el rol:");
        System.out.println("1. ADMIN");
        System.out.println("2. USUARIO");
        System.out.print("Opcion: ");
        String opcion = scanner.nextLine().trim();
        return switch (opcion) {
            case "1" -> Rol.ADMIN;
            case "2" -> Rol.USUARIO;
            default -> {
                System.out.println("Opcion invalida.");
                yield null;
            }
        };
    }


    // ─────────────────────────────────────────────────────────────
    //  MENU PEDIDOS
    // ─────────────────────────────────────────────────────────────

    private static void menuPedidos() {
        boolean volver = false;
        while (!volver) {
            System.out.println("\n--- PEDIDOS ---");
            System.out.println("1. Alta de pedido");
            System.out.println("2. Cambiar estado de pedido");
            System.out.println("3. Baja logica de pedido");
            System.out.println("4. Listado de pedidos activos");
            System.out.println("5. Pedidos por usuario");
            System.out.println("6. Pedidos por estado");
            System.out.println("0. Volver");
            System.out.print("Seleccione una opcion: ");

            String opcion = scanner.nextLine().trim();
            switch (opcion) {
                case "1" -> altaPedido();
                case "2" -> cambiarEstadoPedido();
                case "3" -> bajaPedido();
                case "4" -> listarPedidos();
                case "5" -> pedidosPorUsuario();
                case "6" -> pedidosPorEstado();
                case "0" -> volver = true;
                default -> System.out.println("Opcion invalida.");
            }
        }
    }

    private static void cambiarEstadoPedido() {
        System.out.println("\n-- Cambiar Estado de Pedido --");
        listarPedidos();

        System.out.print("Ingrese el ID del pedido: ");
        Long id = leerLong();
        if (id == null) return;

        Optional<Pedido> opt = pedidoRepo.buscarPorId(id);
        if (opt.isEmpty() || opt.get().isEliminado()) {
            System.out.println("Error: no existe un pedido activo con ese ID.");
            return;
        }

        Pedido pedido = opt.get();
        System.out.println("Estado actual: " + pedido.getEstado());

        Estado nuevoEstado = seleccionarEstado();
        if (nuevoEstado == null) return;

        pedido.setEstado(nuevoEstado);
        pedidoRepo.guardar(pedido);
        System.out.println("Pedido #" + pedido.getId() + " actualizado al estado " + nuevoEstado);
    }

    private static void bajaPedido() {
        System.out.println("\n-- Baja Logica de Pedido --");
        listarPedidos();

        System.out.print("Ingrese el ID del pedido a dar de baja: ");
        Long id = leerLong();
        if (id == null) return;

        Optional<Pedido> opt = pedidoRepo.buscarPorId(id);
        if (opt.isEmpty() || opt.get().isEliminado()) {
            System.out.println("Error: no existe un pedido activo con ese ID.");
            return;
        }

        Double total = opt.get().getTotal();
        boolean resultado = pedidoRepo.eliminarLogico(id);
        if (resultado) {
            System.out.println("Pedido #" + id + " dado de baja correctamente. Total: "
                    + String.format(Locale.US, "$%.2f", total == null ? 0.0 : total));
            System.out.println("El stock de los productos NO se restaura.");
        } else {
            System.out.println("Error al dar de baja el pedido.");
        }
    }

    private static void listarPedidos() {
        List<Pedido> pedidos = pedidoRepo.listarActivos();
        if (pedidos.isEmpty()) {
            System.out.println("No hay pedidos activos.");
            return;
        }
        System.out.println("\n-- Pedidos activos --");
        System.out.printf("%-6s %-12s %-12s %-14s %-20s %-10s%n",
                "ID", "Fecha", "Estado", "Forma Pago", "Usuario", "Total");
        System.out.println("-".repeat(80));
        for (Pedido p : pedidos) {
            String nombreUsuario = p.getUsuario() != null
                    ? p.getUsuario().getNombre() + " " + p.getUsuario().getApellido()
                    : "N/A";
            System.out.printf("%-6d %-12s %-12s %-14s %-20s $%-9.2f%n",
                    p.getId(), p.getFecha(), p.getEstado(), p.getFormaPago(),
                    nombreUsuario, p.getTotal() == null ? 0.0 : p.getTotal());
        }
    }

    private static void pedidosPorUsuario() {
        System.out.println("\n-- Pedidos por Usuario --");
        List<Usuario> usuarios = usuarioRepo.listarActivos();
        if (usuarios.isEmpty()) {
            System.out.println("No hay usuarios activos.");
            return;
        }

        System.out.println("Usuarios disponibles:");
        System.out.printf("%-6s %s%n", "ID", "Nombre completo");
        System.out.println("-".repeat(40));
        for (Usuario u : usuarios) {
            System.out.printf("%-6d %s%n", u.getId(), u.getNombre() + " " + u.getApellido());
        }

        System.out.print("Seleccione el ID del usuario: ");
        Long idUsuario = leerLong();
        if (idUsuario == null) return;

        List<Pedido> pedidos = pedidoRepo.buscarPorUsuario(idUsuario);
        if (pedidos.isEmpty()) {
            System.out.println("El usuario no tiene pedidos activos.");
            return;
        }

        System.out.println("\nPedidos del usuario:");
        System.out.printf("%-6s %-12s %-12s %-10s%n", "ID", "Fecha", "Estado", "Total");
        System.out.println("-".repeat(50));
        for (Pedido p : pedidos) {
            System.out.printf("%-6d %-12s %-12s $%-9.2f%n",
                    p.getId(), p.getFecha(), p.getEstado(), p.getTotal() == null ? 0.0 : p.getTotal());
        }
    }

    private static void pedidosPorEstado() {
        System.out.println("\n-- Pedidos por Estado --");
        Estado estado = seleccionarEstado();
        if (estado == null) return;

        List<Pedido> pedidos = pedidoRepo.buscarPorEstado(estado);
        if (pedidos.isEmpty()) {
            System.out.println("No hay pedidos activos con estado " + estado + ".");
            return;
        }

        System.out.println("\nPedidos con estado " + estado + ":");
        System.out.printf("%-6s %-12s %-20s %-10s%n", "ID", "Fecha", "Usuario", "Total");
        System.out.println("-".repeat(60));
        for (Pedido p : pedidos) {
            String nombreUsuario = p.getUsuario() != null
                    ? p.getUsuario().getNombre() + " " + p.getUsuario().getApellido()
                    : "N/A";
            System.out.printf("%-6d %-12s %-20s $%-9.2f%n",
                    p.getId(), p.getFecha(), nombreUsuario, p.getTotal() == null ? 0.0 : p.getTotal());
        }
    }

    private static Estado seleccionarEstado() {
        System.out.println("Seleccione el estado:");
        System.out.println("1. PENDIENTE");
        System.out.println("2. CONFIRMADO");
        System.out.println("3. TERMINADO");
        System.out.println("4. CANCELADO");
        System.out.print("Opcion: ");
        String opcion = scanner.nextLine().trim();
        return switch (opcion) {
            case "1" -> Estado.PENDIENTE;
            case "2" -> Estado.CONFIRMADO;
            case "3" -> Estado.TERMINADO;
            case "4" -> Estado.CANCELADO;
            default -> {
                System.out.println("Opcion invalida.");
                yield null;
            }
        };
    }

    private static FormaPago seleccionarFormaPago() {
        System.out.println("Seleccione la forma de pago:");
        System.out.println("1. TARJETA");
        System.out.println("2. TRANSFERENCIA");
        System.out.println("3. EFECTIVO");
        System.out.print("Opcion: ");
        String opcion = scanner.nextLine().trim();
        return switch (opcion) {
            case "1" -> FormaPago.TARJETA;
            case "2" -> FormaPago.TRANSFERENCIA;
            case "3" -> FormaPago.EFECTIVO;
            default -> {
                System.out.println("Opcion invalida.");
                yield null;
            }
        };
    }

    // Item temporal en memoria: solo guarda el ID del producto y la cantidad,
    // no entidades JPA. Se usa durante la fase de selección, antes de abrir la transacción.
    private record ItemPedido(Long productoId, int cantidad) {}

    private static void altaPedido() {
        System.out.println("\n-- Alta de Pedido --");

        // 1 Seleccionar usuario activo
        List<Usuario> usuarios = usuarioRepo.listarActivos();
        if (usuarios.isEmpty()) {
            System.out.println("No hay usuarios activos. No se puede crear el pedido.");
            return;
        }
        System.out.println("Usuarios disponibles:");
        System.out.printf("%-6s %s%n", "ID", "Nombre completo");
        System.out.println("-".repeat(40));
        for (Usuario u : usuarios) {
            System.out.printf("%-6d %s%n", u.getId(), u.getNombre() + " " + u.getApellido());
        }
        System.out.print("Seleccione el ID del usuario: ");
        Long idUsuario = leerLong();
        if (idUsuario == null) return;

        Optional<Usuario> optUsuario = usuarioRepo.buscarPorId(idUsuario);
        if (optUsuario.isEmpty() || optUsuario.get().isEliminado()) {
            System.out.println("Error: usuario invalido.");
            return;
        }

        // 2 Seleccionar forma de pago
        FormaPago formaPago = seleccionarFormaPago();
        if (formaPago == null) return;

        // 3 Ingresar productos al pedido (ciclo repetible, todo en memoria
        List<ItemPedido> itemsTemporales = new ArrayList<>();
        boolean seguirAgregando = true;
        while (seguirAgregando) {
            List<Producto> productos = productoRepo.listarActivos();
            if (productos.isEmpty()) {
                System.out.println("No hay productos activos en el catalogo.");
                break;
            }

            System.out.println("\nCatalogo de productos:");
            System.out.printf("%-6s %-25s %-10s %-8s%n", "ID", "Nombre", "Precio", "Stock");
            System.out.println("-".repeat(55));
            for (Producto p : productos) {
                System.out.printf("%-6d %-25s $%-9.2f %-8d%n",
                        p.getId(), p.getNombre(), p.getPrecio(), p.getStock());
            }

            System.out.print("ID del producto a agregar (0 para cancelar): ");
            Long idProducto = leerLong();
            if (idProducto == null) continue;
            if (idProducto == 0) break;

            Optional<Producto> optProducto = productoRepo.buscarPorId(idProducto);
            if (optProducto.isEmpty() || optProducto.get().isEliminado()) {
                System.out.println("Error: el producto no existe o esta dado de baja.");
                continue;
            }

            Producto producto = optProducto.get();
            if (!Boolean.TRUE.equals(producto.getDisponible())) {
                System.out.println("Error: el producto no esta disponible.");
                continue;
            }

            System.out.print("Cantidad: ");
            Integer cantidad = leerInt();
            if (cantidad == null || cantidad <= 0) {
                System.out.println("Error: la cantidad debe ser mayor a 0.");
                continue;
            }

            // Validamos contra el stock actual sumando lo que ya se eligió de este mismo producto
            int yaElegido = itemsTemporales.stream()
                    .filter(i -> i.productoId().equals(idProducto))
                    .mapToInt(ItemPedido::cantidad)
                    .sum();
            if (cantidad + yaElegido > producto.getStock()) {
                System.out.println("Error: stock insuficiente. Stock disponible: "
                        + (producto.getStock() - yaElegido));
                continue;
            }

            itemsTemporales.add(new ItemPedido(idProducto, cantidad));
            System.out.println("Producto agregado al pedido (en memoria, aun no guardado).");

            System.out.print("Desea agregar otro producto? (S/N): ");
            String resp = scanner.nextLine().trim();
            seguirAgregando = resp.equalsIgnoreCase("S");
        }

        if (itemsTemporales.isEmpty()) {
            System.out.println("El pedido debe tener al menos un detalle. Operacion cancelada.");
            return;
        }

        // 4 Fase transaccional: un unico EntityManager, una unica transaccion
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();

            Usuario usuarioGestionado = em.find(Usuario.class, idUsuario);

            Pedido pedido = Pedido.builder()
                    .usuario(usuarioGestionado)
                    .fecha(LocalDate.now())
                    .estado(Estado.PENDIENTE)
                    .formaPago(formaPago)
                    .eliminado(false)
                    .createdAt(LocalDateTime.now())
                    .build();

            for (ItemPedido item : itemsTemporales) {
                Producto productoGestionado = em.find(Producto.class, item.productoId());
                if (productoGestionado == null) {
                    throw new IllegalStateException("Producto " + item.productoId() + " no encontrado.");
                }
                if (item.cantidad() > productoGestionado.getStock()) {
                    throw new IllegalStateException("Stock insuficiente para " + productoGestionado.getNombre());
                }

                pedido.addDetallePedido(item.cantidad(), productoGestionado);
                productoGestionado.setStock(productoGestionado.getStock() - item.cantidad());
            }

            pedido.calcularTotal();
            em.persist(pedido);

            em.getTransaction().commit();

            // 5. Mostrar resumen de la operacion exitosa
            System.out.println("\nPedido creado con exito.");
            System.out.println("ID generado : " + pedido.getId());
            System.out.println("Fecha       : " + pedido.getFecha());
            System.out.println("Usuario     : " + usuarioGestionado.getNombre() + " " + usuarioGestionado.getApellido());
            System.out.println("Forma pago  : " + pedido.getFormaPago());
            System.out.println("Productos:");
            for (DetallePedido d : pedido.getDetalles()) {
                System.out.printf("  %-25s x%-4d $%-9.2f%n",
                        d.getProducto().getNombre(), d.getCantidad(), d.getSubtotal());
            }
            System.out.println("Total       : " + String.format(Locale.US, "$%.2f", pedido.getTotal()));

        } catch (Exception err) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.out.println("Error al crear el pedido: " + err.getMessage());
            System.out.println("Se realizo rollback. No se modifico nada en la base de datos.");
        } finally {
            em.close();
        }
    }

    // ─────────────────────────────────────────────────────────────
    //  HELPERS DE LECTURA
    // ─────────────────────────────────────────────────────────────

    private static Long leerLong() {
        try {
            return Long.parseLong(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Error: debe ingresar un numero entero valido.");
            return null;
        }
    }

    private static Double leerDouble() {
        try {
            return Double.parseDouble(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Error: debe ingresar un numero valido.");
            return null;
        }
    }

    private static Integer leerInt() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Error: debe ingresar un numero entero valido.");
            return null;
        }
    }
}
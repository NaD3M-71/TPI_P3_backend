# Food Store — Sistema de Gestión de Pedidos de Comida

**Tecnicatura Universitaria en Programación — UTN**
**Materia:** Programación III
**Trabajo Final Integrador**
**Estudiante:** Scaglioni Giuliano
**Comisión:** 11

---

## Descripción del proyecto

Food Store es un sistema de gestión de pedidos para un negocio de comidas, desarrollado como Trabajo Final Integrador de Programación III. El proyecto está compuesto por dos partes independientes que comparten el mismo dominio:

- **Backend** (este repositorio en `Parcial2/` o raíz del proyecto Java): aplicación de consola en Java con persistencia mediante JPA/Hibernate y base de datos H2 en archivo. Gestiona Categorías, Productos, Usuarios y Pedidos con sus respectivos Detalles.
- **Frontend** *(en carpeta separada — ver sección Frontend más abajo cuando esté disponible)*: aplicación web en TypeScript + Vite que simula la experiencia de cliente y administrador, consumiendo datos desde archivos JSON estáticos y `localStorage`.

> **Nota:** backend y frontend son entregables independientes que no se comunican entre sí. El frontend no llama a una API expuesta por este backend; cada uno cumple el mismo backlog de historias de usuario a su manera (consola vs. web).

---

## Backend — Tecnologías utilizadas

- Java 21
- Gradle (con wrapper incluido)
- Hibernate ORM 6.4.4 (Jakarta Persistence 3.0)
- H2 Database (modo archivo)
- Lombok

---

## Backend — Modelo de dominio

El modelo se compone de una clase abstracta `Base` (`@MappedSuperclass`, con `id`, `eliminado`, `createdAt`), la interfaz `Calculable`, y cinco entidades: `Categoria`, `Producto`, `Usuario`, `Pedido` y `DetallePedido`.

**Relaciones:**
- `Producto → Categoria`: asociación unidireccional (`@ManyToOne`). Categoria no mantiene lista de productos.
- `Pedido → Usuario`: asociación unidireccional (`@ManyToOne`). Usuario no mantiene lista de pedidos.
- `Pedido ↔ DetallePedido`: composición bidireccional (`@OneToMany(mappedBy="pedido")` / `@ManyToOne`), con cascade `ALL` y `orphanRemoval`. Los `DetallePedido` se crean exclusivamente a través de `Pedido.addDetallePedido()`.
- `DetallePedido → Producto`: asociación unidireccional (`@ManyToOne`).

Las bajas son siempre lógicas (`eliminado = true`); ningún registro se borra físicamente.

---

## Backend — Estructura del proyecto

src/main/java/programacion3/

├── entities/

│   ├── Base.java

│   ├── Categoria.java

│   ├── Producto.java

│   ├── Usuario.java

│   ├── Pedido.java

│   └── DetallePedido.java

├── enums/

│   ├── Rol.java

│   ├── Estado.java

│   └── FormaPago.java

├── interfaces/

│   └── Calculable.java

├── repository/

│   ├── Baserepository.java       ← CRUD genérico (persist/merge según id, buscarPorId, listarActivos, eliminarLogico)

│   ├── CategoriaRepository.java

│   ├── ProductoRepository.java   ← + buscarPorCategoria (JPQL)

│   ├── UsuarioRepository.java    ← + buscarPorMail (JPQL)

│   └── PedidoRepository.java     ← + buscarPorUsuario, buscarPorEstado (JPQL)

├── util/

│   └── JPAUtil.java               ← Singleton del EntityManagerFactory

└── Main.java                      ← Menú de consola (Categorías, Productos, Usuarios, Pedidos, Reportes)
src/main/resources/META-INF/

└── persistence.xml                ← Configuración de H2 en modo archivo

---

## Backend — Funcionalidades implementadas

- **Categorías:** alta, modificación (conservando valores en blanco), baja lógica, listado de activas.
- **Productos:** alta asociado a categoría, modificación con validación de precio/stock, baja lógica, listado de activos.
- **Usuarios:** alta con validación de mail único, modificación, baja lógica, listado, búsqueda por mail.
- **Pedidos:**
   - Alta transaccional: selección de usuario y forma de pago, carga de múltiples productos con validación de stock y disponibilidad, todo confirmado en una única transacción atómica (rollback completo ante cualquier error).
   - Cambio de estado (PENDIENTE → CONFIRMADO → TERMINADO / CANCELADO).
   - Baja lógica (sin restaurar stock).
   - Listados, filtro por usuario, filtro por estado.
- **Reportes:** productos por categoría, pedidos por usuario, pedidos por estado, total facturado (suma de pedidos en estado TERMINADO).

---

## Backend — Configuración de la base de datos

La base de datos H2 se ejecuta en **modo archivo** y se crea automáticamente al iniciar la aplicación por primera vez. No requiere instalación ni configuración manual de un servidor de base de datos.

- **Ubicación del archivo:** `./data/jpa_db.mv.db` (relativo a la raíz del proyecto).
- **Esquema:** gestionado automáticamente por Hibernate (`hibernate.hbm2ddl.auto=update`), por lo que las tablas se crean/actualizan solas según las entidades.
- **Configuración:** `src/main/resources/META-INF/persistence.xml`.
- **Para reiniciar la base desde cero:** cerrar la aplicación y eliminar la carpeta `data/` antes de volver a ejecutar.

---

## Backend — Instrucciones para ejecutar

### Requisitos previos

- **JDK 21** instalado y configurado.
- **IntelliJ IDEA** (recomendado) o cualquier IDE con soporte para Gradle y Lombok.
- Plugin de **Lombok** habilitado en el IDE (con *annotation processing* activado).

### Pasos (desde IntelliJ IDEA)

1. Abrir la carpeta del proyecto en IntelliJ (`File → Open`, seleccionar la raíz donde está `build.gradle`).
2. Esperar a que IntelliJ sincronice Gradle automáticamente (descarga Hibernate, H2 y Lombok).
3. Verificar que el plugin de Lombok esté instalado y que *Annotation Processing* esté habilitado (`Settings → Build, Execution, Deployment → Compiler → Annotation Processors`).
4. Ejecutar la clase `com.tpi.jpa.Main` (botón ▶ junto al método `main`).
5. Interactuar con el sistema a través del menú de consola.

### Pasos alternativos (línea de comandos, sin IDE)

```bash
# Compilar
./gradlew build        # En Windows: gradlew.bat build

# Ejecutar (ajustar el classpath de dependencias según corresponda)
java -cp "build/classes/java/main:<ruta-a-las-dependencias>" com.tpi.jpa.Main
```

> Nota: el proyecto no incluye el plugin `application` de Gradle, por lo que `./gradlew run` no está disponible; se recomienda ejecutar desde IntelliJ.

---

## Notas de desarrollo

- Las entidades usan Lombok (`@Data`, `@SuperBuilder`, etc.) para reducir código repetitivo de getters/setters/builders.
- El repositorio genérico (`Baserepository<T extends Base>`) centraliza el CRUD común; cada repositorio específico solo agrega las consultas JPQL propias de su entidad.
- El alta de pedido separa la fase de selección (en memoria, sin tocar la base) de la fase de persistencia (una única transacción), evitando dejar datos a medio guardar si el operador cancela o se produce un error.

---

## Frontend

*(Pendiente — se documentará en esta sección una vez incorporado el proyecto TypeScript/Vite)*

---

## Entrega

- **Código fuente:** este repositorio.
- **Documentación PDF:** https://drive.google.com/file/d/1HGQ1qUvmgPg_SidLk4W-iFF8WOIj4H8k/view?usp=sharing
- **Video demostrativo:** https://drive.google.com/file/d/1f5QnESm7NXe1vbNG4SCuAXfWabH5GBWf/view?usp=sharing

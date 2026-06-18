# Segundo Parcial

**Tecnicatura Universitaria en Programación — UTN**  
**Materia:** Programación III  
**Alumno:** Scaglioni Giuliano
**Comisión:** 11
**Link al video:** https://drive.google.com/file/d/18_IIjO0P8c76DK96gW4AWfEBDEbHrWps/view?usp=sharing
---

## Descripción del proyecto

Extensión del TP de la Unidad 8 sobre **Java Persistence API (JPA)**. Se agregaron repositorios genéricos y específicos para las entidades `Categoria` y `Producto`, junto con un menú de consola interactivo que permite realizar operaciones ABM (Alta, Baja y Modificación) sobre dichas entidades. Incluye además una consulta JPQL personalizada para filtrar productos por categoría.

### Archivos nuevos creados (sin modificar el TP base)

| Archivo | Paquete | Descripción |
|---|---|---|
| `JPAUtil.java` | `programacion3.util` | Singleton que provee el `EntityManagerFactory` |
| `BaseRepository.java` | `programacion3.repository` | Repositorio genérico `<T>` con CRUD: `guardar`, `buscarPorId`, `listarActivos`, `eliminarLogico` |
| `CategoriaRepository.java` | `programacion3.repository` | Extiende `BaseRepository<Categoria>` |
| `ProductoRepository.java` | `programacion3.repository` | Extiende `BaseRepository<Producto>` y agrega `buscarPorCategoria` con JPQL |
| `Main.java` | `programacion3` | Menú principal de consola con submenús de Categorías, Productos y Reportes |

### Funcionalidades implementadas

- **Categorías:** alta, baja lógica, modificación y listado de activas
- **Productos:** alta con selección de categoría, baja lógica, modificación y listado de activos
- **Reportes:** consulta JPQL de productos activos filtrados por categoría (`buscarPorCategoria`)

---

## Tecnologías utilizadas

- Java 21
- Gradle
- Hibernate 6.4.4 (JPA 3.0)
- H2 Database (archivo local)
- Lombok

---

## Estructura del proyecto

```
src/main/java/programacion3/
├── entities/          ← TP base (minimamente modificado)
│   ├── Base.java
│   ├── Categoria.java
│   ├── Producto.java
│   └── ...
├── util/
│   └── JPAUtil.java   ← NUEVO
├── repository/
│   ├── BaseRepository.java      ← NUEVO
│   ├── CategoriaRepository.java ← NUEVO
│   └── ProductoRepository.java  ← NUEVO
└── Main.java          ← NUEVO (reemplaza el main vacío del TP base)

src/main/resources/META-INF/
└── persistence.xml    ← TP base (no modificado)
```

---

## Instrucciones para ejecutar

### Requisitos previos

- **Java 21** instalado y configurado en el `PATH`
- **Gradle** (o usar el wrapper incluido `./gradlew`)

### Pasos

1. **Descomprimir** el proyecto en una carpeta local.

2. **Abrir una terminal** en la raíz del proyecto (donde está `build.gradle`).

3. **Compilar el proyecto:**

   ```bash
   ./gradlew build
   ```
   En Windows:
   ```cmd
   gradlew.bat build
   ```

4. **Ejecutar la aplicación:**

   ```bash
   ./gradlew run
   ```
   O bien, si no está configurado el plugin `application`, ejecutar directamente:
   ```bash
   java -cp build/libs/*.jar programacion3.Main
   ```

5. La base de datos H2 se crea automáticamente en `./data/jpa_db.mv.db` al iniciar la aplicación por primera vez.

### Notas

- La base de datos es **persistente entre ejecuciones** (archivo H2 en `./data/`).
- Para empezar desde cero, basta con eliminar la carpeta `data/`.
- Hibernate está configurado con `hbm2ddl.auto=update`, por lo que las tablas se crean o actualizan automáticamente.
### Dificultades encontradas y resolución

Durante el desarrollo se presentó un problema relacionado con la gestión de relaciones en JPA/Hibernate. Inicialmente se había modelado la relación entre `Categoria` y `Producto` como `ManyToMany`, utilizando una colección con carga diferida (`LAZY`).

Al intentar asociar productos a una categoría luego de recuperarla desde el repositorio, se producía una excepción `LazyInitializationException`. Tras analizar el comportamiento de Hibernate, se determinó que la causa era el acceso a una colección no inicializada una vez cerrado el `EntityManager`.

Como parte del proceso de revisión del diseño, se reevaluó el modelo de datos y se observó que el dominio del problema no requería una relación `ManyToMany`, ya que cada producto pertenece a una única categoría mientras que una categoría puede contener múltiples productos.

Por este motivo, la relación fue rediseñada utilizando `OneToMany / ManyToOne`, obteniendo un modelo más simple, coherente con el negocio y evitando la necesidad de una tabla intermedia para gestionar las asociaciones.

Esta situación permitió profundizar en conceptos importantes de JPA como el ciclo de vida de las entidades, las estrategias de carga (`LAZY` y `EAGER`) y el funcionamiento de las relaciones entre entidades.
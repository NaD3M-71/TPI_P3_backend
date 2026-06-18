package programacion3.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.util.HashSet;
import java.util.Set;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false, of = "nombre")
@Entity
@Table(name = "categorias")
public class Categoria extends Base {
    @Column(nullable = false)
    private String nombre;
    private String descripcion;

}
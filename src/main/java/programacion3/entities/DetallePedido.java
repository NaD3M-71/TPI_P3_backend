package programacion3.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true, exclude = "pedido")
@EqualsAndHashCode(callSuper = false, of = "cantidad")
@Entity
@Table(name="detalle_pedido")
public class DetallePedido extends Base {
    @Column(nullable = false)
    private int cantidad;

    @Column(nullable = false)
    private Double subtotal;

    @ManyToOne
    @JoinColumn(name="producto_id", nullable = false)
    private Producto producto;

    @ManyToOne
    @JoinColumn(name="pedido_id")
    private Pedido pedido;
}
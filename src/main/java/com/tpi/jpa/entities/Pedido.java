package com.tpi.jpa.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import com.tpi.jpa.enums.Estado;
import com.tpi.jpa.enums.FormaPago;
import com.tpi.jpa.interfaces.Calculable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name="pedidos")
public class Pedido extends Base implements Calculable {
    @Column(nullable = false)
    private LocalDate fecha;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Estado estado;

    private Double total;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FormaPago formaPago;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DetallePedido> detalles = new ArrayList<>();

    // En Pedido.java
    public void addDetallePedido(int cantidad, Producto producto) {
        DetallePedido detalle = DetallePedido.builder()
                .cantidad(cantidad)
                .subtotal(producto.getPrecio() * cantidad)
                .producto(producto)
                .build();
        detalle.setPedido(this);
        detalles.add(detalle);
    }


    public DetallePedido findDetallePedidoByProducto(Producto producto) {
        return detalles.stream()
                .filter(d -> d.getProducto().equals(producto))
                .findFirst()
                .orElse(null);
    }

    public void deleteDetallePedidoByProducto(Producto producto) {
        detalles.removeIf(d -> d.getProducto().equals(producto));
        calcularTotal();
    }

    @Override
    public void calcularTotal() {
        total = detalles.stream()
                .mapToDouble(d -> d.getSubtotal())
                .sum();
    }
    public int calcularCantidadItems() {
        return detalles.stream()
                .mapToInt(d -> d.getCantidad())
                .sum();
    }
}
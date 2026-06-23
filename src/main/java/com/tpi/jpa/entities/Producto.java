package com.tpi.jpa.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false, of = "nombre")
@Entity
@Table(name = "productos")
public class Producto extends Base {
    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private Double precio;

    private String descripcion;

    @Column(nullable = false)
    private int stock;

    private String imagen;

    @Column(nullable = false)
    private Boolean disponible;

    @ManyToOne
    @JoinColumn(name="categoria_id")
    private Categoria categoria;
}
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
@Table(name = "categorias")
public class Categoria extends Base {
    @Column(nullable = false)
    private String nombre;
    private String descripcion;

}
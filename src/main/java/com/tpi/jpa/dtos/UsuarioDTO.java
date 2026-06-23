package com.tpi.jpa.dtos;

import com.tpi.jpa.entities.Pedido;
import java.util.Set;

public record UsuarioDTO(
        Long id,
        String nombre,
        String apellido,
        String mail,
        String celular,
        Set<Pedido> pedidos
) {}
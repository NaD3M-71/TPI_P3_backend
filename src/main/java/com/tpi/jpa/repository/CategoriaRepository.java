package com.tpi.jpa.repository;

import com.tpi.jpa.entities.Categoria;

public class CategoriaRepository extends Baserepository<Categoria> {

    public CategoriaRepository() {
        super(Categoria.class);
    }
}

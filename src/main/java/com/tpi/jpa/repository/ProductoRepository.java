package com.tpi.jpa.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import com.tpi.jpa.entities.Producto;

import java.util.List;

public class ProductoRepository extends Baserepository<Producto>{
    public ProductoRepository() {
        super(Producto.class);
    }

    // Busca todos los productos activos de la categoria
    public List<Producto> buscarPorCategoria(Long categoriaId){
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Producto> query = em.createQuery(
                    "SELECT p FROM Producto p WHERE p.categoria.id = :categoriaId AND p.eliminado = false",
                    Producto.class);
            query.setParameter("categoriaId", categoriaId);
            return query.getResultList();
        }finally{em.close();}
    }
}

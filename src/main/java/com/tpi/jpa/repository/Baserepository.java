package com.tpi.jpa.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import com.tpi.jpa.entities.Base;
import com.tpi.jpa.util.JPAUtil;

import java.util.List;
import java.util.Optional;

public abstract class Baserepository<T extends Base> {
    protected final Class<T> clazz;
    protected final EntityManagerFactory emf;

    public Baserepository(Class<T> clazz) {
        this.clazz = clazz;
        this.emf = JPAUtil.getEntityManagerFactory();
    }

    // Persistir la entidad
    public T guardar(T entity){
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            T resultado;
            if (entity.getId() == null) {
                em.persist(entity);
                resultado = entity;
            } else {
                resultado = em.merge(entity);
            }
            em.getTransaction().commit();
            return resultado;
        } catch (Exception err) {
            if(em.getTransaction().isActive()){
                em.getTransaction().rollback();
            }
            throw err;
        } finally {em.close();}

    }
    // Buscar por id
    public Optional<T> buscarPorId(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            T entity = em.find(clazz, id);
            return Optional.ofNullable(entity);
        } finally {
            em.close();
        }
    }
    // Retornar la lista de entidades activas
    public List<T> listarActivos() {
        EntityManager em = emf.createEntityManager();
        try {
            String jpql = "SELECT e FROM " + clazz.getSimpleName() + " e WHERE e.eliminado = false";
            return em.createQuery(jpql, clazz).getResultList();
        } finally {
            em.close();
        }
    }

    // Dar baja logica a una entidad y persistir
    public boolean eliminarLogico(Long id){
        EntityManager em = emf.createEntityManager();
        try{
            em.getTransaction().begin();
            T entity = em.find(clazz, id);
            if (entity == null){
                em.getTransaction().rollback();
                return false;
            }

            // Seteamos el campo eliminado
            clazz.getMethod("setEliminado", boolean.class).invoke(entity, true);
            em.merge(entity);
            em.getTransaction().commit();
            return true;
        } catch (Exception err) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error al eliminar logicamente: " + err.getMessage(), err);
        } finally {em.close();}

    }

}

package com.tpi.jpa.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import com.tpi.jpa.entities.Usuario;

import java.util.Optional;

public class UsuarioRepository extends Baserepository<Usuario> {

    public UsuarioRepository() {
        super(Usuario.class);
    }

    // Busca un usuario activo por su dirección de correo electrónico.
    // Retorna Optional para manejar el caso en que el mail no esté registrado.
    public Optional<Usuario> buscarPorMail(String mail) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Usuario> query = em.createQuery(
                    "SELECT u FROM Usuario u WHERE u.mail = :mail AND u.eliminado = false",
                    Usuario.class);
            query.setParameter("mail", mail);
            Usuario usuario = query.getResultList().getFirst();
            return Optional.of(usuario);
        } catch (NoResultException | java.util.NoSuchElementException err) {
            return Optional.empty();
        } finally {
            em.close();
        }
    }
}
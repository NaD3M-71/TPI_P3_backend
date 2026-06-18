package programacion3.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import programacion3.entities.Pedido;
import programacion3.enums.Estado;

import java.util.List;

public class PedidoRepository extends Baserepository<Pedido> {

    public PedidoRepository() {
        super(Pedido.class);
    }

    // Retorna todos los pedidos activos de un usuario dado su ID.
    // Filtra por usuario.id (FK en Pedido) y por eliminado = false para excluir bajas lógicas.
    public List<Pedido> buscarPorUsuario(Long idUsuario) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Pedido> query = em.createQuery(
                    "SELECT p FROM Pedido p WHERE p.usuario.id = :idUsuario AND p.eliminado = false",
                    Pedido.class);
            query.setParameter("idUsuario", idUsuario);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    // Retorna todos los pedidos activos con un estado específico.
    // Útil para filtrar PENDIENTE, CONFIRMADO, TERMINADO o CANCELADO.
    public List<Pedido> buscarPorEstado(Estado estado) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Pedido> query = em.createQuery(
                    "SELECT p FROM Pedido p WHERE p.estado = :estado AND p.eliminado = false",
                    Pedido.class);
            query.setParameter("estado", estado);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}
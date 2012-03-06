/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package model.entities.base.facades;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import model.entities.AbstractFacade;
import model.entities.base.Encuesta;
import model.entities.base.OpcionEncuesta;
import model.entities.base.Usuario;

/**
 *
 * @author rulyone
 */
@Stateless
public class OpcionEncuestaFacade extends AbstractFacade<OpcionEncuesta> {
    @PersistenceContext(unitName = "DotaCLPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public OpcionEncuestaFacade() {
        super(OpcionEncuesta.class);
    }

    public List<OpcionEncuesta> findVotosUsuarioPorEncuesta(Usuario votador, Encuesta encuesta) {
        TypedQuery<OpcionEncuesta> tq = em.createNamedQuery("OpcionEncuesta.findVotosUsuarioPorEncuesta", OpcionEncuesta.class);
        tq.setParameter("votador", votador);
        tq.setParameter("encuesta", encuesta);
        return tq.getResultList();
    }

    public int countVotosByIdOpcion(Long idOpcion) {
        Query q = em.createNamedQuery("OpcionEncuesta.countVotosByIdOpcionEncuesta");
        q.setParameter("id", idOpcion);
        return ((Long) q.getSingleResult()).intValue();
    }

    public int countVotosUnicosByIdEncuesta(Long idEncuesta) {
        Query q = em.createNamedQuery("OpcionEncuesta.countVotosUnicosByIdEncuesta");
        q.setParameter("id", idEncuesta);
        return ((Long) q.getSingleResult()).intValue();
    }

}

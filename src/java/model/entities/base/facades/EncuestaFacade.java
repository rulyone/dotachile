/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package model.entities.base.facades;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import model.entities.AbstractFacade;
import model.entities.base.Encuesta;

/**
 *
 * @author rulyone
 */
@Stateless
public class EncuestaFacade extends AbstractFacade<Encuesta> {
    @PersistenceContext(unitName = "DotaCLPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public EncuestaFacade() {
        super(Encuesta.class);
    }

    public Encuesta findByPregunta(String pregunta) {
        TypedQuery<Encuesta> tq = em.createNamedQuery("Encuesta.findByPregunta", Encuesta.class);
        tq.setParameter("pregunta", pregunta);
        try {
            return tq.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    public Encuesta getUltimaEncuesta() {
        TypedQuery<Encuesta> tq = em.createNamedQuery("Encuesta.encuestasOrderByIdDesc", Encuesta.class);
        tq.setMaxResults(1);
        tq.setFirstResult(0);
        try{
            return tq.getSingleResult();
        }catch (NoResultException ex) {
            return null;
        }
    }

    

}

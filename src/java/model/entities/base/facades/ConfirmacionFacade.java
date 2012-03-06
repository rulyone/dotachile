/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.entities.base.facades;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import model.entities.AbstractFacade;
import model.entities.base.Confirmacion;

/**
 *
 * @author rulyone
 */
@Stateless
public class ConfirmacionFacade extends AbstractFacade<Confirmacion> {
    @PersistenceContext(unitName = "DotaCLPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ConfirmacionFacade() {
        super(Confirmacion.class);
    }
    
    public Confirmacion findByTag(String tag) {
        TypedQuery<Confirmacion> tq = em.createNamedQuery("Confirmacion.findByTag", Confirmacion.class);
        tq.setParameter("tag", tag);
        try{
            return tq.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }
    
}

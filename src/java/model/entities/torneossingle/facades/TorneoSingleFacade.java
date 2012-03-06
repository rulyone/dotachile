/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.entities.torneossingle.facades;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.entities.AbstractFacade;
import model.entities.torneossingle.TorneoSingle;

/**
 *
 * @author rulyone
 */
@Stateless
public class TorneoSingleFacade extends AbstractFacade<TorneoSingle> {
    @PersistenceContext(unitName = "DotaCLPU")
    private EntityManager em;

    protected EntityManager getEntityManager() {
        return em;
    }

    public TorneoSingleFacade() {
        super(TorneoSingle.class);
    }
    
}

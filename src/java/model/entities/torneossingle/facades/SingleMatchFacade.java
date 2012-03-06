/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.entities.torneossingle.facades;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.entities.AbstractFacade;
import model.entities.torneossingle.SingleMatch;

/**
 *
 * @author rulyone
 */
@Stateless
public class SingleMatchFacade extends AbstractFacade<SingleMatch> {
    @PersistenceContext(unitName = "DotaCLPU")
    private EntityManager em;

    protected EntityManager getEntityManager() {
        return em;
    }

    public SingleMatchFacade() {
        super(SingleMatch.class);
    }
    
}

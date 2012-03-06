/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package model.entities.base.facades;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.entities.AbstractFacade;
import model.entities.base.Replay;

/**
 *
 * @author Pablo
 */
@Stateless
public class ReplayFacade extends AbstractFacade<Replay> {
    @PersistenceContext(unitName = "DotaCLPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ReplayFacade() {
        super(Replay.class);
    }

}

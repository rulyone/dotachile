/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package model.entities.base.facades;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.entities.AbstractFacade;
import model.entities.base.BanHistory;

/**
 *
 * @author Pablo
 */
@Stateless
public class BanHistoryFacade extends AbstractFacade<BanHistory> {
    @PersistenceContext(unitName = "DotaCLPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public BanHistoryFacade() {
        super(BanHistory.class);
    }

}

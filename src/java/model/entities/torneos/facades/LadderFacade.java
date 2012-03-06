/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package model.entities.torneos.facades;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.entities.AbstractFacade;
import model.entities.torneos.Ladder;

/**
 *
 * @author Pablo
 */
@Stateless
public class LadderFacade extends AbstractFacade<Ladder> {
    @PersistenceContext(unitName = "DotaCLPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public LadderFacade() {
        super(Ladder.class);
    }

}

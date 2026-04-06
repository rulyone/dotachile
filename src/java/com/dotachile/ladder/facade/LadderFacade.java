/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dotachile.ladder.facade;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.entities.AbstractFacade;
import com.dotachile.ladder.entity.Ladder;

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

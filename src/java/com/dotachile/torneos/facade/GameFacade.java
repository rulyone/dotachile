/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dotachile.torneos.facade;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import model.entities.AbstractFacade;
import com.dotachile.torneos.entity.Game;

/**
 *
 * @author Pablo
 */
@Stateless
public class GameFacade extends AbstractFacade<Game> {
    @PersistenceContext(unitName = "DotaCLPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public GameFacade() {
        super(Game.class);
    }

    public int countByClanTag(String tag) {
        Query q = em.createNamedQuery("Game.countByClanTag");
        q.setParameter("tag", tag);
        return ((Long) q.getSingleResult()).intValue();
    }
    
    public List<Game> compararClanes(String tag1, String tag2) {
        return em.createNamedQuery("Game.compararClanes", Game.class)
                .setParameter("tag1", tag1)
                .setParameter("tag2", tag2)
                .getResultList();
    }
}

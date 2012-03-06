/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package model.entities.torneos.facades;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import model.entities.AbstractFacade;
import model.entities.torneos.Game;
import model.entities.torneos.GameMatch;

/**
 *
 * @author Pablo
 */
@Stateless
public class GameMatchFacade extends AbstractFacade<GameMatch> {
    @PersistenceContext(unitName = "DotaCLPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public GameMatchFacade() {
        super(GameMatch.class);
    }

    //GameMatch.findByGame
    public GameMatch findByGame(Game game) {
        if(!em.contains(game))
            return null;
        List<GameMatch> matches = em.createNamedQuery("GameMatch.findByGame", GameMatch.class)
                .setParameter("game", game)
                .setMaxResults(1)
                .getResultList();
        if(matches == null || matches.isEmpty())
            return null;
        return matches.get(0);
    }

    public List<Game> findGamesByTag(String tag) {
        TypedQuery<Game> tq = em.createNamedQuery("GameMatch.findGamesByTag", Game.class);
        tq.setParameter("tag", tag);
        return tq.getResultList();
    }

    public List<Game> findGamesByTagLimit(String tag, int first, int size) {
        TypedQuery<Game> q = em.createNamedQuery("GameMatch.findGamesByTag", Game.class);
        q.setParameter("tag", tag);
        q.setMaxResults(size);
        q.setFirstResult(first);
        return q.getResultList();
    }

    public int countByTag(String tag) {
        Query q = em.createNamedQuery("GameMatch.countByTag");
        q.setParameter("tag", tag);
        return ((Long) q.getSingleResult()).intValue();
    }

    public List<GameMatch> findMatchesConfirmadosByTag(String tag) {
        TypedQuery<GameMatch> q = em.createNamedQuery("GameMatch.findMatchesConfirmadosByTag", GameMatch.class);
        q.setParameter("tag", tag);
        return q.getResultList();
    }
    
    public int countMatchesConfirmadosByTag(String tag) {
        Query q = em.createNamedQuery("GameMatch.findMatchesConfirmadosByTag");
        q.setParameter("tag", tag);
        return ((Long) q.getSingleResult()).intValue();
    }
    
    public List<GameMatch> findMatchesPendientesByTag(String tag) {
        TypedQuery<GameMatch> q = em.createNamedQuery("GameMatch.findPendientesByTag", GameMatch.class);
        q.setParameter("tag", tag);
        return q.getResultList();
    }
    
    public List<GameMatch> listMatchesPendientes() {
        return em.createNamedQuery("GameMatch.listMatchesPendientes", GameMatch.class).getResultList();
    }
}

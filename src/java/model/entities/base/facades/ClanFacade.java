/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package model.entities.base.facades;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import model.entities.AbstractFacade;
import model.entities.base.Clan;
import model.entities.base.Usuario;

/**
 *
 * @author Pablo
 */
@Stateless
public class ClanFacade extends AbstractFacade<Clan> {
    @PersistenceContext(unitName = "DotaCLPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ClanFacade() {
        super(Clan.class);
    }

    public Clan findByNombre(String nombre) {
        List<Clan> list = em.createNamedQuery("Clan.findByNombre", Clan.class)
                .setParameter("nombre", nombre)
                .getResultList();
        return list.size() == 1? list.get(0) : null;
    }

    public Clan findByTag(String tag) {
        List<Clan> list = em.createNamedQuery("Clan.findByTag", Clan.class)
                .setParameter("tag", tag)
                .getResultList();
        return list.size() == 1? list.get(0) : null;
    }

    public List<Clan> findLimitOrderByElo(int first, int size) {
        TypedQuery<Clan> q = em.createNamedQuery("Clan.findClanOrderByElo", Clan.class);
        q.setMaxResults(size);
        q.setFirstResult(first);
        return q.getResultList();
    }

    public List<Clan> rankClanesLimit(int first, int size) {
        TypedQuery<Clan> q = em.createNamedQuery("Clan.rankClanes", Clan.class);
        q.setMaxResults(size);
        q.setFirstResult(first);
        return q.getResultList();
    }
    
    public int rankClanesCount() {
        Query q = em.createNamedQuery("Clan.rankClanesCount");
        return ((Long) q.getSingleResult()).intValue();
    }

    public List<Clan> searchClanesByTag(String tag, int maxResults) {
        return em.createNamedQuery("Clan.searchClanesByTag", Clan.class)
                .setParameter("tag", "%" + tag + "%")
                .setMaxResults(maxResults)
                .getResultList();
    }
    
    public List<Clan> listClanesOrderByFechaCreacion() {
        return em.createNamedQuery("Clan.listClanesOrderByFechaCreacion", Clan.class).getResultList();
    }
    
    public List<Clan> searchClanesByChieftain(Usuario chieftain) {
        return em.createNamedQuery("Clan.searchClanesByChieftain", Clan.class)
                .setParameter("chieftain", chieftain)
                .getResultList();
    }

}

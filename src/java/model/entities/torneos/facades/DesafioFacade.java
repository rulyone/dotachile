/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package model.entities.torneos.facades;

import java.util.List;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import model.entities.AbstractFacade;
import model.entities.torneos.Desafio;
import model.entities.torneos.Game;

/**
 *
 * @author Pablo
 */
@Stateless
@DeclareRoles({"ADMIN_ROOT", "ADMIN_DOTA", "ADMIN_LADDER"})
public class DesafioFacade extends AbstractFacade<Desafio> {
    @PersistenceContext(unitName = "DotaCLPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DesafioFacade() {
        super(Desafio.class);
    }

    public List<Desafio> desafiosPendientes(String tag) {
        return em.createNamedQuery("Desafio.findDesafiosPendientesByTag", Desafio.class)
                .setParameter("tag", tag)
                .getResultList();
    }

    public List<Game> findGamesByTag(String tag) {
        TypedQuery<Game> tq = em.createNamedQuery("Desafio.findGamesByTag", Game.class);
        tq.setParameter("tag", tag);
        return tq.getResultList();
    }

    public List<Game> findGamesByTagLimit(String tag, int first, int size) {
        TypedQuery<Game> q = em.createNamedQuery("Desafio.findGamesByTag", Game.class);
        q.setParameter("tag", tag);
        q.setMaxResults(size);
        q.setFirstResult(first);
        return q.getResultList();
    }

    public int countByTag(String tag) {
        Query q = em.createNamedQuery("Desafio.countByTag");
        q.setParameter("tag", tag);
        return ((Long) q.getSingleResult()).intValue();
    }
    
    public List<Desafio> findDesafiosConfirmadosByTag(String tag) {
        TypedQuery<Desafio> q = em.createNamedQuery("Desafio.findDesafiosConfirmadosByTag", Desafio.class);
        q.setParameter("tag", tag);
        return q.getResultList();
    }
    
    public int countConfirmadosByTag(String tag) {
        Query q = em.createNamedQuery("Desafio.countConfirmadosByTag");
        q.setParameter("tag", tag);
        return ((Long) q.getSingleResult()).intValue();
    }

    @RolesAllowed({"ADMIN_ROOT", "ADMIN_DOTA", "ADMIN_LADDER"})
    public List<Desafio> findDesafiosPendientes() {
        TypedQuery<Desafio> tq = em.createNamedQuery("Desafio.findDesafiosPendientes", Desafio.class);
        return tq.getResultList();        
    }
    
    public int countDesafiosPendientes() {
        Query q = em.createNamedQuery("Desafio.countDesafiosPendientes");
        return ((Long) q.getSingleResult()).intValue();
    }
    
    public List<Desafio> findDesafiosNoAceptados() {
        TypedQuery<Desafio> tq = em.createNamedQuery("Desafio.findDesafiosNoAceptados", Desafio.class);
        return tq.getResultList();
    }

}

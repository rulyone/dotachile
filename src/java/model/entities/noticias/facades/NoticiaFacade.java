/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package model.entities.noticias.facades;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import model.entities.AbstractFacade;
import model.entities.noticias.Noticia;

/**
 *
 * @author Pablo
 */
@Stateless
public class NoticiaFacade extends AbstractFacade<Noticia> {
    @PersistenceContext(unitName = "DotaCLPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public NoticiaFacade() {
        super(Noticia.class);
    }

    public List<Noticia> findLimit(int first, int size, String categoryname) {
        TypedQuery<Noticia> q = em.createQuery("SELECT n FROM Noticia n WHERE n.categoria.categoryname = :categoryname ORDER BY n.fecha ASC", Noticia.class);
        q.setParameter("categoryname", categoryname);
        q.setMaxResults(size);
        q.setFirstResult(first);
        return q.getResultList();
    }

    @Override
    public List<Noticia> findAll() {
        return em.createNamedQuery("Noticia.findAll", Noticia.class).getResultList();
    }

    public int count(String categoryname) {
        Query q = em.createQuery("SELECT COUNT(n) FROM Noticia n WHERE n.categoria.categoryname = :categoryname");
        q.setParameter("categoryname", categoryname);
        return ((Long) q.getSingleResult()).intValue();
    }
    
    public int countNoAprobadas() {
        Query q = em.createQuery("SELECT COUNT(n) FROM Noticia n WHERE n.aprobada = FALSE");
        return ((Long) q.getSingleResult()).intValue();
    }

    public int countComentariosByIdNoticia(Long idNoticia) {
        Query q = em.createNamedQuery("Noticia.countComentariosByIdNoticia");
        q.setParameter("idNoticia", idNoticia);
        return ((Long) q.getSingleResult()).intValue();
    }

    public List<Noticia> findUltimasNoticias(int first, int maxResults) {
        TypedQuery<Noticia> tq = em.createNamedQuery("Noticia.findUltimasNoticias", Noticia.class);
        tq.setFirstResult(first);
        tq.setMaxResults(maxResults);
        return tq.getResultList();
    }

    public List<Noticia> findUltimasNoticiasByCategoryname(String categoryname, int maxResults) {
        TypedQuery<Noticia> tq = em.createNamedQuery("Noticia.findUltimasNoticiasByCategoryname", Noticia.class);
        tq.setParameter("categoryname", categoryname);
        tq.setMaxResults(maxResults);
        return tq.getResultList();
    }
    
    public List<Noticia> findUltimasNoticiasNoAprobadas(int first, int maxResults) {
        TypedQuery<Noticia> tq = em.createNamedQuery("Noticia.findUltimasNoticiasNoAprobadas", Noticia.class);
        tq.setFirstResult(first);
        tq.setMaxResults(maxResults);
        return tq.getResultList();
    }

}

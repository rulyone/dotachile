/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package model.entities.noticias.facades;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.entities.AbstractFacade;
import model.entities.noticias.Categoria;

/**
 *
 * @author Pablo
 */
@Stateless
public class CategoriaFacade extends AbstractFacade<Categoria> {
    @PersistenceContext(unitName = "DotaCLPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CategoriaFacade() {
        super(Categoria.class);
    }

    public Categoria findByCategoryname(String categoryname) {
        List<Categoria> list = em.createNamedQuery("Categoria.findByCategoryname", Categoria.class)
                .setParameter("categoryname", categoryname)
                .getResultList();
        return list.size() == 1? list.get(0) : null;
    }


}

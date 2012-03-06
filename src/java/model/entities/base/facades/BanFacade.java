/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package model.entities.base.facades;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.entities.AbstractFacade;
import model.entities.base.Ban;

/**
 *
 * @author Pablo
 */
@Stateless
public class BanFacade extends AbstractFacade<Ban> {
    @PersistenceContext(unitName = "DotaCLPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public BanFacade() {
        super(Ban.class);
    }

    public Ban findByBaneadoUsername(String username) {
        List<Ban> list = em.createNamedQuery("Ban.findByBaneadoUsername", Ban.class).setParameter("username", username).getResultList();
        return list.size() == 1 ? list.get(0) : null;
    }

    public List<Ban> findByBaneadorUsername(String username) {
        return em.createNamedQuery("Ban.findByBaneadorUsername", Ban.class).setParameter("username", username).getResultList();
    }
}

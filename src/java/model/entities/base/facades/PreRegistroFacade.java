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
import model.entities.base.PreRegistro;

/**
 *
 * @author Pablo
 */
@Stateless
public class PreRegistroFacade extends AbstractFacade<PreRegistro> {
    @PersistenceContext(unitName = "DotaCLPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PreRegistroFacade() {
        super(PreRegistro.class);
    }

    public PreRegistro findByUsername(String username) {
        List<PreRegistro> list = em.createNamedQuery("PreRegistro.findByUsername", PreRegistro.class)
                .setParameter("username", username)
                .getResultList();
        return list.size()==1? list.get(0):null;
    }

    public PreRegistro findByEmail(String email) {
        List<PreRegistro> list = em.createNamedQuery("PreRegistro.findByEmail", PreRegistro.class)
                .setParameter("email", email)
                .getResultList();
        return list.size()==1? list.get(0):null;
    }
}

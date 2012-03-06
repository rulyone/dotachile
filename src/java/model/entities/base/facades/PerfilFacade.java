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
import model.entities.base.Perfil;

/**
 *
 * @author Pablo
 */
@Stateless
public class PerfilFacade extends AbstractFacade<Perfil> {
    @PersistenceContext(unitName = "DotaCLPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PerfilFacade() {
        super(Perfil.class);
    }

    public Perfil findByUsername(String username) {
        List<Perfil> list = em.createNamedQuery("Perfil.findByUsername", Perfil.class)
                .setParameter("username", username)
                .getResultList();
        return list.size()==1? list.get(0):null;
    }
    
    public Perfil findByNickW3(String nickw3) {
        List<Perfil> list = em.createNamedQuery("Perfil.findByNickW3", Perfil.class)
                                          .setParameter("nickw3", nickw3)
                                          .getResultList();
        return list.size() == 1? list.get(0):null;
    }
    
    public Perfil findByBotW3(String botw3) {
        List<Perfil> list = em.createNamedQuery("Perfil.findByBotW3", Perfil.class)
                .setParameter("botw3", botw3)
                .getResultList();
        return list.size() == 1? list.get(0):null;
    }
    
}

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
import model.entities.base.ClanBan;

/**
 *
 * @author rulyone
 */
@Stateless
public class ClanBanFacade extends AbstractFacade<ClanBan> {
    @PersistenceContext(unitName = "DotaCLPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ClanBanFacade() {
        super(ClanBan.class);
    }
    
    public ClanBan findByTag(String tag) {
        List<ClanBan> list = em.createNamedQuery("ClanBan.findByTag", ClanBan.class)
                .setParameter("tag", tag)
                .getResultList();
        return list.size() == 1? list.get(0) : null;
    }
    
}

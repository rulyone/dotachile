/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package model.entities.torneos.facades;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.entities.AbstractFacade;
import model.entities.base.Clan;
import model.entities.torneos.Modificacion;
import model.entities.torneos.TemporadaModificacion;

/**
 *
 * @author Pablo
 */
@Stateless
public class ModificacionFacade extends AbstractFacade<Modificacion> {
    @PersistenceContext(unitName = "DotaCLPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ModificacionFacade() {
        super(Modificacion.class);
    }

    public List<Modificacion> findByTemporadaAndClan(TemporadaModificacion temporada, Clan clan) {
        if(!em.contains(temporada) || !em.contains(clan))
            return null;

        return em.createNamedQuery("Modificacion.findByTemporadaAndClan", Modificacion.class)
                .setParameter("temporada", temporada)
                .setParameter("clan", clan)
                .getResultList();
    }

}

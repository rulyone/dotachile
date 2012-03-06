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
import model.entities.AbstractFacade;
import model.entities.base.Clan;
import model.entities.torneos.Torneo;

/**
 *
 * @author Pablo
 */

@Stateless
public class TorneoFacade extends AbstractFacade<Torneo> {
    @PersistenceContext(unitName = "DotaCLPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TorneoFacade() {
        super(Torneo.class);
    }

    
    public Torneo findByNombre(String nombre) {
        List<Torneo> list = em.createNamedQuery("Torneo.findByNombre", Torneo.class)
                .setParameter("nombre", nombre)
                .getResultList();
        if(list != null && list.size() == 1)
            return list.get(0);
        return null;
    }

    public int cantidadClanesInscritos(Long idTorneo) {
        return ((Long)em.createNamedQuery("Torneo.cantidadClanesInscritos")
                .setParameter("id", idTorneo)
                .getSingleResult()).intValue();
    }

    public List<Torneo> findAllReverse() {
        return em.createNamedQuery("Torneo.findAllReverse", Torneo.class).getResultList();
    }

    public List<Clan> top10TorneosGanados() {
        //TODO: Ver Torneo.java @NamedQuery...
        return null;
    }

}

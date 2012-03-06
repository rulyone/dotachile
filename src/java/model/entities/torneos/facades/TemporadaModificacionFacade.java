/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package model.entities.torneos.facades;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.entities.AbstractFacade;
import model.entities.torneos.TemporadaModificacion;

/**
 *
 * @author Pablo
 */
@Stateless
public class TemporadaModificacionFacade extends AbstractFacade<TemporadaModificacion> {
    @PersistenceContext(unitName = "DotaCLPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TemporadaModificacionFacade() {
        super(TemporadaModificacion.class);
    }

    public TemporadaModificacion findByDate(Date fecha) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(fecha);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date date = cal.getTime();
        List<TemporadaModificacion> temp = em.createNamedQuery("TemporadaModificacion.findByDate", TemporadaModificacion.class)
                                            .setParameter("date", date)
                                            .setMaxResults(1)
                                            .getResultList();
        if(temp != null && temp.size() == 1)
            return temp.get(0);
        return null;
    }

    public List<TemporadaModificacion> findAllReverse() {
        return em.createNamedQuery("TemporadaMOdificacion.findAllReverse", TemporadaModificacion.class).getResultList();
    }

}

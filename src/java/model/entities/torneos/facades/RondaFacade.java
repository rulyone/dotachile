/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package model.entities.torneos.facades;

import java.io.Serializable;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.entities.AbstractFacade;
import model.entities.torneos.Ronda;

/**
 *
 * @author Pablo
 */
@Stateless
public class RondaFacade extends AbstractFacade<Ronda> implements Serializable {
    final static long serialVersionUID = 1L;

    @PersistenceContext(unitName = "DotaCLPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public RondaFacade() {
        super(Ronda.class);
    }

}

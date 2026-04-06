/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dotachile.torneos.facade;

import java.io.Serializable;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.entities.AbstractFacade;
import com.dotachile.torneos.entity.Ronda;

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

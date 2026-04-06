/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dotachile.torneossingle.facade;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.entities.AbstractFacade;
import com.dotachile.torneossingle.entity.TorneoSingle;

/**
 *
 * @author rulyone
 */
@Stateless
public class TorneoSingleFacade extends AbstractFacade<TorneoSingle> {
    @PersistenceContext(unitName = "DotaCLPU")
    private EntityManager em;

    protected EntityManager getEntityManager() {
        return em;
    }

    public TorneoSingleFacade() {
        super(TorneoSingle.class);
    }
    
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dotachile.auth.facade;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.entities.AbstractFacade;
import com.dotachile.auth.entity.Grupo;

/**
 *
 * @author Pablo
 */
@Stateless
public class GrupoFacade extends AbstractFacade<Grupo> {
    @PersistenceContext(unitName = "DotaCLPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public GrupoFacade() {
        super(Grupo.class);
    }

}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dotachile.comentarios;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.entities.AbstractFacade;

/**
 *
 * @author Pablo
 */
@Stateless
public class ComentarioFacade extends AbstractFacade<Comentario> {
    @PersistenceContext(unitName = "DotaCLPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ComentarioFacade() {
        super(Comentario.class);
    }

}

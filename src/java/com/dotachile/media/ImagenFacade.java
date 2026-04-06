/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dotachile.media;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.entities.AbstractFacade;
import com.dotachile.media.Imagen;

/**
 *
 * @author Pablo
 */
@Stateless
public class ImagenFacade extends AbstractFacade<Imagen> {
    @PersistenceContext(unitName = "DotaCLPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ImagenFacade() {
        super(Imagen.class);
    }

}

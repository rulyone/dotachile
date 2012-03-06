/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package model.services;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.ejb.SessionContext;
import model.entities.base.facades.*;
import model.entities.torneos.facades.ModificacionFacade;
import model.entities.torneos.facades.TemporadaModificacionFacade;

/**
 *
 * @author rulyone
 */
@Stateless
@LocalBean
public class FunService {

    @Resource
    SessionContext ctx;

    @EJB private ClanFacade clanFac;
    @EJB private UsuarioFacade userFac;
    @EJB private TemporadaModificacionFacade tempFac;
    @EJB private ModificacionFacade modFac;
    @EJB private ConfirmacionFacade confirmacionFac;
    @EJB private ClanBanFacade clanBanFac;
    @EJB private MovimientoFacade movFac;
    
    
}

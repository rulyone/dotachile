/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dotachile.shared;

import java.io.Serializable;
import javax.ejb.EJB;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import com.dotachile.auth.entity.Usuario;
import model.entities.torneos.facades.GameFacade;
import com.dotachile.shared.BusinessLogicException;
import com.dotachile.comentarios.ComentariosService;
import com.dotachile.shared.Util;
import com.dotachile.infrastructure.web.listeners.SessionManagerBean;

/**
 *
 * @author Pablo
 */
@ManagedBean(name="applicationMB")
@ApplicationScoped
public class ApplicationMB implements Serializable {

    @EJB private ComentariosService comentariosService;
    @EJB private GameFacade gameFac;
    @EJB private SessionManagerBean sessionBean;

    /** Creates a new instance of ApplicationMB */
    public ApplicationMB() {
    }

    public int getLogedUsersOnline() {
        return Usuario.logins.size();
    }

    public String denegarComentario(Long idComentario) {
        try {
            comentariosService.denegarComentario(idComentario);
            Util.addInfoMessage("Comentario denegado.", null);
        } catch (BusinessLogicException ex) {
            Util.addErrorMessage("Error al denegar comentario.", ex.getMessage());
        }
        return null;
    }

    //REMOVIDO PORQUE USA MUCHO RECURSO.
//    public int gamesByTag(String tag) {
//        return gameFac.countByClanTag(tag);
//    }

    public int getOnlineUsers() {
        return sessionBean.getActiveSessionsCount();
    }

}

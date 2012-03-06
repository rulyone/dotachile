/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package controller;

import java.io.Serializable;
import javax.ejb.EJB;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import model.entities.base.Usuario;
import model.entities.torneos.facades.GameFacade;
import model.exceptions.BusinessLogicException;
import model.services.ComentariosService;
import utils.Util;
import webapp.listeners.SessionManagerBean;

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

    public int gamesByTag(String tag) {
        return gameFac.countByClanTag(tag);
    }

    public int getOnlineUsers() {
        return sessionBean.getActiveSessionsCount();
    }

}

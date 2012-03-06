/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package controller.clanes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.event.ActionEvent;

import model.entities.base.facades.UsuarioFacade;
import model.exceptions.BusinessLogicException;
import model.services.ClanService;
import utils.Util;

/**
 *
 * @author Pablo
 */
@ManagedBean(name="invitarPlayerMB")
@RequestScoped
public class InvitarPlayerMB implements Serializable {
    
    @EJB private ClanService clanService;
    @EJB private UsuarioFacade userFac;

    private String playerUsername;

    /** Creates a new instance of InvitarPlayerMB */
    public InvitarPlayerMB() {
    }

    public String getPlayerUsername() {
        return playerUsername;
    }

    public void setPlayerUsername(String playerUsername) {
        this.playerUsername = playerUsername;
    }

    public void invitarPlayer(ActionEvent e) {
        try {
            clanService.invitarPlayer(playerUsername);
            Util.addInfoMessage("Player " + playerUsername + " invitado satisfactoriamente.", "Debes esperar que " + playerUsername + " acepte la invitacion.");
        } catch (BusinessLogicException ex) {
            Util.addErrorMessage("Error al invitar player", ex.getMessage());
        }
    }

    public List<String> completeUsernames(String query) {
        List<String> results = new ArrayList<String>(10);
        results = userFac.searchUsernames(query, 10);
        return results;
    }

}

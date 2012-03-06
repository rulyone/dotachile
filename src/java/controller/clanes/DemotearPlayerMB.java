/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package controller.clanes;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.event.ActionEvent;
import model.exceptions.BusinessLogicException;
import model.services.ClanService;
import utils.Util;

/**
 *
 * @author Pablo
 */
@ManagedBean(name="demotearPlayerMB")
@RequestScoped
public class DemotearPlayerMB implements Serializable {

    @EJB private ClanService clanService;

    private String usernameToDemote;

    /** Creates a new instance of DemotearPlayerMB */
    public DemotearPlayerMB() {
    }

    public String getUsernameToDemote() {
        return usernameToDemote;
    }

    public void setUsernameToDemote(String usernameToDemote) {
        this.usernameToDemote = usernameToDemote;
    }

    public void demotearPlayer(ActionEvent e) {
        try {
            clanService.demotear(usernameToDemote);
            Util.addInfoMessage("Player " + usernameToDemote + " demoteado satisfactoriamente.", null);
        } catch (BusinessLogicException ex) {
            Util.addErrorMessage("Error al demotear player.", ex.getMessage());
        }
    }
}

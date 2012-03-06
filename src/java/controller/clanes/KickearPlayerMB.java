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
@ManagedBean(name="kickearPlayerMB")
@RequestScoped
public class KickearPlayerMB implements Serializable {

    @EJB private ClanService clanService;

    private String usernameToKick;

    /** Creates a new instance of KickearPlayerMB */
    public KickearPlayerMB() {
    }

    public String getUsernameToKick() {
        return usernameToKick;
    }

    public void setUsernameToKick(String usernameToKick) {
        this.usernameToKick = usernameToKick;
    }

    public void kickearPlayer(ActionEvent e) {
        try {
            clanService.kickearPlayer(usernameToKick);
            Util.addInfoMessage("Player " + usernameToKick + " sacado del clan satisfactoriamente.", null);
        } catch (BusinessLogicException ex) {
            Util.addErrorMessage("Error al kickear player.", ex.getMessage());
        }
    }
}

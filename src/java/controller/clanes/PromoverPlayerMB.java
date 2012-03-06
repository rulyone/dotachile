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
import model.exceptions.BusinessLogicException;
import model.services.ClanService;
import utils.Util;

/**
 *
 * @author Pablo
 */
@ManagedBean(name="promoverPlayerMB")
@RequestScoped
public class PromoverPlayerMB implements Serializable {

    @EJB private ClanService clanService;

    private String usernameToPromote;

    /** Creates a new instance of PromoverPlayerMB */
    public PromoverPlayerMB() {
    }

    public String getUsernameToPromote() {
        return usernameToPromote;
    }

    public void setUsernameToPromote(String usernameToPromote) {
        this.usernameToPromote = usernameToPromote;
    }

    public void promoverPlayer() {
        try {
            clanService.promover(usernameToPromote);
            Util.addInfoMessage("Usuario " + usernameToPromote + " promovido satisfactoriamente.", null);
        } catch (BusinessLogicException ex) {
            Util.addErrorMessage("Error al promover player.", ex.getMessage());
        }
    }
}

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
@ManagedBean(name="traspasarChieftainMB")
@RequestScoped
public class TraspasarChieftainMB implements Serializable {

    @EJB private ClanService clanService;

    private String usernameNuevoChieftain;

    /** Creates a new instance of TraspasarChieftainMB */
    public TraspasarChieftainMB() {
    }

    public String getUsernameNuevoChieftain() {
        return usernameNuevoChieftain;
    }

    public void setUsernameNuevoChieftain(String usernameNuevoChieftain) {
        this.usernameNuevoChieftain = usernameNuevoChieftain;
    }

    public void traspasarChieftain(ActionEvent e) {
        try {
            clanService.traspasarChieftain(usernameNuevoChieftain);
            Util.addInfoMessage("Usuario " + this.usernameNuevoChieftain + " es el nuevo chieftain del clan.", null);
        } catch (BusinessLogicException ex) {
            Util.addErrorMessage("Error al traspasar Chieftain.", ex.getMessage());
        }
    }
}

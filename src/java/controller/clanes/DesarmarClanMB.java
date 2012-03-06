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
@ManagedBean(name="desarmarClanMB")
@RequestScoped
public class DesarmarClanMB implements Serializable {

    @EJB
    private ClanService clanService;

    /** Creates a new instance of DesarmarClanMB */
    public DesarmarClanMB() {
    }

    public String desarmarClan() {
        String ret = null;
        try {
            clanService.desarmarClan();
            Util.addInfoMessage("Clan desarmado satisfactoriamente.", "Recuerda que solo tu puedes revivir el clan en un futuro, las estadisticas se mantendran.");
            ret = "/index.xhtml";
        } catch (BusinessLogicException ex) {
            Util.addErrorMessage("Error al desarmar clan", ex.getMessage());
        }
        return ret;
    }

}

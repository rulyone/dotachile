/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package controller.torneos;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import model.exceptions.BusinessLogicException;
import model.services.TorneoService;
import utils.Util;

/**
 *
 * @author Pablo
 */
@ManagedBean(name="modificarBestOfMB")
@RequestScoped
public class ModificarBestOfMB implements Serializable {

    @EJB private TorneoService torneoService;

    private Long idMatch;
    private int bestOf;

    /** Creates a new instance of ModificarBestOfMB */
    public ModificarBestOfMB() {
    }

    public int getBestOf() {
        return bestOf;
    }

    public void setBestOf(int bestOf) {
        this.bestOf = bestOf;
    }

    public Long getIdMatch() {
        return idMatch;
    }

    public void setIdMatch(Long idMatch) {
        this.idMatch = idMatch;
    }

    public String modificarBestOf() {
        try {
            torneoService.modificarBestOfMatch(idMatch, bestOf);
            Util.addInfoMessage("Modificacion del Best Of satisfactoria.", null);
        } catch (BusinessLogicException ex) {
            Util.addErrorMessage("Error al modificar el Best Of.", ex.getMessage());
        }
        //TODO:
        return null;
    }
}

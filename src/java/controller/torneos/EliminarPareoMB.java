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
@ManagedBean(name="eliminarPareoMB")
@RequestScoped
public class EliminarPareoMB implements Serializable {

    @EJB private TorneoService torneoService;

    private Long idMatch;

    /** Creates a new instance of EliminarPareoMB */
    public EliminarPareoMB() {
    }

    public Long getIdMatch() {
        return idMatch;
    }

    public void setIdMatch(Long idMatch) {
        this.idMatch = idMatch;
    }

    public String eliminarPareo() {
        try {
            torneoService.eliminarPareo(idMatch);
            Util.addInfoMessage("Pareo eliminado satisfactoriamente.", null);
            return "/index.xhtml";
        } catch (BusinessLogicException ex) {
            Util.addErrorMessage("Error al eliminar el pareo.", ex.getMessage());
        }
        //TODO:
        return null;
    }

}

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
@ManagedBean(name="inscribirClanTorneoForcedMB")
@RequestScoped
public class InscribirClanTorneoForcedMB implements Serializable {

    @EJB private TorneoService torneoService;

    private Long idTorneo;
    private String tagClan;

    /** Creates a new instance of InscribirClanTorneoForcedMB */
    public InscribirClanTorneoForcedMB() {
    }

    public String getTagClan() {
        return tagClan;
    }

    public void setTagClan(String tagClan) {
        this.tagClan = tagClan;
    }
    
    public Long getIdTorneo() {
        return idTorneo;
    }

    public void setIdTorneo(Long idTorneo) {
        this.idTorneo = idTorneo;
    }

    public String inscribirClanTorneoForced() {
        try {
            torneoService.inscribirClanTorneoForced(idTorneo, tagClan);
            Util.addInfoMessage("Inscripcion Forzada satisfactoria.", null);
        } catch (BusinessLogicException ex) {
            Util.addErrorMessage("Error al forzar la inscripcion del clan.", ex.getMessage());
        }
        return null;
    }

}

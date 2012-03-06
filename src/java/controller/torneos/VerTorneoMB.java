/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package controller.torneos;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ViewScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ComponentSystemEvent;
import model.entities.torneos.Torneo;
import model.entities.torneos.facades.TorneoFacade;
import model.exceptions.BusinessLogicException;
import model.services.ComentariosService;
import model.services.TorneoService;
import utils.Util;


/**
 *
 * @author Pablo
 */
@ManagedBean(name="verTorneoMB")
@ViewScoped
public class VerTorneoMB implements Serializable {
    
    @EJB private TorneoFacade torneoFac;
    @EJB private TorneoService torneoService;
    @EJB private ComentariosService comentariosService;

    private Long idTorneo;
    private Torneo torneo;

    private String comentarioTorneo;

    /** Creates a new instance of VerTorneoMB */
    public VerTorneoMB() {
        torneo = new Torneo();
    }

    public String getComentarioTorneo() {
        return comentarioTorneo;
    }

    public void setComentarioTorneo(String comentarioTorneo) {
        this.comentarioTorneo = comentarioTorneo;
    }

    public Long getIdTorneo() {
        return idTorneo;
    }

    public void setIdTorneo(Long idTorneo) {
        this.idTorneo = idTorneo;
    }

    public Torneo getTorneo() {
        return torneo;
    }

    public void setTorneo(Torneo torneo) {
        this.torneo = torneo;
    }

    public void loadTorneo(ComponentSystemEvent e) {
        if(this.idTorneo == null) {
            FacesContext ctx = FacesContext.getCurrentInstance();
            ctx.getApplication().getNavigationHandler().handleNavigation(ctx, null, "/index.xhtml?faces-redirect=true");
            return ;
        }
        Torneo t = torneoFac.find(idTorneo);
        if(t == null) {
            FacesContext ctx = FacesContext.getCurrentInstance();
            ctx.getApplication().getNavigationHandler().handleNavigation(ctx, null, "/index.xhtml?faces-redirect=true");
            return ;
        }
        this.torneo = t;
    }

    public void inscribirClan(ActionEvent e) {
        try {
            torneoService.inscribirClanTorneo(idTorneo);
            Util.addInfoMessage("Clan inscrito satisfactoriamente.", null);
        } catch (BusinessLogicException ex) {
            Util.addErrorMessage("Error al inscribir clan.", ex.getMessage());
        }
    }

    public void cancelarInscripcionClanTorneo(ActionEvent e) {
        try {
            torneoService.cancelarInscripcionClanTorneo(idTorneo);
            Util.addInfoMessage("Inscripcion cancelada satisfactoriamente.", null);
        } catch (BusinessLogicException ex) {
            Util.addErrorMessage("Error al cancelar inscripcion.", ex.getMessage());
        }
    }

    public String avanzarRonda() {
        try {
            torneoService.avanzarRonda(idTorneo);
            Util.addInfoMessage("Ronda avanzada satisfactoriamente", null);
        } catch (BusinessLogicException ex) {
            Util.addErrorMessage("Error al avanzar ronda.", ex.getMessage());
        }
        return null;
    }

    public String startTorneo() {
        try {
            torneoService.startTorneo(idTorneo);
            Util.addInfoMessage("Torneo comenzado satisfactoriamente.", null);
        } catch (BusinessLogicException ex) {
            Util.addErrorMessage("Error al comenzar torneo.", ex.getMessage());
        } 
        return null;
    }

    public void eliminarTorneo(ActionEvent e) {
        try {
            torneoService.eliminarTorneo(idTorneo);
            Util.addInfoMessage("Torneo eliminado satisfactoriamente.", null);
        } catch (BusinessLogicException ex) {
            Util.addErrorMessage("Error al eliminar torneo.", ex.getMessage());
        }
    }

    public void agregarComentarioTorneo(ActionEvent e) {
        try {
            comentariosService.agregarComentarioTorneo(idTorneo, comentarioTorneo);
            Util.addInfoMessage("Comentario agregado satisfactoriamente.", null);
            this.comentarioTorneo = "";
        } catch (BusinessLogicException ex) {
            Util.addErrorMessage("Error al agregar comentario.", ex.getMessage());
        }
    }
    
}

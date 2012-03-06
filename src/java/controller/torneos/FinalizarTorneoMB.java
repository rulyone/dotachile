/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package controller.torneos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.model.SelectItem;
import model.entities.base.Clan;
import model.entities.torneos.Torneo;
import model.entities.torneos.facades.TorneoFacade;
import model.exceptions.BusinessLogicException;
import model.services.TorneoService;
import utils.Util;

/**
 *
 * @author Pablo
 */
@ManagedBean(name="finalizarTorneoMB")
@RequestScoped
public class FinalizarTorneoMB implements Serializable {

    @EJB private TorneoService torneoService;
    @EJB private TorneoFacade torneoFac;

    private Long idTorneo;
    private String tagCampeon;

    private Torneo torneo;


    /** Creates a new instance of FinalizarTorneoMB */
    public FinalizarTorneoMB() {
        this.torneo = new Torneo();
    }

    public Torneo getTorneo() {
        return torneo;
    }

    public void setTorneo(Torneo torneo) {
        this.torneo = torneo;
    }

    public Long getIdTorneo() {
        return idTorneo;
    }

    public void setIdTorneo(Long idTorneo) {
        this.idTorneo = idTorneo;
    }

    public String getTagCampeon() {
        return tagCampeon;
    }

    public void setTagCampeon(String tagCampeon) {
        this.tagCampeon = tagCampeon;
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

    public String finalizarTorneo() {
        try {
            torneoService.finalizarTorneo(idTorneo, tagCampeon);
            Util.addInfoMessage("Torneo finalizado satisfactoriamente.", null);
        } catch (BusinessLogicException ex) {
            Util.addErrorMessage("Error al finalizar torneo.", ex.getMessage());
            return null;
        }
        FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
        return "/web/torneos/VerTorneo.xhtml?faces-redirect=true&amp;idTorneo=" + idTorneo;
    }

}

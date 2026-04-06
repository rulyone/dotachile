/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dotachile.torneos.controller;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import com.dotachile.torneos.entity.Torneo;
import com.dotachile.torneos.facade.TorneoFacade;
import com.dotachile.shared.BusinessLogicException;
import com.dotachile.torneos.service.TorneoService;
import com.dotachile.shared.Util;

/**
 *
 * @author rulyone
 */
@ManagedBean
@ViewScoped
public class EditarTorneoMB {
    
    @EJB private TorneoFacade torneosFac;
    @EJB private TorneoService torneoService;

    private Long idTorneo;

    private Torneo torneo;

    private String informacion;
    
    /** Creates a new instance of EditarTorneoMB */
    public EditarTorneoMB() {
    }

    public Long getIdTorneo() {
        return idTorneo;
    }

    public void setIdTorneo(Long idTorneo) {
        this.idTorneo = idTorneo;
    }

    public String getInformacion() {
        return informacion;
    }

    public void setInformacion(String informacion) {
        this.informacion = informacion;
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
        Torneo t = torneosFac.find(idTorneo);
        if(t == null) {
            FacesContext ctx = FacesContext.getCurrentInstance();
            ctx.getApplication().getNavigationHandler().handleNavigation(ctx, null, "/index.xhtml?faces-redirect=true");
            return ;
        }
        this.torneo = t;
        this.informacion = t.getInformacion();
    }

    public String editarTorneo() {
        String ret = null;
        try {
            torneoService.editarTorneo(idTorneo, informacion);
            Util.addInfoMessage("Torneo editado satisfactoriamente", null);
            Util.keepMessages();
            ret = "/web/torneos/VerTorneo.xhtml?faces-redirect=true&amp;id=" + idTorneo;
        } catch (BusinessLogicException ex) {
            Util.addErrorMessage("Error al editar el torneo.", ex.getMessage());
        }
        return ret;
    }
    
}

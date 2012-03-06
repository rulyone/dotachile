/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package controller.torneos.ladder;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ComponentSystemEvent;
import model.entities.torneos.Desafio;
import model.entities.torneos.Resultado;
import model.entities.torneos.facades.DesafioFacade;
import model.exceptions.BusinessLogicException;
import model.services.LadderService;
import utils.Util;

/**
 *
 * @author Pablo
 */
@ManagedBean(name="verDesafioMB")
@ViewScoped
public class VerDesafioMB implements Serializable {

    @EJB private LadderService ladderService;
    @EJB private DesafioFacade desafioFac;

    private Long idDesafio;
    private Desafio desafio;

    /** Creates a new instance of VerDesafioMB */
    public VerDesafioMB() {
    }

    public Desafio getDesafio() {
        return desafio;
    }

    public void setDesafio(Desafio desafio) {
        this.desafio = desafio;
    }

    public Long getIdDesafio() {
        return idDesafio;
    }

    public void setIdDesafio(Long idDesafio) {
        this.idDesafio = idDesafio;
    }

    public void loadDesafio(ComponentSystemEvent e) {
        if(this.idDesafio == null) {
            FacesContext ctx = FacesContext.getCurrentInstance();
            ctx.getApplication().getNavigationHandler().handleNavigation(ctx, null, "/index.xhtml?faces-redirect=true");
            return ;
        }
        Desafio d = desafioFac.find(idDesafio);
        if(d == null) {
            FacesContext ctx = FacesContext.getCurrentInstance();
            ctx.getApplication().getNavigationHandler().handleNavigation(ctx, null, "/index.xhtml?faces-redirect=true");
            return ;
        }
        this.desafio = d;
    }

    public void aceptarDesafio(ActionEvent e) {
        try {
            ladderService.aceptarDesafio(idDesafio);
            Util.addInfoMessage("Desafio aceptado satisfactoriamente", null);
        } catch (BusinessLogicException ex) {
            Util.addErrorMessage("Error al aceptar desafio.", ex.getMessage());
        }
    }
    
    public void rechazarDesafio(ActionEvent e) {
        try {
            ladderService.rechazarDesafio(idDesafio);
            Util.addInfoMessage("Desafio rechazado satisfactoriamente.", null);
        } catch (BusinessLogicException ex) {
            Util.addErrorMessage("Error al rechazar desafio.", ex.getMessage());
        }
    }
    
    public void cancelarDesafioByAdmin(ActionEvent e) {
        try {
            ladderService.cancelarDesafioByAdmin(idDesafio);
            Util.addInfoMessage("Desafio cancelado satisfactoriamente.", null);
        } catch (BusinessLogicException ex) {
            Util.addErrorMessage("Error al cancelar desafio.", ex.getMessage());
        }
    }

    public void confirmarResultado(ActionEvent e) {
        try {
            ladderService.confirmarResultadoDesafio(idDesafio);
            Util.addInfoMessage("Resultado confirmado.", null);
        } catch (BusinessLogicException ex) {
            Util.addErrorMessage("Error al confirmar resultado.", ex.getMessage());
        }
    }

    public void eliminarResultado(ActionEvent e) {
        try {
            ladderService.eliminarReporteLadder(idDesafio);
            Util.addInfoMessage("Reporte eliminado satisfactoriamente.", null);
        } catch (BusinessLogicException ex) {
            Util.addErrorMessage("Error al eliminar resultado.", ex.getMessage());
        }
    }
    

}

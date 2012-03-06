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
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import model.entities.torneos.Game;
import model.entities.torneos.GameMatch;
import model.entities.torneos.facades.GameFacade;
import model.entities.torneos.facades.GameMatchFacade;
import model.exceptions.BusinessLogicException;
import model.services.TorneoService;
import utils.Util;

/**
 *
 * @author Pablo
 */
@ManagedBean(name="eliminarReporteMB")
@RequestScoped
public class EliminarReporteMB implements Serializable {

    @EJB private TorneoService torneoService;
    @EJB private GameFacade gameFac;
    @EJB private GameMatchFacade matchFac;

    private Long idGame;

    private Game game;

    /** Creates a new instance of EliminarReporteMB */
    public EliminarReporteMB() {
        this.game = new Game();
    }

    public Long getIdGame() {
        return idGame;
    }

    public void setIdGame(Long idGame) {
        this.idGame = idGame;
    }
    
    public Game getGame() {
        return this.game;
    }
    
    public void setGame(Game game) {
        this.game = game;
    }

    public void loadGame(ComponentSystemEvent e) {
        if(this.idGame == null) {
            FacesContext ctx = FacesContext.getCurrentInstance();
            ctx.getApplication().getNavigationHandler().handleNavigation(ctx, null, "/index.xhtml?faces-redirect=true");
            return ;
        }
        Game g = gameFac.find(idGame);
        if(g == null) {
            FacesContext ctx = FacesContext.getCurrentInstance();
            ctx.getApplication().getNavigationHandler().handleNavigation(ctx, null, "/index.xhtml?faces-redirect=true");
            return ;
        }
        this.game = g;
    }

    public String eliminarReporte() {
        FacesContext ctx = FacesContext.getCurrentInstance();
        ctx.getExternalContext().getFlash().setKeepMessages(true);
        try {
            torneoService.eliminarReporte(idGame);
            Util.addInfoMessage("Reporte eliminado satisfactoriamente.", null);
        } catch (BusinessLogicException ex) {
            Util.addErrorMessage("Error al eliminar reporte.", ex.getMessage());
        }
        
        return "/web/torneos/ListarTorneos.xhtml?faces-redirect=true";
    }

}

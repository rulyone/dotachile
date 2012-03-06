/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package controller.torneos;

import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ComponentSystemEvent;
import model.entities.torneos.GameMatch;
import model.entities.torneos.Resultado;
import model.entities.torneos.facades.GameMatchFacade;
import model.exceptions.BusinessLogicException;
import model.services.ComentariosService;
import model.services.TorneoService;
import utils.Util;

/**
 *
 * @author Pablo
 */
@ManagedBean(name = "verMatchMB")
@RequestScoped
public class VerMatchMB implements Serializable {

    @EJB private GameMatchFacade matchFac;
    @EJB private TorneoService torneoService;
    @EJB private ComentariosService comentariosService;

    private Long idMatch;
    private GameMatch match;
    
    //var para modificar el bestof...
    private Integer bestOf;

    //vars para reportar. . .
    private String tagSentinel;
    private String tagScourge;
    private List<String> sentinelUsernames;
    private List<String> scourgeUsernames;
    private Resultado resultado;
    private Long idReplay;
    private String comentarioMatch;

    /** Creates a new instance of VerMatchMB */
    public VerMatchMB() {
        match = new GameMatch();
    }

    public String getComentarioMatch() {
        return comentarioMatch;
    }

    public void setComentarioMatch(String comentarioMatch) {
        this.comentarioMatch = comentarioMatch;
    }

    public Integer getBestOf() {
        return bestOf;
    }

    public void setBestOf(Integer bestOf) {
        this.bestOf = bestOf;
    }

    public Long getIdReplay() {
        return idReplay;
    }

    public void setIdReplay(Long idReplay) {
        this.idReplay = idReplay;
    }

    public Resultado getResultado() {
        return resultado;
    }

    public void setResultado(Resultado resultado) {
        this.resultado = resultado;
    }

    public List<String> getScourgeUsernames() {
        return scourgeUsernames;
    }

    public void setScourgeUsernames(List<String> scourgeUsernames) {
        this.scourgeUsernames = scourgeUsernames;
    }

    public List<String> getSentinelUsernames() {
        return sentinelUsernames;
    }

    public void setSentinelUsernames(List<String> sentinelUsernames) {
        this.sentinelUsernames = sentinelUsernames;
    }

    public String getTagScourge() {
        return tagScourge;
    }

    public void setTagScourge(String tagScourge) {
        this.tagScourge = tagScourge;
    }

    public String getTagSentinel() {
        return tagSentinel;
    }

    public void setTagSentinel(String tagSentinel) {
        this.tagSentinel = tagSentinel;
    }

    public Long getIdMatch() {
        return idMatch;
    }

    public void setIdMatch(Long idMatch) {
        this.idMatch = idMatch;
    }

    public GameMatch getMatch() {
        return match;
    }

    public void setMatch(GameMatch match) {
        this.match = match;
    }

    public void loadMatch(ComponentSystemEvent e) {
        if(this.idMatch == null) {
            FacesContext ctx = FacesContext.getCurrentInstance();
            ctx.getApplication().getNavigationHandler().handleNavigation(ctx, null, "/index.xhtml?faces-redirect=true");
            return ;
        }
        GameMatch m = matchFac.find(idMatch);
        if(m == null) {
            FacesContext ctx = FacesContext.getCurrentInstance();
            ctx.getApplication().getNavigationHandler().handleNavigation(ctx, null, "/index.xhtml?faces-redirect=true");
            return ;
        }
        this.match = m;
        this.bestOf = m.getBestOf();
    }

//    public void reportarGame() {
//        try {
//            torneoService.reportarGame(idMatch, tagSentinel, tagScourge, sentinelUsernames, scourgeUsernames, resultado, idReplay);
//            Util.addInfoMessage("Game reportado satisfactoriamente.", null);
//        } catch (BusinessLogicException ex) {
//            Util.addErrorMessage("Error al reportar.", ex.getMessage());
//        }
//    }

    public void confirmarMatch(ActionEvent e) {
        try {
            torneoService.confirmarMatch(idMatch);
            Util.addInfoMessage("Match confirmado satisfactoriamente.", null);
        } catch (BusinessLogicException ex) {
            Util.addErrorMessage("Error al confirmar match.", ex.getMessage());
        }
    }

    public String eliminarPareo() {
        try {
            torneoService.eliminarPareo(idMatch);
            Util.addInfoMessage("Pareo eliminado satisfactoriamente.", null);
        } catch (BusinessLogicException ex) {
            Util.addErrorMessage("Error al eliminar el pareo.", ex.getMessage());
            return null;
        }
        return "/web/torneos/VerTorneo.xhtml?faces-redirect=true&amp;idTorneo=" + this.match.getRonda().getTorneo().getId();
    }
    
    public String eliminarPareoInseguro() {
        try {
            torneoService.eliminarPareoInseguro(idMatch);
            Util.addInfoMessage("Pareo eliminado satisfactoriamente.", null);
        } catch (BusinessLogicException ex) {
            Util.addErrorMessage("Error al eliminar el pareo.", ex.getMessage());
            return null;
        }
        return "/web/torneos/VerTorneo.xhtml?faces-redirect=true&amp;idTorneo=" + this.match.getRonda().getTorneo().getId();
    }

    public void modificarBestOf() {
        try {
            torneoService.modificarBestOfMatch(idMatch, this.bestOf);
            Util.addInfoMessage("Modificacion del Best Of satisfactoria.", null);
            this.match.setBestOf(this.bestOf);
        } catch (BusinessLogicException ex) {
            Util.addErrorMessage("Error al modificar el Best Of.", ex.getMessage());
        }
    }

    public void agregarComentarioMatch(ActionEvent e) {
        try {
            comentariosService.agregarComentarioMatch(this.idMatch, comentarioMatch);
            Util.addInfoMessage("Comentario agregado satisfactoriamente.", null);
            this.comentarioMatch = "";
        } catch (BusinessLogicException ex) {
            Util.addErrorMessage("Error al agregar comentario.", ex.getMessage());
        }
    }

}

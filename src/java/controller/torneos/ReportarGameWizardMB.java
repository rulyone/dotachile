/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package controller.torneos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ViewScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.model.SelectItem;
import model.entities.base.Usuario;
import model.entities.torneos.GameMatch;
import model.entities.torneos.Resultado;
import model.entities.torneos.facades.GameMatchFacade;
import model.exceptions.BusinessLogicException;
import model.services.TorneoService;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DualListModel;
import org.primefaces.model.UploadedFile;
import utils.Util;

/**
 *
 * @author Pablo
 */
@ManagedBean(name="reportarGameWizardMB")
@ViewScoped
public class ReportarGameWizardMB implements Serializable {

    @EJB private TorneoService torneoService;
    @EJB private GameMatchFacade matchFac;

    private Long idMatch;
    private GameMatch match;

    //wizard stuff
    private String tagSentinel;
    private String tagScourge;
    private Resultado resultado;
    private Long idReplay;

    //wizard stuff.
    private int step = 0;
    private SelectItem clan1Item;
    private SelectItem clan2Item;

    private DualListModel<Usuario> playersSentinel;
    private DualListModel<Usuario> playersScourge;
    private UploadedFile uploadedReplay;

    public boolean replaySubido = false;

    /** Creates a new instance of ReportarGameWizardMB */
    public ReportarGameWizardMB() {
        playersSentinel = new DualListModel<Usuario>();
        playersScourge = new DualListModel<Usuario>();
    }

    public boolean isReplaySubido() {
        return replaySubido;
    }

    public void setReplaySubido(boolean replaySubido) {
        this.replaySubido = replaySubido;
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

    public DualListModel<Usuario> getPlayersScourge() {
        return playersScourge;
    }

    public void setPlayersScourge(DualListModel<Usuario> playersScourge) {
        this.playersScourge = playersScourge;
    }

    public DualListModel<Usuario> getPlayersSentinel() {
        return playersSentinel;
    }

    public void setPlayersSentinel(DualListModel<Usuario> playersSentinel) {
        this.playersSentinel = playersSentinel;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public Long getIdMatch() {
        return idMatch;
    }

    public void setIdMatch(Long idMatch) {
        this.idMatch = idMatch;
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

    public SelectItem getClan1Item() {
        return clan1Item;
    }

    public SelectItem getClan2Item() {
        return clan2Item;
    }

    public Resultado[] getResultadoValues() {
        return Resultado.values();
    }

    public void loadMatch(ComponentSystemEvent e) {
        if(this.idMatch == null) {
            FacesContext ctx = FacesContext.getCurrentInstance();
            ctx.getApplication().getNavigationHandler().handleNavigation(ctx, null, "/index.xhtml?faces-redirect=true");
            return ;
        }
        if(this.match != null)
            return ;
        GameMatch m = matchFac.find(idMatch);
        if(m == null) {
            FacesContext ctx = FacesContext.getCurrentInstance();
            ctx.getApplication().getNavigationHandler().handleNavigation(ctx, null, "/index.xhtml?faces-redirect=true");
            return ;
        }
        this.match = m;

        clan1Item = new SelectItem();
        clan1Item.setLabel(m.getClan1().getTag());
        clan1Item.setValue(m.getClan1().getTag());

        clan2Item = new SelectItem();
        clan2Item.setLabel(m.getClan2().getTag());
        clan2Item.setValue(m.getClan2().getTag());
        
        step = 0;
    }

    public void elegirResultado(ActionEvent e) {

        if(this.tagSentinel.equals(this.tagScourge)) {
            Util.addErrorMessage("Ambos clanes no pueden ser de la misma faccion...", null);
            return ;
        }
        if(resultado.equals(Resultado.WIN_SENTINEL) || resultado.equals(Resultado.WIN_SCOURGE)) {
            if(this.tagSentinel.equals(match.getClan1().getTag())) {
                //sentinels = clan1, scourges = clan2
                this.playersSentinel = new DualListModel<Usuario>();
                this.playersScourge = new DualListModel<Usuario>();
                List<Usuario> sents = new ArrayList<Usuario>();
                for(Usuario u : match.getClan1().getIntegrantes()) {
                    sents.add(u);
                }
                List<Usuario> scrgs = new ArrayList<Usuario>();
                for(Usuario u : match.getClan2().getIntegrantes()) {
                    scrgs.add(u);
                }
                this.playersSentinel.setSource(sents);
                this.playersScourge.setSource(scrgs);
            }else{
                //scourges = caln1, sentinels = clan2
                this.playersSentinel = new DualListModel<Usuario>();
                this.playersScourge = new DualListModel<Usuario>();
                List<Usuario> sents = new ArrayList<Usuario>();
                for(Usuario u : match.getClan2().getIntegrantes()) {
                    sents.add(u);
                }
                List<Usuario> scrgs = new ArrayList<Usuario>();
                for(Usuario u : match.getClan1().getIntegrantes()) {
                    scrgs.add(u);
                }
                this.playersSentinel.setSource(sents);
                this.playersScourge.setSource(scrgs);
            }
            step = 1;
        }else{
            //wo, no se necesita elegir players ni subir replay
            step = 3;
        }
        
        
    }

    public void elegirPlayers(ActionEvent e) {
        int sentLen = this.playersSentinel.getTarget().size();
        int scrgLen = this.playersScourge.getTarget().size();
        if(sentLen < 3 || scrgLen < 3){
            Util.addErrorMessage("Debes elegir al menos 3 player por cada Clan.", null);
            return ;
        }else if(sentLen > 5 || scrgLen > 5) {
            Util.addErrorMessage("Debes elegir maximo 5 players por cada Clan.", null);
            return ;
        }
        step = 2;
    }

    public void uploadReplayHandler(FileUploadEvent e) {
        this.uploadedReplay = e.getFile();
        if(this.uploadedReplay != null)
            this.replaySubido = true;
        System.out.println("PASE POR ACA");
    }
    
    public void subirReplay(ActionEvent e) {
        step = 3;
    }

    public void reportarGame(ActionEvent e) {
        try {
            if(this.uploadedReplay != null)
                torneoService.reportarGame(idMatch, tagSentinel, tagScourge, this.playersSentinel.getTarget(), this.playersScourge.getTarget(), resultado, this.uploadedReplay.getContents());
            else
                torneoService.reportarGame(idMatch, tagSentinel, tagScourge, this.playersSentinel.getTarget(), this.playersScourge.getTarget(), resultado, null);
            Util.addInfoMessage("Game reportado satisfactoriamente.", null);
        } catch (BusinessLogicException ex) {
            Util.addErrorMessage("Error al reportar game.", ex.getMessage());
        }
    }

    public void back(ActionEvent e) {
        if(this.step > 0)
            --step;
    }

}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package controller.torneos.ladder;

import java.io.Serializable;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ComponentSystemEvent;

import model.entities.base.Usuario;
import model.entities.torneos.Desafio;
import model.entities.torneos.Resultado;
import model.entities.torneos.facades.DesafioFacade;
import model.exceptions.BusinessLogicException;
import model.services.LadderService;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DualListModel;
import org.primefaces.model.UploadedFile;
import utils.Util;

/**
 *
 * @author Pablo
 */
@ManagedBean(name="reportarGameLadderWizardMB")
@ViewScoped
public class ReportarGameLadderWizardMB implements Serializable {

    @EJB private LadderService ladderService;
    @EJB private DesafioFacade desafioFac;

    private Long idDesafio;
    private Desafio desafio;
    
    private String tagSentinel;
    private String tagScourge;
    private Resultado resultado;
    private Long idReplay;
    
    private int step = 0;
    private DualListModel<Usuario> playersSentinel;
    private DualListModel<Usuario> playersScourge;
    private UploadedFile uploadedReplay;

    private boolean replaySubido = false;

    /** Creates a new instance of ReportarGameLadderWizardMB */
    public ReportarGameLadderWizardMB() {
        playersSentinel = new DualListModel<Usuario>();
        playersScourge = new DualListModel<Usuario>();
    }

    public boolean isReplaySubido() {
        return replaySubido;
    }

    public void setReplaySubido(boolean replaySubido) {
        this.replaySubido = replaySubido;
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

    public Long getIdReplay() {
        return idReplay;
    }

    public void setIdReplay(Long idReplay) {
        this.idReplay = idReplay;
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

    public Resultado getResultado() {
        return resultado;
    }

    public void setResultado(Resultado resultado) {
        this.resultado = resultado;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
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

    public Resultado[] getResultadoValues() {
        Resultado[] resultados = {Resultado.WIN_SENTINEL, Resultado.WIN_SCOURGE};
        ExternalContext ectx = FacesContext.getCurrentInstance().getExternalContext();
        Principal principal = ectx.getUserPrincipal();
        if (principal != null && (ectx.isUserInRole("ADMIN_DOTA") || ectx.isUserInRole("ADMIN_ROOT") || ectx.isUserInRole("ADMIN_LADDER")) ) {
            resultados = Resultado.values();
        }        
        return resultados;
    }

    public void loadDesafio(ComponentSystemEvent e) {
        if(this.idDesafio == null) {
            FacesContext ctx = FacesContext.getCurrentInstance();
            ctx.getApplication().getNavigationHandler().handleNavigation(ctx, null, "/index.xhtml?faces-redirect=true");
            return ;
        }
        if(this.desafio != null)
            return ;
        Desafio d = desafioFac.find(idDesafio);
        if(d == null) {
            FacesContext ctx = FacesContext.getCurrentInstance();
            ctx.getApplication().getNavigationHandler().handleNavigation(ctx, null, "/index.xhtml?faces-redirect=true");
            return ;
        }
        this.desafio = d;
        step = 0;
    }

    public void elegirResultado(ActionEvent e) {
        if(this.tagSentinel.equals(this.tagScourge)) {
            Util.addErrorMessage("Ambos clanes no pueden ser de la misma faccion...", null);
            return ;
        }
        if(resultado.equals(Resultado.WIN_SENTINEL) || resultado.equals(Resultado.WIN_SCOURGE)) {
            if(this.tagSentinel.equals(desafio.getDesafiador().getTag())) {
                //sentinels = desafiador, scourge = rival
                this.playersSentinel = new DualListModel<Usuario>();
                this.playersScourge = new DualListModel<Usuario>();
                List<Usuario> sents = new ArrayList<Usuario>();
                for(Usuario u : desafio.getDesafiador().getIntegrantes()) {
                    sents.add(u);
                }
                List<Usuario> scrgs = new ArrayList<Usuario>();
                for(Usuario u : desafio.getRival().getIntegrantes()) {
                    scrgs.add(u);
                }
                this.playersSentinel.setSource(sents);
                this.playersScourge.setSource(scrgs);
            }else{
                //sentinels = rival, scourges = desafiador
                this.playersSentinel = new DualListModel<Usuario>();
                this.playersScourge = new DualListModel<Usuario>();
                List<Usuario> sents = new ArrayList<Usuario>();
                for(Usuario u : desafio.getRival().getIntegrantes()) {
                    sents.add(u);
                }
                List<Usuario> scrgs = new ArrayList<Usuario>();
                for(Usuario u : desafio.getDesafiador().getIntegrantes()) {
                    scrgs.add(u);
                }
                this.playersSentinel.setSource(sents);
                this.playersScourge.setSource(scrgs);
            }
            step = 1;
        }else {
            //wo, no se necesita elegir players ni subir replay.
            step = 3;
        }
    }

    public void elegirPlayers(ActionEvent e) {
        int sentLen = this.playersSentinel.getTarget().size();
        int scrgLen = this.playersScourge.getTarget().size();
        if(sentLen < 3 || scrgLen < 3) {
            Util.addErrorMessage("Debes elegir al menos 3 player por cada Clan.", null);
            return ;
        }else if(sentLen > 5 || scrgLen > 5) {
            Util.addErrorMessage("Debes elegir maximo 5 players por cada Clan.", null);
        }
        step = 2;
    }

    public void uploadReplayHandler(FileUploadEvent e) {
        this.uploadedReplay = e.getFile();
        if(this.uploadedReplay != null)
            this.replaySubido = true;
    }
    public void subirReplay(ActionEvent e) {
        step = 3;
    }

    public void reportarGame(ActionEvent e) {
        try {
            if(this.uploadedReplay != null)
                ladderService.reportarGameLadder(idDesafio, tagSentinel, tagScourge, this.playersSentinel.getTarget(), this.playersScourge.getTarget(), resultado, this.uploadedReplay.getContents());
            else
                ladderService.reportarGameLadder(idDesafio, tagSentinel, tagScourge, this.playersSentinel.getTarget(), this.playersScourge.getTarget(), resultado, null);
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

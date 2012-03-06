/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package controller.torneos.ladder;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ComponentSystemEvent;
import model.entities.base.Clan;
import model.entities.base.Usuario;
import model.entities.base.facades.ClanFacade;
import model.entities.noticias.Noticia;
import model.entities.torneos.Desafio;
import model.entities.torneos.FactorK;
import model.entities.torneos.FaseLadder;
import model.entities.torneos.Ladder;
import model.entities.torneos.facades.DesafioFacade;
import model.entities.torneos.facades.LadderFacade;
import model.exceptions.BusinessLogicException;
import model.services.LadderService;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import utils.Util;

/**
 *
 * @author Pablo
 */
@ManagedBean(name="verLadderMB")
@ViewScoped
public class VerLadderMB implements Serializable {

    @EJB private LadderService ladderService;
    @EJB private LadderFacade ladderFac;
    @EJB private DesafioFacade desafioFac;

    @EJB private ClanFacade clanFac;
    
    private Ladder ladder;

    private LazyDataModel<Clan> lazyModel;

    private List<Desafio> desafiosPendientes;

    /** Creates a new instance of VerLadderMB */
    public VerLadderMB() {
    }

    public Ladder getLadder() {
        return ladder;
    }

    public void setLadder(Ladder ladder) {
        this.ladder = ladder;
    }

    public LazyDataModel<Clan> getLazyModel() {
        return lazyModel;
    }

    public void setLazyModel(LazyDataModel<Clan> lazyModel) {
        this.lazyModel = lazyModel;
    }

    public List<Desafio> getDesafiosPendientes() {
        return desafiosPendientes;
    }

    public void setDesafiosPendientes(List<Desafio> desafiosPendientes) {
        this.desafiosPendientes = desafiosPendientes;
    }

    @PostConstruct
    private void loadAll() {
        //por el momento suporteamos 1 ladder solamente //TODO list...
        this.ladder = ladderFac.find(1L);
        if(this.ladder == null) {
            FacesContext ctx = FacesContext.getCurrentInstance();
            ctx.getApplication().getNavigationHandler().handleNavigation(ctx, null, "/index.xhtml?faces-redirect=true");
            return ;
        }

        this.lazyModel = new LazyDataModel<Clan>() {
           
            @Override
            public List<Clan> load(int first, int pageSize, String string, SortOrder so, Map<String, String> map) {
                this.setRowCount(clanFac.rankClanesCount());
                return clanFac.rankClanesLimit(first, pageSize);
            }
            
            @Override
            public Clan getRowData(String rowKey) {
                return clanFac.find(Long.parseLong(rowKey));
            }

            @Override
            public Object getRowKey(Clan clan) {
                return clan.getId();
            }
            
        };
        this.lazyModel.setRowCount(clanFac.rankClanesCount());

        //cargar desafioPendiente siempre y cuando el user tenga clan y este logeado obviously.
        Usuario user = Util.getUsuarioLogeado();
        if(user != null && user.getClan() != null) {
            this.desafiosPendientes = desafioFac.desafiosPendientes(user.getClan().getTag());
        }
    }

    public void pausarLadder(ActionEvent e) {
        try {
            ladderService.pausarLadder(ladder.getId());
            Util.addInfoMessage("Ladder pausado satisfactoriamente.", null);
            this.ladder.setFaseLadder(FaseLadder.PAUSED);
        } catch (BusinessLogicException ex) {
            Util.addErrorMessage("Error al pausar ladder", ex.getMessage());
        }
    }

    public void despausarLadder(ActionEvent e) {
        try {
            ladderService.despausarLadder(ladder.getId());
            Util.addInfoMessage("Ladder resumido satisfactoriamente.", null);
            this.ladder.setFaseLadder(FaseLadder.STARTED);
        } catch (BusinessLogicException ex) {
            Util.addErrorMessage("Error al despausar ladder", ex.getMessage());
        }
    }

    public void onRowSelectNavigate(SelectEvent event) {
        Clan clan = (Clan)event.getObject();
        Util.navigate("/web/clanes/VerClan.xhtml?faces-redirect=true&amp;tag=" + clan.getTag());
    }
}

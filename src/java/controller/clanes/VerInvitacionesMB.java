/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package controller.clanes;

import java.io.Serializable;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import model.entities.base.Clan;
import model.entities.base.Usuario;
import model.entities.base.facades.UsuarioFacade;
import model.exceptions.BusinessLogicException;
import model.services.ClanService;
import utils.Util;

/**
 *
 * @author Pablo
 */
@ManagedBean(name="verInvitacionesMB")
@RequestScoped
public class VerInvitacionesMB implements Serializable {

    @EJB private ClanService clanService;

    @EJB private UsuarioFacade userFac;

    private List<String> tagsClanesInvitadores = new ArrayList<String>();

    private String selectedTagClan;

    /** Creates a new instance of VerInvitacionesMB */
    public VerInvitacionesMB() {
        
    }

    @PostConstruct
    private void loadTagsClanesInvitadores() {
        this.tagsClanesInvitadores.clear();
        Principal principal = FacesContext.getCurrentInstance().getExternalContext().getUserPrincipal();
        if(principal != null && principal.getName() != null) {
            Usuario user = userFac.findByUsername(principal.getName());
            if(user != null) {
                for(Clan c : user.getInvitacionesDeClan()) {
                    this.tagsClanesInvitadores.add(c.getTag());
                }
            }
        }
    }

    public List<String> getTagsClanesInvitadores() {
        return tagsClanesInvitadores;
    }

    public String getSelectedTagClan() {
        return selectedTagClan;
    }

    public void setSelectedTagClan(String selectedTagClan) {
        this.selectedTagClan = selectedTagClan;
    }

    public String aceptarInvitacion() {
        String ret = null;
        try {
            clanService.aceptarInvitacion(selectedTagClan);
            Util.addInfoMessage("Ya eres parte del clan " + this.selectedTagClan, null);
            ret = "/web/clanes/VerClan.xhtml?faces-redirect=true&amp;tag=" + this.selectedTagClan;
            Util.keepMessages();
        } catch (BusinessLogicException ex) {
            Util.addErrorMessage("Error al aceptar invitacion", ex.getMessage());
        }
        return ret;
    }

    public String rechazarInvitacion() {
        try {
            clanService.rechazarInvitacion(selectedTagClan);
            Util.addInfoMessage("Rechazo de la invitacion satisfactorio.", null);
        } catch (BusinessLogicException ex) {
            Util.addErrorMessage("Error al aceptar invitacion", ex.getMessage());
        }
        this.loadTagsClanesInvitadores();
        return null;
    }

}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package controller.clanes;

import java.io.Serializable;
import java.security.Principal;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import model.entities.base.Clan;
import model.entities.base.Usuario;
import model.entities.base.facades.ClanFacade;
import model.entities.base.facades.UsuarioFacade;
import model.exceptions.BusinessLogicException;
import model.services.ClanService;
import utils.Util;

/**
 *
 * @author Pablo
 */
@ManagedBean(name="dejarClanMB")
@RequestScoped
public class DejarClanMB implements Serializable {

    @EJB
    private ClanService clanService;
        
    @EJB
    private UsuarioFacade userFac;

    private Clan clan;

    /** Creates a new instance of DejarClanMB */
    public DejarClanMB() {
    }

    @PostConstruct
    public void load() {
        this.clan = null;
        Principal principal = FacesContext.getCurrentInstance().getExternalContext().getUserPrincipal();
        if(principal != null && principal.getName() != null) {
            Usuario user = userFac.findByUsername(principal.getName());
            if(user != null) {
                this.clan = user.getClan();
            }
        }
    }

    public Clan getClan() {
        return clan;
    }

    public void setClan(Clan clan) {
        this.clan = clan;
    }

    public String dejarClan() {
        try {
            clanService.dejarClan();
            Util.addInfoMessage("Has dejado el clan.", null);
        } catch (BusinessLogicException ex) {
            Util.addErrorMessage("Error al dejar clan", ex.getMessage());
        }
        this.load();
        return null;
    }

}

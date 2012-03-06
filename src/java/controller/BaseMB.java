/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package controller;

import java.io.Serializable;
import java.security.Principal;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import model.entities.base.Usuario;
import model.entities.base.facades.UsuarioFacade;
import utils.Util;

/**
 *
 * @author Pablo
 */
@ManagedBean(name="baseMB")
@RequestScoped
public class BaseMB implements Serializable {

    @EJB private UsuarioFacade userFac;

    /** Creates a new instance of BaseMB */
    public BaseMB() {
    }

    @PostConstruct
    public void load() {

    }

    public String goToOwnClan() {
        Principal principal = FacesContext.getCurrentInstance().getExternalContext().getUserPrincipal();
        if(principal != null && principal.getName() != null) {
            Usuario user = userFac.findByUsername(principal.getName());
            if(user != null && user.getClan() != null) {
                return "/web/clanes/VerClan.xhtml?faces-redirect=true&amp;tag=" + user.getClan().getTag();
            }
        }
        Util.addWarnMessage("No tienes clan", null);
        return null;
    }

}

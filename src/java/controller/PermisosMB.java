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

/**
 *
 * @author Pablo
 */
@ManagedBean(name="permisosMB")
@RequestScoped
public class PermisosMB implements Serializable {

    @EJB private UsuarioFacade userFac;
    private Usuario user = null;

    /** Creates a new instance of PermisosMB */
    public PermisosMB() {
    }

    @PostConstruct
    public void postConstruct() {
        Principal principal = FacesContext.getCurrentInstance().getExternalContext().getUserPrincipal();
        if(principal != null) {
            this.user = userFac.findByUsername(principal.getName());
        }
    }

    public Usuario getUser() {
        return user;
    }

}

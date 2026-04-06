/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dotachile.auth.controller;

import java.io.Serializable;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ComponentSystemEvent;
import com.dotachile.auth.entity.Perfil;
import com.dotachile.auth.entity.Usuario;
import com.dotachile.auth.facade.GrupoFacade;
import com.dotachile.auth.facade.PerfilFacade;
import com.dotachile.auth.facade.UsuarioFacade;
import com.dotachile.auth.service.AdminService;


/**
 *
 * @author Pablo
 */
@ManagedBean(name="editarPerfilMB")
@ViewScoped
public class EditarPerfilMB implements Serializable {

    @EJB private UsuarioFacade userFac;
    @EJB private PerfilFacade perfilFac;
    @EJB private GrupoFacade grupoFac;
    @EJB private AdminService adminService;

    private String username;

    private Usuario usuario;
    private Perfil perfil;


    /** Creates a new instance of EditarPerfilMB */
    public EditarPerfilMB() {
    }

    public Perfil getPerfil() {
        return perfil;
    }

    public void setPerfil(Perfil perfil) {
        this.perfil = perfil;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public void loadPerfil(ComponentSystemEvent e) {
        if(this.username == null) {
            FacesContext ctx = FacesContext.getCurrentInstance();
            ctx.getApplication().getNavigationHandler().handleNavigation(ctx, null, "/index.xhtml?faces-redirect=true");
            return ;
        }
        Usuario u = userFac.findByUsername(username);
        if(u == null) {
            FacesContext ctx = FacesContext.getCurrentInstance();
            ctx.getApplication().getNavigationHandler().handleNavigation(ctx, null, "/index.xhtml?faces-redirect=true");
            return ;
        }
        Perfil p = perfilFac.findByUsername(username);
        if(p == null) {
            FacesContext ctx = FacesContext.getCurrentInstance();
            ctx.getApplication().getNavigationHandler().handleNavigation(ctx, null, "/index.xhtml?faces-redirect=true");
            return ;
        }
        this.usuario = u;
        this.perfil = p;


    }

    

}

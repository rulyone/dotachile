/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package controller.usuarios;

import java.io.Serializable;
import javax.ejb.EJB;
import javax.faces.bean.ViewScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import model.entities.base.Usuario;
import model.entities.base.facades.UsuarioFacade;
import model.exceptions.BusinessLogicException;
import model.services.AdminService;
import utils.Util;

/**
 *
 * @author Pablo
 */
@ManagedBean(name="banearUsuarioMB")
@ViewScoped
public class BanearUsuarioMB implements Serializable {

    @EJB private UsuarioFacade userFac;
    @EJB private AdminService adminService;

    private String username;
    private Usuario usuario;

    private String razonBan;
    private Integer diasBan;

    /** Creates a new instance of BanearUsuarioMB */
    public BanearUsuarioMB() {
    }

    public Integer getDiasBan() {
        return diasBan;
    }

    public void setDiasBan(Integer diasBan) {
        this.diasBan = diasBan;
    }

    public String getRazonBan() {
        return razonBan;
    }

    public void setRazonBan(String razonBan) {
        this.razonBan = razonBan;
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

    public void loadUsuario(ComponentSystemEvent e) {
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

        this.usuario = u;
    }

    public String banearUsuario() {
        try {
            adminService.banUser(username, razonBan, diasBan);
            Util.addInfoMessage("Usuario baneado satisfactoriamente.", null);
        } catch (BusinessLogicException ex) {
            Util.addErrorMessage("Error al banear.", ex.getMessage());
            return null;
        }
        return "/index.xhtml";
    }
}

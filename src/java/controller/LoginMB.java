/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package controller;

import java.io.Serializable;
import java.util.Date;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.application.FacesMessage;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import model.entities.base.Usuario;
import model.entities.base.facades.UsuarioFacade;
import utils.Util;

/**
 *
 * @author Pablo
 */
@ManagedBean(name="loginMB")
@SessionScoped
public class LoginMB implements Serializable {

    @EJB private UsuarioFacade userFac;
    
    private Usuario user;

    //para programmatic login.
    private String username;
    private String password;

    /** Creates a new instance of LoginMB */
    public LoginMB() {
    }

    public Usuario getUser() {
        return user;
    }

    public void setUser(Usuario user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void login(ActionEvent e) {
        FacesContext ctx = FacesContext.getCurrentInstance();
        HttpServletRequest req = (HttpServletRequest) ctx.getExternalContext().getRequest();
        try {
            req.login(username, password);
            this.user = userFac.findByUsername(username);
            ExternalContext exctx =
                    (ExternalContext) FacesContext.getCurrentInstance().getExternalContext();

            this.user.setLastLogin(new Date());
            userFac.edit(this.user);
            exctx.getSessionMap().put("user", this.user);

        } catch (ServletException ex) {
            Util.addErrorMessage("Login fallido", "Username o password no coinciden.");
        }
        this.password = null;
    }

    public String logout() {
        FacesContext ctx = FacesContext.getCurrentInstance();
        HttpServletRequest req = (HttpServletRequest) ctx.getExternalContext().getRequest();
        try {
            req.logout();
            ctx.getExternalContext().getSessionMap().remove("user");
            req.getSession(false).invalidate();
            this.user = null;
            this.username = null;
            this.password = null;
            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Te has deslogeado satisfactoriamente.",""));
        } catch (ServletException ex) {
            ctx.addMessage(null, new FacesMessage("Error al deslogear"));
        } catch (IllegalStateException ex) {
            ctx.addMessage(null, new FacesMessage("Error al deslogear"));
        }
        return "/index.xhtml";
    }

}

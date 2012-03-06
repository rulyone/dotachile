/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.usuarios;

import java.security.Principal;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import model.entities.base.Usuario;
import model.entities.base.facades.UsuarioFacade;
import model.exceptions.BusinessLogicException;
import model.services.BasicService;
import utils.Util;

/**
 *
 * @author rulyone
 */
@ManagedBean
@RequestScoped
public class CreacionCuentaMB {
    
    private @EJB UsuarioFacade userFac;
    private @EJB BasicService basicService;
    
    private Usuario usuario;
    
    private String w3username;
    private String w3password;
    private String w3passwordretry;
    
    private String w3usernamebot;
    private String w3passwordbot;
    private String w3passwordbotretry;

    /** Creates a new instance of CreacionCuentasMB */
    public CreacionCuentaMB() {
    }
    
    @PostConstruct
    public void postConstruct() {
        Principal principal = FacesContext.getCurrentInstance().getExternalContext().getUserPrincipal();
        if(principal != null) {
            this.usuario = userFac.findByUsername(principal.getName());
        }
    }
    
    public String crearCuentaW3() {
        if (!w3password.equals(w3passwordretry)) {
            Util.addErrorMessage("Passwords no coinciden.", null);
            w3username = w3password = null;
            return null;
        }
        try {
            basicService.crearCuentaW3(w3username, w3password);
            Util.addInfoMessage("Cuenta creada satisfactoriamente.", null);
        } catch (BusinessLogicException ex) {
            Util.addErrorMessage("Error al crear la cuenta", ex.getMessage());
            w3username = w3password = null;
            return null;
        }
        w3username = w3password = null;
        return "/index.xhtml";
    }
    
    public String crearCuentaW3Bot() {
        if (!w3passwordbot.equals(w3passwordbotretry)) {
            Util.addErrorMessage("Passwords no coinciden.", null);
            w3usernamebot = w3passwordbot = null;
            return null;
        }
        try {
            basicService.crearCuentaW3BOT(w3usernamebot, w3passwordbot);
            Util.addInfoMessage("Cuenta creada satisfactoriamente.", null);
        } catch (BusinessLogicException ex) {
            Util.addErrorMessage("Error al crear la cuenta", ex.getMessage());
            w3usernamebot = w3passwordbot = null;
            return null;
        }
        w3usernamebot = w3passwordbot = null;
        return "/index.xhtml";
    }

    public String getW3passwordbotretry() {
        return w3passwordbotretry;
    }

    public void setW3passwordbotretry(String w3passwordbotretry) {
        this.w3passwordbotretry = w3passwordbotretry;
    }

    public String getW3passwordretry() {
        return w3passwordretry;
    }

    public void setW3passwordretry(String w3passwordretry) {
        this.w3passwordretry = w3passwordretry;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getW3password() {
        return w3password;
    }

    public void setW3password(String w3password) {
        this.w3password = w3password;
    }

    public String getW3username() {
        return w3username;
    }

    public void setW3username(String w3username) {
        this.w3username = w3username;
    }

    public String getW3passwordbot() {
        return w3passwordbot;
    }

    public void setW3passwordbot(String w3passwordbot) {
        this.w3passwordbot = w3passwordbot;
    }

    public String getW3usernamebot() {
        return w3usernamebot;
    }

    public void setW3usernamebot(String w3usernamebot) {
        this.w3usernamebot = w3usernamebot;
    }
    
    
}

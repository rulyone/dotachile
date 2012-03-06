/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.usuarios;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import model.exceptions.BusinessLogicException;
import model.services.BasicService;

/**
 *
 * @author rulyone
 */
@ManagedBean
@RequestScoped
public class CambiarPasswordPvpgnMB {
    
    private @EJB BasicService basicService;
    
    private String newpassword;
    private String newpasswordretry;

    /** Creates a new instance of CambiarPasswordPvpgnMB */
    public CambiarPasswordPvpgnMB() {
    }
    
    public String cambiarPasswordCuentaPrincipal() {
        
        if (newpassword == null || newpasswordretry == null || !newpassword.equals(newpasswordretry)) {
            utils.Util.addErrorMessage("Passwords no coinciden.", null);
            return null;
        }
        
        try {
            basicService.cambiarPasswordCuentaW3(newpassword);
            utils.Util.addInfoMessage("Password cambiada satisfactoriamente.", null);
        } catch (BusinessLogicException ex) {
            utils.Util.addErrorMessage("Error al cambiar password.", ex.getMessage());
            this.newpassword = null;
            this.newpasswordretry = null;
            return null;
        }
        this.newpassword = null;
        this.newpasswordretry = null;
        return "/index.xhtml";
    }
    
    public String cambiarPasswordCuentaSecundaria() {
        if (newpassword == null || newpasswordretry == null || !newpassword.equals(newpasswordretry)) {
            utils.Util.addErrorMessage("Passwords no coinciden.", null);
            return null;
        }
        
        try {
            basicService.cambiarPasswordCuentaW3(newpassword);
            utils.Util.addInfoMessage("Password cambiada satisfactoriamente.", null);
        } catch (BusinessLogicException ex) {
            utils.Util.addErrorMessage("Error al cambiar password.", ex.getMessage());
            this.newpassword = null;
            this.newpasswordretry = null;
            return null;
        }
        this.newpassword = null;
        this.newpasswordretry = null;
        return "/index.xhtml";
    }

    public String getNewpassword() {
        return newpassword;
    }

    public void setNewpassword(String newpassword) {
        this.newpassword = newpassword;
    }

    public String getNewpasswordretry() {
        return newpasswordretry;
    }

    public void setNewpasswordretry(String newpasswordretry) {
        this.newpasswordretry = newpasswordretry;
    }
    
    
}

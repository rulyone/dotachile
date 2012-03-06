/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package controller.registro;

import java.io.Serializable;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import model.exceptions.BusinessLogicException;
import model.services.BasicService;
import utils.Util;

/**
 *
 * @author Pablo
 */
@ManagedBean(name="cambiarPasswordMB")
@RequestScoped
public class CambiarPasswordMB implements Serializable {

    @EJB
    private BasicService basicService;
    
    private String username;
    private String password;
    private String newpassword;

    /** Creates a new instance of CambiarPasswordMB */
    public CambiarPasswordMB() {
    }

    public String getNewpassword() {
        return newpassword;
    }

    public void setNewpassword(String newpassword) {
        this.newpassword = newpassword;
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

    public String cambiarPassword() {
        String ret = null;
        try {
            basicService.cambiarPassword(username, password, newpassword);
            Util.addInfoMessage("Password cambiada satisfactoriamente.", null);
            ret = "/index.xhtml";
        } catch (BusinessLogicException ex) {
            Util.addErrorMessage("Error al cambiar password", ex.getMessage());
            ret = "/web/registro/cambiar_password.xhtml";
        }
        return ret;
    }

}

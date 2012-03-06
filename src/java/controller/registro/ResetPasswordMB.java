/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package controller.registro;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import model.exceptions.BusinessLogicException;
import model.services.BasicService;
import javax.ejb.EJB;
import utils.Util;

/**
 *
 * @author rulyone
 */
@ManagedBean
@RequestScoped
public class ResetPasswordMB {

    @EJB private BasicService basicService;

    private String username;
    private String email;

    /** Creates a new instance of ResetPasswordMB */
    public ResetPasswordMB() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String resetPassword() {
        try {
            basicService.resetPassword(username, email);
            Util.addInfoMessage("Password reseteado.", "Debes revisar tu email para ver la nueva password.");
            return "/index.xhtml";
        } catch (BusinessLogicException ex) {
            Util.addErrorMessage("Error al resetear password", ex.getMessage());
        }
        return null;
    }

}

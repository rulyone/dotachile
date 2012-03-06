/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package controller.registro;

import java.io.Serializable;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import model.exceptions.BusinessLogicException;
import model.services.BasicService;
import utils.Util;

/**
 *
 * @author Pablo
 */
@ManagedBean(name="registrarseMB")
@RequestScoped
public class RegistrarseMB implements Serializable {

    @EJB
    private BasicService basicService;

    private String username;
    private String password;
    private String email;

    /** Creates a new instance of RegistrarseMB */
    public RegistrarseMB() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public void register(ActionEvent e) {
        try {
            basicService.register(username, password, email);
            String detalle = "Debes revisar tu correo para poder activar y utilizar tu cuenta en nuestros servicios, "
                    + "las cuentas NO activadas son eliminadas todos los dias a las 5am automáticamente. RECUERDA REVISAR TU CORREO SPAM.";
            Util.addInfoMessage("Cuenta creada satisfactoriamente.", detalle);
            this.username = null;
            this.password = null;
            this.email = null;
        } catch (BusinessLogicException ex) {
            Util.addErrorMessage(ex.getMessage(), null);
        }
    }

}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package controller.registro;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.event.ComponentSystemEvent;
import model.exceptions.BusinessLogicException;
import model.services.BasicService;
import utils.Util;

/**
 *
 * @author Pablo
 */
@ManagedBean(name="activarCuentaMB")
@RequestScoped
public class ActivarCuentaMB {

    @EJB private BasicService basicService;

    private String username;
    private String codigoActivacion;

    /** Creates a new instance of ActivarCuentaMB */
    public ActivarCuentaMB() {
    }

    public String getCodigoActivacion() {
        return codigoActivacion;
    }

    public void setCodigoActivacion(String codigoActivacion) {
        this.codigoActivacion = codigoActivacion;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void activarCuenta(ComponentSystemEvent e) {
        try {
            if(username != null && codigoActivacion != null) {
                basicService.activarCuenta(username, codigoActivacion);
                Util.addInfoMessage("Cuenta activada satisfactoriamente!", "Ya puedes hacer uso de tu cuenta en nuestro sistema. RECUERDA QUE ESTA CUENTA NO TE SIRVE PARA JUGAR EN EL SERVIDOR. PARA ESTO DEBES IR ACÁ: http://www.dotachile.com/DotaCL/web/usuarios/CreacionCuenta.jsf");
                return ;
            }
        } catch (BusinessLogicException ex) {
            Util.addErrorMessage("Error al activar la cuenta.", ex.getMessage());
            return ;
        }
        Util.addErrorMessage("Link inválido.", null);
    }

}

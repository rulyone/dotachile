/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package controller;

import java.io.Serializable;
import java.security.Principal;
import java.util.Map;
import java.util.Set;
import javax.ejb.EJB;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import model.entities.base.Usuario;
import model.entities.base.facades.UsuarioFacade;

/**
 *
 * @author Pablo
 */
@ManagedBean(name="userManagerMB")
@ApplicationScoped
public class UserManagerMB implements Serializable {

    @EJB private UsuarioFacade userFac;

    /** Creates a new instance of UserManagerMB */
    public UserManagerMB() {
    
    }
    
    public void actualizarUsuario(String username) {
        Map<Usuario,HttpSession> logins = Usuario.logins;
        Usuario u = userFac.findByUsername(username);
        if(u != null) {
            HttpSession session = logins.get(u);
            logins.remove(u);
            logins.put(u, session);
        }
    }
    
}

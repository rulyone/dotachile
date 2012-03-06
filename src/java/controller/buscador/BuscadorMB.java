/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package controller.buscador;

import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import model.entities.base.Clan;
import model.entities.base.Perfil;
import model.entities.base.Usuario;
import model.entities.base.facades.ClanFacade;
import model.entities.base.facades.PerfilFacade;
import model.entities.base.facades.UsuarioFacade;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author rulyone
 */
@ManagedBean
@ViewScoped
public class BuscadorMB implements Serializable {

    @EJB private UsuarioFacade userFac;
    @EJB private PerfilFacade perfilFac;
    @EJB private ClanFacade clanFac;

    private String username;
    private String tag;

    private Usuario selectedUser;
    private Perfil selectedPerfil;
    private Clan selectedClan;

    /** Creates a new instance of BuscadorMB */
    public BuscadorMB() {
    }

    public Perfil getSelectedPerfil() {
        return selectedPerfil;
    }

    public void setSelectedPerfil(Perfil selectedPerfil) {
        this.selectedPerfil = selectedPerfil;
    }

    public Clan getSelectedClan() {
        return selectedClan;
    }

    public void setSelectedClan(Clan selectedClan) {
        this.selectedClan = selectedClan;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Usuario getSelectedUser() {
        return selectedUser;
    }

    public void setSelectedUser(Usuario selectedUser) {
        this.selectedUser = selectedUser;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void handleSelectUsuario(SelectEvent event) {
        this.selectedUser = (Usuario) event.getObject();
        if(this.selectedUser != null) {
            this.selectedPerfil = perfilFac.findByUsername(selectedUser.getUsername());
        }
    }


    public List<Usuario> searchUsuarios(String username) {
        return userFac.searchUsuariosByUsername(username, 10);
    }

    public void handleSelectClan(SelectEvent event) {
        this.selectedClan = (Clan) event.getObject();
    }

    public List<Clan> searchClanes(String tag) {
        return clanFac.searchClanesByTag(tag, 10);
    }

}

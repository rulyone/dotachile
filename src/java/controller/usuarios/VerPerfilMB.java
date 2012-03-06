/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package controller.usuarios;

import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ViewScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ComponentSystemEvent;
import model.entities.base.Clan;
import model.entities.base.Perfil;
import model.entities.base.Usuario;
import model.entities.base.facades.ClanFacade;
import model.entities.base.facades.PerfilFacade;
import model.entities.base.facades.UsuarioFacade;
import model.exceptions.BusinessLogicException;
import model.services.BasicService;
import model.services.ComentariosService;
import utils.Util;

/**
 *
 * @author Pablo
 */
@ManagedBean(name="verPerfilMB")
@ViewScoped
public class VerPerfilMB implements Serializable {

    @EJB private UsuarioFacade userFac;
    @EJB private PerfilFacade perfilFac;
    @EJB private ComentariosService comentariosService;
    @EJB private BasicService basicService;
    @EJB private ClanFacade clanFac;

    private String username;

    private Usuario usuario;
    private Perfil perfil;
    private String comentarioPerfil;
    
    private String w3username;
    private String w3password;
    
    private List<Clan> clanesComoChieftain;

    /** Creates a new instance of VerPerfilMB */
    public VerPerfilMB() {
        this.usuario = new Usuario();
    }

    public String getComentarioPerfil() {
        return comentarioPerfil;
    }

    public void setComentarioPerfil(String comentarioPerfil) {
        this.comentarioPerfil = comentarioPerfil;
    }

    public Perfil getPerfil() {
        return perfil;
    }

    public void setPerfil(Perfil perfil) {
        this.perfil = perfil;
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

    public List<Clan> getClanesComoChieftain() {
        return clanesComoChieftain;
    }

    public void setClanesComoChieftain(List<Clan> clanesComoChieftain) {
        this.clanesComoChieftain = clanesComoChieftain;
    }

    public void loadPerfil(ComponentSystemEvent e) {
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
        Perfil p = perfilFac.findByUsername(username);
        if(p == null) {
            FacesContext ctx = FacesContext.getCurrentInstance();
            ctx.getApplication().getNavigationHandler().handleNavigation(ctx, null, "/index.xhtml?faces-redirect=true");
            return ;
        }
        this.usuario = u;
        this.perfil = p;
        this.clanesComoChieftain = clanFac.searchClanesByChieftain(usuario);
    }

    public void agregarComentarioPerfil(ActionEvent e) {
        try {
            comentariosService.agregarComentarioPerfil(perfil.getId(), comentarioPerfil);
            Util.addInfoMessage("Comentario agregado satisfactoriamente.", null);
            this.comentarioPerfil = "";
        } catch (BusinessLogicException ex) {
            Util.addErrorMessage("Error al agregar comentario.", ex.getMessage());
        }
    }

    public void modificarNickw3(ActionEvent e) {
        try {
            basicService.modificarNickW3(username, perfil.getNickw3());
            Util.addInfoMessage("Nick w3 modificado satisfactoriamente.", null);
        } catch (BusinessLogicException ex) {
            Util.addErrorMessage("Error al modificar nick w3.", ex.getMessage());
        }
    }

}

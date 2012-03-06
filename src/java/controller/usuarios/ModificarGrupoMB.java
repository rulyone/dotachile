/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package controller.usuarios;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;

import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ComponentSystemEvent;
import model.entities.base.Grupo;
import model.entities.base.Perfil;
import model.entities.base.Usuario;
import model.entities.base.facades.GrupoFacade;
import model.entities.base.facades.PerfilFacade;
import model.entities.base.facades.UsuarioFacade;
import model.exceptions.BusinessLogicException;
import model.services.AdminService;
import org.primefaces.model.DualListModel;
import utils.Util;

/**
 *
 * @author Pablo
 */
@ManagedBean(name="modificarGrupoMB")
@ViewScoped
public class ModificarGrupoMB implements Serializable {

    @EJB private UsuarioFacade userFac;
    @EJB private PerfilFacade perfilFac;
    @EJB private GrupoFacade grupoFac;
    @EJB private AdminService adminService;

    private String username;

    private Usuario usuario;
    private Perfil perfil;

    //para admins...
    private DualListModel<Grupo> grupos;

    /** Creates a new instance of EditarPerfilMB */
    public ModificarGrupoMB() {
    }

    public DualListModel<Grupo> getGrupos() {
        return grupos;
    }

    public void setGrupos(DualListModel<Grupo> grupos) {
        this.grupos = grupos;
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

    public void loadUsuario(ComponentSystemEvent e) {
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

        List<Grupo> userGroups = u.getGrupos();
        
        this.grupos = new DualListModel<Grupo>();
        this.grupos.setTarget(userGroups);
        List<Grupo> allGroups = grupoFac.findAll();
        allGroups.removeAll(userGroups);
        this.grupos.setSource(allGroups);
    }

    public void modificarGrupos(ActionEvent e) {
        try {
            List<String> groupnames = new ArrayList<String>();
            for(Grupo g : this.grupos.getTarget()) {
                groupnames.add(g.getGroupname());
            }
            this.adminService.setearGrupos(username, groupnames);
            Util.addInfoMessage("Modificacion de grupos exitosa.", null);
        } catch (BusinessLogicException ex) {
            Util.addErrorMessage("Error al modificar los grupos", ex.getMessage());
        }
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package controller.clanes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.bean.ManagedBean;
import javax.servlet.ServletContext;
import model.entities.base.Clan;
import model.entities.base.ClanBan;
import model.entities.base.Confirmacion;
import model.entities.base.Imagen;
import model.entities.base.Perfil;
import model.entities.base.facades.ClanBanFacade;
import model.entities.base.facades.ClanFacade;
import model.entities.base.facades.ConfirmacionFacade;
import model.entities.base.facades.ImagenFacade;
import model.entities.base.facades.PerfilFacade;
import model.entities.torneos.Desafio;
import model.entities.torneos.Game;
import model.entities.torneos.GameMatch;
import model.entities.torneos.facades.DesafioFacade;
import model.entities.torneos.facades.GameMatchFacade;
import model.exceptions.BusinessLogicException;
import model.services.AdminService;
import model.services.ClanService;
import model.services.ComentariosService;
import model.services.LadderService;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.UploadedFile;
import utils.Util;

/**
 *
 * @author Pablo
 */
@ManagedBean(name="verClanMB")
@ViewScoped
public class VerClanMB implements Serializable {

    @EJB private LadderService ladderService;
    @EJB private ClanFacade clanFac;
    @EJB private ComentariosService comentariosService;
    @EJB private ImagenFacade imagenFac;
    @EJB private DesafioFacade desafioFac;
    @EJB private GameMatchFacade matchFac;
    @EJB private ConfirmacionFacade confirmacionFac;
    @EJB private ClanService clanService;
    @EJB private PerfilFacade perfilFac;
    @EJB private AdminService adminService;
    @EJB private ClanBanFacade clanBanFac;

    private String tag;
    private Clan clan;
    private Confirmacion confirmacion;
    private List<Perfil> perfilesIntegrantes;

    private List<Desafio> desafios;
    private List<Game> gamesTorneos;

    private String comentarioClan;

    private String razonBan;
    private boolean clanBaneado = false;
    
    //para cambiar tag... solo admins.
    private String nuevoTag;
    private String nuevoNombre;
    /** Creates a new instance of VerClanMB */
    public VerClanMB() {
        clan = new Clan();
    }

    public String getNuevoNombre() {
        return nuevoNombre;
    }

    public void setNuevoNombre(String nuevoNombre) {
        this.nuevoNombre = nuevoNombre;
    }

    public String getNuevoTag() {
        return nuevoTag;
    }

    public void setNuevoTag(String nuevoTag) {
        this.nuevoTag = nuevoTag;
    }

    public String getRazonBan() {
        return razonBan;
    }

    public void setRazonBan(String razonBan) {
        this.razonBan = razonBan;
    }

    public List<Perfil> getPerfilesIntegrantes() {
        return perfilesIntegrantes;
    }

    public void setPerfilesIntegrantes(List<Perfil> perfilesIntegrantes) {
        this.perfilesIntegrantes = perfilesIntegrantes;
    }

    public Confirmacion getConfirmacion() {
        return confirmacion;
    }

    public void setConfirmacion(Confirmacion confirmacion) {
        this.confirmacion = confirmacion;
    }

    public List<Desafio> getDesafios() {
        return desafios;
    }

    public void setDesafios(List<Desafio> desafios) {
        this.desafios = desafios;
    }

    public List<Game> getGamesTorneos() {
        return gamesTorneos;
    }

    public void setGamesTorneos(List<Game> gamesTorneos) {
        this.gamesTorneos = gamesTorneos;
    }
    
    public String getComentarioClan() {
        return comentarioClan;
    }

    public void setComentarioClan(String comentarioClan) {
        this.comentarioClan = comentarioClan;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Clan getClan() {
        return clan;
    }

    public void setClan(Clan clan) {
        this.clan = clan;
    }

    public boolean isClanBaneado() {
        return clanBaneado;
    }

    public void setClanBaneado(boolean clanBaneado) {
        this.clanBaneado = clanBaneado;
    }

    @PostConstruct
    private void postConstruct() {        
    }
    
    public String confirmar() {
        try {
            clanService.confirmar();
            Util.addInfoMessage("Confirmación hecha!", null);
        } catch (BusinessLogicException ex) {
            Util.addErrorMessage("Error al confirmar.", ex.getMessage());
        }
        return null;
    }

    public void loadClan(ComponentSystemEvent e) {
        if(this.tag == null) {
            FacesContext ctx = FacesContext.getCurrentInstance();
            ctx.getApplication().getNavigationHandler().handleNavigation(ctx, null, "/index.xhtml?faces-redirect=true");
            return ;
        }
        Clan c = clanFac.findByTag(tag);
        if(clan == null) {
            FacesContext ctx = FacesContext.getCurrentInstance();
            ctx.getApplication().getNavigationHandler().handleNavigation(ctx, null, "/index.xhtml?faces-redirect=true");
            return ;
        }
        this.clan = c;
        this.confirmacion = confirmacionFac.findByTag(c.getTag());

        if(this.desafios == null)
            this.desafios = desafioFac.findDesafiosConfirmadosByTag(tag);
        if(this.gamesTorneos == null)
            this.gamesTorneos = matchFac.findGamesByTag(tag);
        this.perfilesIntegrantes = new ArrayList<Perfil>();
        for (int i = 0; i < clan.getIntegrantes().size(); i++) {
            Perfil p = perfilFac.findByUsername(clan.getIntegrantes().get(i).getUsername());
            this.perfilesIntegrantes.add(p);
        }
        ClanBan clanBan = clanBanFac.findByTag(tag);
        if (clanBan != null) {
            clanBaneado = true;
            razonBan = clanBan.getRazon(); 
        }
        
    }

    public void agregarComentarioClan(ActionEvent e) {
        try {
            comentariosService.agregarComentarioClan(clan.getId(), comentarioClan);
            Util.addInfoMessage("Comentario agregado satisfactoriamente.", null);
            this.comentarioClan = "";
        } catch (BusinessLogicException ex) {
            Util.addErrorMessage("Error al agregar comentario.", ex.getMessage());
        }
    }
    
    public void avatarUploadHandler(FileUploadEvent e) {
        System.out.println("SUBIENDO AVATAR");
        Principal p = FacesContext.getCurrentInstance().getExternalContext().getUserPrincipal();
        ExternalContext ext = FacesContext.getCurrentInstance().getExternalContext();
        for(int i = 0; i < clan.getIntegrantes().size(); i++) {
            if((clan.getIntegrantes().size() > 0) && clan.getChieftain().getUsername().equalsIgnoreCase(p.getName())) {
                //si puede
            }else if(ext.isUserInRole("ADMIN_ROOT") || ext.isUserInRole("ADMIN_DOTA")) {
                //si puede...
            }else{
                //no puede
                return ;
            }
        }
        UploadedFile file = e.getFile();
        ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        //String relativeURL = File.separator + "uploaded" + File.separator + "clanes" + File.separator + "avatar" + File.separator + this.clan.getTag() + "_avatar.jpg";
        String relativeURL = "/uploads/clanes/avatar/" + this.clan.getTag() + "_avatar.jpg";
        String absolutePath = "/home/dotachile/UPLOADS/clanes/avatar/" + this.clan.getTag() + "_avatar.jpg";
        FileOutputStream imageOut = null;
        try {
            imageOut = new FileOutputStream(new File(absolutePath));
            imageOut.write(file.getContents());
            if(this.clan.getAvatar() == null) {
                Imagen avatar = new Imagen();
                avatar.setRelativeUrl(relativeURL);
                imagenFac.create(avatar);
                this.clan.setAvatar(avatar);
                clanFac.edit(clan);
            }else{
                Imagen avatar = this.clan.getAvatar();
                avatar.setRelativeUrl(relativeURL);
                imagenFac.edit(avatar);
                this.clan.setAvatar(avatar);
                clanFac.edit(clan);
            }
            Util.addInfoMessage("Se ha actualizado el avatar, si no se ven los cambios actualiza la pagina (F5).", null);
        } catch (FileNotFoundException ex) {
            Util.addErrorMessage("Error al subir el archivo.", null);
            Logger.getLogger(VerClanMB.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Util.addErrorMessage("Error al subir el archivo.", null);
            Logger.getLogger(VerClanMB.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try { if(imageOut != null) imageOut.close(); } catch (IOException ex) { }
        }
        Util.keepMessages();
        Util.navigate("/web/clanes/VerClan.xhtml?faces-redirect=true&amp;tag=" + this.tag);
    }



    public String desafiarClan() {
        try {
            ladderService.desafiarClan(this.clan.getTag());
            Util.addInfoMessage("Clan desafiado satisfactoriamente.", null);
            return "/web/ladder/VerLadder.xhtml";
        } catch (BusinessLogicException ex) {
            Util.addErrorMessage("Error al desafiar clan.", ex.getMessage());
        }
        return null;
    }

    public void banearClan(ActionEvent e) {
        try {
            adminService.banearClan(tag, razonBan);
            Util.addInfoMessage("Clan baneado satisfactoriamente.", null);
            this.clanBaneado = true;
        } catch (BusinessLogicException ex) {
            Util.addErrorMessage("Error al banear al clan.", ex.getMessage());
        }
    }
    
    public void desbanearClan(ActionEvent e) {
        try {
            adminService.desbanearClan(tag);
            Util.addInfoMessage("Clan desbaneado satisfactoriamente.", null);
            this.clanBaneado = false;
        } catch (BusinessLogicException ex) {
            Util.addErrorMessage("Error al desbanear al clan.", ex.getMessage());
        }
    }
    
    public String cambiarTag() {
        try {
            clanService.cambiarTag(tag, nuevoTag);
            Util.addInfoMessage("Tag cambiado satisfactoriamente.", null);
            return "/index.xhtml";
        } catch (BusinessLogicException ex) {
            Util.addErrorMessage("Error al cambiar tag.", ex.getMessage());
        }
        return null;
    }
    
    public String cambiarNombre() {
        try {
            clanService.cambiarNombre(tag, nuevoNombre);
            Util.addInfoMessage("Nombre cambiado satisfactoriamente.", null);
            return "/index.xhtml";
        } catch (BusinessLogicException ex) {
            Util.addErrorMessage("Error al cambiar nombre.", ex.getMessage());
        }
        return null;
    }
    
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package controller.noticias;

import java.io.Serializable;
import javax.ejb.EJB;
import javax.faces.bean.ViewScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ComponentSystemEvent;

import model.entities.noticias.Noticia;
import model.entities.noticias.facades.NoticiaFacade;
import model.exceptions.BusinessLogicException;
import model.services.ComentariosService;
import model.services.NoticiasService;
import utils.Util;

/**
 *
 * @author Pablo
 */
@ManagedBean(name="verNoticiaMB")
@ViewScoped
public class VerNoticiaMB implements Serializable {
    
    @EJB private NoticiaFacade newsFac;
    @EJB private ComentariosService comentariosService;
    @EJB private NoticiasService noticiasService;

    private Long id;
    private Noticia noticia;
    private int sizeComentarios;

    private String comentarioNoticia;


    /** Creates a new instance of VerNoticiaMB */
    public VerNoticiaMB() {
        this.noticia = new Noticia();
    }

    public int getSizeComentarios() {
        return sizeComentarios;
    }

    public void setSizeComentarios(int sizeComentarios) {
        this.sizeComentarios = sizeComentarios;
    }

    public String getComentarioNoticia() {
        return comentarioNoticia;
    }

    public void setComentarioNoticia(String comentarioNoticia) {
        this.comentarioNoticia = comentarioNoticia;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Noticia getNoticia() {
        return noticia;
    }

    public void setNoticia(Noticia noticia) {
        this.noticia = noticia;
    }

    public void loadNoticia(ComponentSystemEvent e) {
        if(this.id == null) {
            FacesContext ctx = FacesContext.getCurrentInstance();
            ctx.getApplication().getNavigationHandler().handleNavigation(ctx, null, "/index.xhtml?faces-redirect=true");
            return ;
        }
        Noticia n = newsFac.find(id);
        if(n == null) {
            FacesContext ctx = FacesContext.getCurrentInstance();
            ctx.getApplication().getNavigationHandler().handleNavigation(ctx, null, "/index.xhtml?faces-redirect=true");
            return ;
        }
        this.noticia = n;
        this.sizeComentarios = newsFac.countComentariosByIdNoticia(n.getId());
    }

    public void agregarComentarioNoticia(ActionEvent e) {
        try {
            comentariosService.agregarComentarioNoticia(noticia.getId(), comentarioNoticia);
            Util.addInfoMessage("Comentario agregado satisfactoriamente.", null);
            this.comentarioNoticia = "";
        } catch (BusinessLogicException ex) {
            Util.addErrorMessage("Error al agregar comentario.", ex.getMessage());
        }
    }

    public String eliminarNoticia() {
        try {
            noticiasService.eliminarNoticia(id);
            Util.addInfoMessage("Noticia eliminada satisfactoriamente.", null);
        } catch (BusinessLogicException ex) {
            Util.addErrorMessage("Error al eliminar noticia.", ex.getMessage());
        }
        return "/index.xhtml";
    }
    
    public String aprobarNoticia() {
        try {
            noticiasService.aprobarNoticia(id);
            Util.addInfoMessage("Noticia aprobada satisfactoriamente.", null);
        } catch (BusinessLogicException ex) {
            Util.addErrorMessage("Error al aprobar noticia.", ex.getMessage());
        }
        return "/index.xhtml";
    }
}

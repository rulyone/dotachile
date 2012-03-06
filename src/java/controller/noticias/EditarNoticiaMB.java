/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package controller.noticias;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ViewScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import model.entities.noticias.Categoria;
import model.entities.noticias.Noticia;
import model.entities.noticias.facades.CategoriaFacade;
import model.entities.noticias.facades.NoticiaFacade;
import model.exceptions.BusinessLogicException;
import model.services.NoticiasService;
import utils.Util;

/**
 *
 * @author Pablo
 */
@ManagedBean(name="editarNoticiaMB")
@ViewScoped
public class EditarNoticiaMB implements Serializable {

    @EJB private NoticiasService noticiasService;
    @EJB private NoticiaFacade newsFac;
    @EJB private CategoriaFacade catFac;

    private Long idNoticia;

    private Noticia noticia;

    private String titulo;
    private String preview;
    private String contenido;
    private String categoryname;

    /** Creates a new instance of EditarNoticiaMB */
    public EditarNoticiaMB() {
    }

    public List<String> getCategoryList() {
        List<Categoria> cats = catFac.findAll();
        List<String> list = new ArrayList<String>(cats.size());
        for (Categoria c : cats) {
            list.add(c.getCategoryname());
        }
        return list;
    }

    public String getCategoryname() {
        return categoryname;
    }

    public void setCategoryname(String categoryname) {
        this.categoryname = categoryname;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public String getPreview() {
        return preview;
    }

    public void setPreview(String preview) {
        this.preview = preview;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Long getIdNoticia() {
        return idNoticia;
    }

    public void setIdNoticia(Long idNoticia) {
        this.idNoticia = idNoticia;
    }

    public Noticia getNoticia() {
        return noticia;
    }

    public void setNoticia(Noticia noticia) {
        this.noticia = noticia;
    }

    public void loadNoticia(ComponentSystemEvent e) {
        if(this.idNoticia == null) {
            FacesContext ctx = FacesContext.getCurrentInstance();
            ctx.getApplication().getNavigationHandler().handleNavigation(ctx, null, "/index.xhtml?faces-redirect=true");
            return ;
        }
        Noticia n = newsFac.find(idNoticia);
        if(n == null) {
            FacesContext ctx = FacesContext.getCurrentInstance();
            ctx.getApplication().getNavigationHandler().handleNavigation(ctx, null, "/index.xhtml?faces-redirect=true");
            return ;
        }
        this.noticia = n;
        this.titulo = n.getTitulo();
        this.preview = n.getPreview();
        this.contenido = n.getContenido();
        this.categoryname = n.getCategoria().getCategoryname();
    }

    public String editarNoticia() {
        String ret = null;
        try {
            noticiasService.editarNoticia(idNoticia, titulo, preview, contenido, categoryname);
            Util.addInfoMessage("Noticia editada satisfactoriamente", null);
            Util.keepMessages();
            ret = "/web/noticias/VerNoticia.xhtml?faces-redirect=true&amp;id=" + idNoticia;
        } catch (BusinessLogicException ex) {
            Util.addErrorMessage("Error al editar la noticia.", ex.getMessage());
        }
        return ret;
    }
}

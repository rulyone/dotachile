/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package controller.noticias;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import model.entities.noticias.Categoria;
import model.entities.noticias.facades.CategoriaFacade;
import model.entities.noticias.facades.NoticiaFacade;
import model.exceptions.BusinessLogicException;
import model.services.NoticiasService;
import utils.Util;

/**
 *
 * @author Pablo
 */
@ManagedBean(name="agregarNoticiaMB")
@RequestScoped
public class AgregarNoticiaMB implements Serializable {

    @EJB private NoticiasService noticiasService;
    @EJB private CategoriaFacade catFac;

    private String titulo;
    private String preview;
    private String contenido;
    private String categoryname;

    /** Creates a new instance of AgregarNoticiaMB */
    public AgregarNoticiaMB() {
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

    public String agregarNoticia() {
        try {
            noticiasService.agregarNoticia(titulo, preview, contenido, categoryname);
            Util.addInfoMessage("Noticia agregada satisfactoriamente!", null);
            return "/index.xhtml";
        } catch (BusinessLogicException ex) {
            Util.addErrorMessage("Error al agregar noticia", ex.getMessage());
        }
        return null;
    }

}

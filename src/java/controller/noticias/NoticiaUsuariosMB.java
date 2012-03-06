/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.noticias;

import java.io.Serializable;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import model.entities.noticias.facades.CategoriaFacade;
import model.exceptions.BusinessLogicException;
import model.services.NoticiasService;
import utils.Util;

/**
 *
 * @author rulyone
 */
@ManagedBean
@ViewScoped
public class NoticiaUsuariosMB implements Serializable {

    @EJB private NoticiasService noticiasService;

    private String titulo;
    private String preview;
    private String contenido;
    
    /**
     * Creates a new instance of NoticiaUsuariosMB
     */
    public NoticiaUsuariosMB() {
    }
    
    public String agregarNoticia() {
        try {
            noticiasService.agregarNoticiaSinEdicion(titulo, preview, contenido);
            Util.addInfoMessage("Noticia agregada satisfactoriamente!", "Debes esperar que un administrador aprueb la noticia. Es posible que incluso esta sea editada con el ánimo de mejorarla.");
            return "/index.xhtml";
        } catch (BusinessLogicException ex) {
            Util.addErrorMessage("Error al agregar noticia", ex.getMessage());
        }
        return null;
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
    
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package controller.seleccion;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import model.entities.noticias.Noticia;
import model.entities.noticias.facades.CategoriaFacade;
import model.entities.noticias.facades.NoticiaFacade;

/**
 *
 * @author rulyone
 */
@ManagedBean
@ViewScoped
public class SeleccionNoticiasMB implements Serializable {

    @EJB private NoticiaFacade newsFac;
    @EJB private CategoriaFacade catFac;

    private List<Noticia> noticias;

    //para guardar el numero de comentarios de cada noticia (y asi evitar eager load de los comentarios.)
    private Map<Long,Integer> newsMap;

    /** Creates a new instance of SeleccionNoticiasMB */
    public SeleccionNoticiasMB() {
    }

    @PostConstruct
    public void postConstruct() {

        this.noticias = newsFac.findUltimasNoticiasByCategoryname("Seleccion", 10);

        this.newsMap = new HashMap<Long,Integer>();
        for(Noticia n : this.noticias) {
            Long id = n.getId();
            int count = newsFac.countComentariosByIdNoticia(id);
            this.newsMap.put(id, count);
        }

    }

    public Map<Long, Integer> getNewsMap() {
        return newsMap;
    }

    public void setNewsMap(Map<Long, Integer> newsMap) {
        this.newsMap = newsMap;
    }

    public List<Noticia> getNoticias() {
        return noticias;
    }

    public void setNoticias(List<Noticia> noticias) {
        this.noticias = noticias;
    }

}

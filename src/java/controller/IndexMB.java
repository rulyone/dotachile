/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package controller;

import java.io.Serializable;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import model.entities.base.Clan;
import model.entities.base.Usuario;
import model.entities.base.facades.ClanFacade;
import model.entities.base.facades.UsuarioFacade;
import model.entities.noticias.Noticia;
import model.entities.noticias.facades.CategoriaFacade;
import model.entities.noticias.facades.NoticiaFacade;
import model.entities.torneos.GameMatch;
import model.entities.torneos.facades.GameMatchFacade;

/**
 *
 * @author Pablo
 */
@ManagedBean(name="indexMB")
@ViewScoped
public class IndexMB implements Serializable {

    @EJB private NoticiaFacade newsFac;
    @EJB private CategoriaFacade catFac;
    @EJB private UsuarioFacade userFac;
    @EJB private ClanFacade clanFac;
    @EJB private GameMatchFacade matchFac;
//    @EJB private ConfirmacionFacade confirmacionFac;
    
//    private List<Confirmacion> confirmaciones;

    private List<Noticia> noticias;

    private List<Clan> top10;
    
    private List<GameMatch> matchesPendientes;

    //para guardar el numero de comentarios de cada noticia (y asi evitar eager load de los comentarios.)
    private Map<Long,Integer> newsMap;

    /** Creates a new instance of IndexMB */
    public IndexMB() {
    }
    
    @PostConstruct
    public void postConstruct() {
        this.noticias = newsFac.findUltimasNoticiasByCategoryname("General", 10);

        this.newsMap = new HashMap<Long,Integer>();
        for(Noticia n : this.noticias) {
            Long id = n.getId();
            int count = newsFac.countComentariosByIdNoticia(id);
            this.newsMap.put(id, count);
        }
        
        this.top10 = clanFac.rankClanesLimit(0, 10);
                
        this.matchesPendientes = new ArrayList<GameMatch>();
        
        Principal principal = FacesContext.getCurrentInstance().getExternalContext().getUserPrincipal();
        if(principal != null) {
            Usuario user = userFac.findByUsername(principal.getName());
            if (user != null) {
                if (user.getClan() != null) {
                    this.matchesPendientes = matchFac.findMatchesPendientesByTag(user.getClan().getTag());
                }
            }
        }
        
//        this.confirmaciones = confirmacionFac.findAll();
    }

//    public List<Confirmacion> getConfirmaciones() {
//        return confirmaciones;
//    }
//
//    public void setConfirmaciones(List<Confirmacion> confirmaciones) {
//        this.confirmaciones = confirmaciones;
//    }

    public List<GameMatch> getMatchesPendientes() {
        return matchesPendientes;
    }

    public void setMatchesPendientes(List<GameMatch> matchesPendientes) {
        this.matchesPendientes = matchesPendientes;
    }

    public List<Clan> getTop10() {
        return top10;
    }

    public void setTop10(List<Clan> top10) {
        this.top10 = top10;
    }

    public List<Noticia> getNoticias() {
        return noticias;
    }

    public void setNoticias(List<Noticia> noticias) {
        this.noticias = noticias;
    }

    public Map<Long, Integer> getNewsMap() {
        return newsMap;
    }

    public void setNewsMap(Map<Long, Integer> newsMap) {
        this.newsMap = newsMap;
    }

}

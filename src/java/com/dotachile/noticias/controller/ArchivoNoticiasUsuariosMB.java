/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dotachile.noticias.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import com.dotachile.noticias.entity.Categoria;
import com.dotachile.noticias.entity.Noticia;
import com.dotachile.noticias.facade.CategoriaFacade;
import com.dotachile.noticias.facade.NoticiaFacade;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import com.dotachile.shared.Util;

/**
 *
 * @author rulyone
 */
@ManagedBean
@ViewScoped
public class ArchivoNoticiasUsuariosMB implements Serializable {
    
    @EJB private NoticiaFacade newsFac;

    private LazyDataModel<Noticia> noticias;

    /** Creates a new instance of ArchivoNoticiasMB */
    public ArchivoNoticiasUsuariosMB() {
    }

    public LazyDataModel<Noticia> getNoticias() {
        return noticias;
    }

    public void setNoticias(LazyDataModel<Noticia> noticias) {
        this.noticias = noticias;
    }
    
    @PostConstruct
    public void postConstruct() {
        
        this.noticias = new LazyDataModel<Noticia>() {

            @Override
            public List<Noticia> load(int first, int pageSize, String string, SortOrder so, Map<String, String> map) {
                //this.setRowCount(newsFac.count());
                return newsFac.findUltimasNoticiasNoAprobadas(first, pageSize);
            }
            
            @Override
            public Noticia getRowData(String rowKey) {
                return newsFac.find(Long.parseLong(rowKey));
            }

            @Override
            public Object getRowKey(Noticia noticia) {
                return noticia.getId();
            }
            
            
        };
        this.noticias.setRowCount(newsFac.countNoAprobadas());
    }

    public void onRowSelectNavigate(SelectEvent event) {
        Noticia noticia = (Noticia)event.getObject();
        Util.navigate("/web/noticias/VerNoticia.xhtml?faces-redirect=true&amp;id=" + noticia.getId());
    }
}

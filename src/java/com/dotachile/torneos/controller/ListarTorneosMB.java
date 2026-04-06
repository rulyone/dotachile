/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dotachile.torneos.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import com.dotachile.torneos.entity.Torneo;
import com.dotachile.torneos.facade.TorneoFacade;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.SelectableDataModel;
import com.dotachile.shared.Util;

/**
 *
 * @author Pablo
 */
@ManagedBean(name="listarTorneosMB")
@RequestScoped
public class ListarTorneosMB implements Serializable {

    @EJB private TorneoFacade torneoFac;

    private List<Torneo> torneos = new ArrayList<Torneo>();

    /** Creates a new instance of ListarTorneosMB */
    public ListarTorneosMB() {
    }
    
    @PostConstruct
    private void loadTorneos() {
        this.torneos.clear();
        this.torneos = torneoFac.findAllReverse();
    }

    public List<Torneo> getTorneos() {
        return torneos;
    }

    public void setTorneos(List<Torneo> torneos) {
        this.torneos = torneos;
    }
    
    public void onRowSelectNavigate(SelectEvent event) {
        Torneo torneo = (Torneo)event.getObject();
        Util.navigate("/web/torneos/VerTorneo.xhtml?faces-redirect=true&amp;idTorneo=" + torneo.getId());
    } 
}
